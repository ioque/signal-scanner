package ru.ioque.investfund.adapters.storage.jpa;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.ioque.investfund.adapters.exception.AdapterException;
import ru.ioque.investfund.adapters.storage.jpa.entity.exchange.ExchangeEntity;
import ru.ioque.investfund.adapters.storage.jpa.entity.exchange.dailyvalue.DailyValueEntity;
import ru.ioque.investfund.adapters.storage.jpa.entity.exchange.instrument.InstrumentEntity;
import ru.ioque.investfund.adapters.storage.jpa.entity.exchange.intradayvalue.IntradayValueEntity;
import ru.ioque.investfund.adapters.storage.jpa.repositories.DailyValueRepository;
import ru.ioque.investfund.adapters.storage.jpa.repositories.ExchangeEntityRepository;
import ru.ioque.investfund.adapters.storage.jpa.repositories.InstrumentEntityRepository;
import ru.ioque.investfund.adapters.storage.jpa.repositories.IntradayValueRepository;
import ru.ioque.investfund.application.adapters.ExchangeRepository;
import ru.ioque.investfund.domain.exchange.entity.Exchange;

import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Log4j2
@Component
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class JpaExchangeRepository implements ExchangeRepository {
    ExchangeEntityRepository exchangeRepository;
    InstrumentEntityRepository instrumentEntityRepository;
    DailyValueRepository dailyValueRepository;
    IntradayValueRepository intradayValueRepository;

    @Override
    @Transactional(readOnly = true)
    public Exchange get() {
        return exchangeRepository
            .findAll()
            .stream()
            .map(entity ->
                entity.toDomain(
                    instrumentEntityRepository
                        .findAll()
                        .stream()
                        .map(InstrumentEntity::toDomain)
                        .toList())
            )
            .findFirst()
            .orElseThrow(() -> new AdapterException("Данные о бирже не найдены."));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Exchange> getBy(LocalDate today) {
        return exchangeRepository
            .findAll()
            .stream()
            .findFirst()
            .map(entity ->
                entity.toDomain(
                    instrumentEntityRepository
                        .findAll()
                        .stream()
                        .map(instrumentEntity -> instrumentEntity.toDomain(
                                dailyValueRepository
                                    .findAllBy(instrumentEntity.getTicker(), today.minusMonths(6))
                                    .stream()
                                    .map(DailyValueEntity::toDomain)
                                    .toList(),
                                intradayValueRepository
                                    .findAllBy(instrumentEntity.getTicker(), today.atStartOfDay())
                                    .stream()
                                    .map(IntradayValueEntity::toDomain)
                                    .toList()
                            )
                        )
                        .toList())
            );
    }

    @Override
    @Transactional
    public void save(Exchange exchange) {
        exchangeRepository.save(ExchangeEntity.fromDomain(exchange));
        exchange
            .getInstruments()
            .stream()
            .map(instrument -> CompletableFuture.runAsync(() -> {
                instrumentEntityRepository.save(InstrumentEntity.fromDomain(instrument));
                final Long lastIntradayValueNumber = getLastNumber(instrument.getTicker());
                final Optional<LocalDate> lastDailyValueDate = getLastDate(instrument.getTicker());
                dailyValueRepository.saveAll(instrument
                    .getDailyValues()
                    .stream()
                    .filter(row -> lastDailyValueDate
                        .map(lastDate -> lastDate.isBefore(row.getTradeDate()))
                        .orElse(true))
                    .map(DailyValueEntity::fromDomain)
                    .toList());
                intradayValueRepository.saveAll(instrument
                    .getIntradayValues()
                    .stream()
                    .filter(value -> value.getNumber() > lastIntradayValueNumber)
                    .map(IntradayValueEntity::fromDomain)
                    .toList());
            }))
            .forEach(CompletableFuture::join);
    }

    private Optional<LocalDate> getLastDate(String ticker) {
        return dailyValueRepository.lastDateBy(ticker);
    }

    private Long getLastNumber(String ticker) {
        return intradayValueRepository.lastNumberBy(ticker).orElse(0L);
    }
}

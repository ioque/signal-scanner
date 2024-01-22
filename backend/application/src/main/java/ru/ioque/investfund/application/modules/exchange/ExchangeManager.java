package ru.ioque.investfund.application.modules.exchange;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.ConfigureProvider;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.ExchangeProvider;
import ru.ioque.investfund.application.adapters.ExchangeRepository;
import ru.ioque.investfund.application.adapters.UUIDProvider;
import ru.ioque.investfund.application.share.exception.ApplicationException;
import ru.ioque.investfund.application.share.logger.LoggerFacade;
import ru.ioque.investfund.domain.exchange.entity.Exchange;
import ru.ioque.investfund.domain.exchange.entity.Instrument;
import ru.ioque.investfund.domain.exchange.value.statistic.InstrumentStatistic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ExchangeManager {
    DateTimeProvider dateTimeProvider;
    ConfigureProvider configureProvider;
    ExchangeProvider exchangeProvider;
    ExchangeRepository repository;
    UUIDProvider uuidProvider;
    LoggerFacade loggerFacade;
    ExchangeCache exchangeCache;

    public void clearCache() {
        exchangeCache.clear();
    }

    public List<Instrument> getInstruments() {
        return exchangeCache.get().orElseGet(this::getExchangeFromRepo).getInstruments();
    }

    public Instrument getInstrumentBy(UUID id) {
        return exchangeCache.get().orElseGet(this::getExchangeFromRepo).getById(id);
    }

    public void enableUpdate(List<UUID> ids) {
        final Exchange exchange = exchangeCache.get().orElseGet(this::getAndCacheInit);
        exchange.enableUpdate(ids);
        repository.save(exchange);
    }

    public void disableUpdate(List<UUID> ids) {
        final Exchange exchange = exchangeCache.get().orElseGet(this::getAndCacheInit);
        exchange.disableUpdate(ids);
        repository.save(exchange);
    }

    public void integrateWithDataSource() {
        final Exchange exchange =
            repository.getBy(dateTimeProvider.nowDate()).orElseGet(this::newExchange);
        loggerFacade.logRunSynchronizeWithDataSource(exchange.getName(), dateTimeProvider.nowDateTime());
        exchangeProvider.fetchInstruments().forEach(exchange::saveInstrument);
        repository.save(exchange);
        loggerFacade.logFinishSynchronizeWithDataSource(exchange.getName(), dateTimeProvider.nowDateTime());
    }

    public void tradingDataIntegrate() {
        final Exchange exchange = exchangeCache.get().orElseGet(this::getAndCacheInit);
        exchange.getUpdatableInstruments().forEach(instrument -> {
            loggerFacade.logRunUpdateMarketData(instrument, dateTimeProvider.nowDateTime());
            exchangeProvider
                .fetchIntradayValuesBy(instrument)
                .stream()
                .filter(row -> row.getDateTime().toLocalDate().isEqual(dateTimeProvider.nowDate()))
                .forEach(instrument::addNewIntradayValue);
            exchangeProvider
                .fetchDailyTradingResultsBy(instrument)
                .forEach(instrument::addNewDailyValue);
            loggerFacade.logFinishUpdateMarketData(instrument, dateTimeProvider.nowDateTime());
        });
        repository.save(exchange);
    }

    private Exchange getAndCacheInit() {
        Exchange exchange = getExchangeFromRepo();
        exchangeCache.put(exchange);
        return exchange;
    }

    private Exchange getExchangeFromRepo() {
        return repository.getBy(dateTimeProvider.nowDate()).orElseThrow(exchangeNotFound());
    }

    private Supplier<ApplicationException> exchangeNotFound() {
        return () -> new ApplicationException("Биржа не зарегистрирована.");
    }

    private Exchange newExchange() {
        return Exchange
            .builder()
            .id(uuidProvider.generate())
            .name(configureProvider.exchangeName())
            .url(configureProvider.exchangeServerUrl())
            .description(configureProvider.exchangeDescription())
            .updatable(new HashSet<>())
            .instruments(new ArrayList<>())
            .build();
    }

    public List<InstrumentStatistic> getStatistics() {
        return exchangeCache.get()
            .orElseGet(this::getExchangeFromRepo)
            .getInstruments()
            .stream()
            .filter(row -> !row.getDailyValues().isEmpty() && !row.getIntradayValues().isEmpty())
            .map(Instrument::calcStatistic)
            .toList();
    }
}

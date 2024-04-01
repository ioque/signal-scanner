package ru.ioque.investfund.application.modules.exchange;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.EventBus;
import ru.ioque.investfund.application.adapters.ExchangeProvider;
import ru.ioque.investfund.application.adapters.ExchangeRepository;
import ru.ioque.investfund.application.adapters.UUIDProvider;
import ru.ioque.investfund.application.modules.SystemModule;
import ru.ioque.investfund.application.share.exception.ApplicationException;
import ru.ioque.investfund.application.share.logger.LoggerFacade;
import ru.ioque.investfund.domain.exchange.entity.Exchange;
import ru.ioque.investfund.domain.exchange.event.TradingDataUpdatedEvent;
import ru.ioque.investfund.domain.exchange.value.IntradayValue;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ExchangeManager implements SystemModule {
    DateTimeProvider dateTimeProvider;
    ExchangeProvider exchangeProvider;
    ExchangeRepository repository;
    UUIDProvider uuidProvider;
    LoggerFacade loggerFacade;
    EventBus eventBus;

    @Override
    public synchronized void execute() {
        integrateTradingData();
    }

    public synchronized void enableUpdate(List<UUID> ids) {
        final Exchange exchange = getExchangeFromRepo();
        exchange.enableUpdate(ids);
        repository.save(exchange);
    }

    public synchronized void disableUpdate(List<UUID> ids) {
        final Exchange exchange = getExchangeFromRepo();
        exchange.disableUpdate(ids);
        repository.save(exchange);
    }

    public synchronized void integrateWithDataSource() {
        final Exchange exchange = getExchangeFromRepo();
        loggerFacade.logRunSynchronizeWithDataSource(exchange.getName(), dateTimeProvider.nowDateTime());
        exchangeProvider.fetchInstruments().forEach(exchange::saveInstrument);
        repository.save(exchange);
        loggerFacade.logFinishSynchronizeWithDataSource(exchange.getName(), dateTimeProvider.nowDateTime());
    }

    public synchronized void integrateTradingData() {
        final Exchange exchange = getExchangeFromRepo();
        exchange.getUpdatableInstruments().forEach(instrument -> {
            loggerFacade.logRunUpdateMarketData(instrument, dateTimeProvider.nowDateTime());

            exchangeProvider
                .fetchIntradayValuesBy(instrument.getTicker(), instrument.lastIntradayValue().map(IntradayValue::getNumber).orElse(0L))
                .stream()
                .filter(row -> row.isSameByDate(dateTimeProvider.nowDate()))
                .forEach(instrument::addNewIntradayValue);

            if (instrument.isNeedUpdateHistory(dateTimeProvider.nowDate())) {
                exchangeProvider
                    .fetchHistoryBy(
                        instrument.getTicker(),
                        instrument.lastHistoryValueDate().orElse(dateTimeProvider.monthsAgo(6)),
                        dateTimeProvider.nowDate().minusDays(1)
                    )
                    .forEach(instrument::addNewDailyValue);
            }

            loggerFacade.logFinishUpdateMarketData(instrument, dateTimeProvider.nowDateTime());

        });

        repository.save(exchange);

        eventBus.publish(new TradingDataUpdatedEvent(uuidProvider.generate(), dateTimeProvider.nowDateTime()));
    }

    private Exchange getExchangeFromRepo() {
        return repository
            .getAllBy(dateTimeProvider.nowDate())
            .stream()
            .findFirst()
            .orElseThrow(exchangeNotFound());
    }

    private Supplier<ApplicationException> exchangeNotFound() {
        return () -> new ApplicationException("Биржа не зарегистрирована.");
    }

    public void registerDatasource(AddDatasourceCommand command) {
        var exchange = Exchange
            .builder()
            .id(uuidProvider.generate())
            .name(command.getName())
            .url(command.getUrl())
            .description(command.getDescription())
            .instruments(new ArrayList<>())
            .build();
        repository.save(exchange);
    }
}

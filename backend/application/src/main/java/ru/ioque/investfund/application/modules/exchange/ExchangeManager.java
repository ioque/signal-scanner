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
        if (repository.getBy(dateTimeProvider.nowDate()).isPresent()) {
            integrateTradingData();
        }
    }

    public synchronized void registerDatasource(AddDatasourceCommand command) {
        repository.save(command.factory(uuidProvider.generate()));
    }

    public synchronized void unregisterDatasource() {
        repository.delete();
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

    public synchronized void integrateInstruments() {
        final Exchange exchange = getExchangeFromRepo();
        loggerFacade.logRunSynchronizeWithDataSource(exchange.getName(), dateTimeProvider.nowDateTime());
        exchangeProvider.fetchInstruments(exchange.getUrl()).forEach(exchange::saveInstrument);
        repository.save(exchange);
        loggerFacade.logFinishSynchronizeWithDataSource(exchange.getName(), dateTimeProvider.nowDateTime());
    }

    private void integrateTradingData() {
        final Exchange exchange = getExchangeFromRepo();
        exchange
            .getUpdatableInstruments()
            .forEach(instrument -> {
                loggerFacade.logRunUpdateMarketData(instrument, dateTimeProvider.nowDateTime());
                instrument.addIntradayValues(
                    exchangeProvider
                        .fetchIntradayValuesBy(
                            exchange.getUrl(),
                            instrument.getTicker(),
                            instrument.lastIntradayNumber().orElse(0L)
                        )
                );
                if (instrument.isNeedUpdateHistory(dateTimeProvider.nowDate())) {
                    instrument.addHistoryValues(
                        exchangeProvider
                            .fetchHistoryBy(
                                exchange.getUrl(),
                                instrument.getTicker(),
                                instrument.lastHistoryValueDate().orElse(dateTimeProvider.monthsAgo(6)),
                                dateTimeProvider.nowDate().minusDays(1)
                            )
                    );
                }
                loggerFacade.logFinishUpdateMarketData(instrument, dateTimeProvider.nowDateTime());
            });

        repository.save(exchange);

        eventBus.publish(new TradingDataUpdatedEvent(uuidProvider.generate(), dateTimeProvider.nowDateTime()));
    }

    private Exchange getExchangeFromRepo() {
        return repository
            .getBy(dateTimeProvider.nowDate())
            .orElseThrow(exchangeNotFound());
    }

    private Supplier<ApplicationException> exchangeNotFound() {
        return () -> new ApplicationException("Биржа не зарегистрирована.");
    }
}

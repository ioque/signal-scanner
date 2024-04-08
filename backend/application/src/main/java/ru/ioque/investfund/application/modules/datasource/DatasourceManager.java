package ru.ioque.investfund.application.modules.datasource;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DatasourceRepository;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.EventBus;
import ru.ioque.investfund.application.adapters.DatasourceProvider;
import ru.ioque.investfund.application.adapters.UUIDProvider;
import ru.ioque.investfund.application.modules.SystemModule;
import ru.ioque.investfund.application.share.exception.ApplicationException;
import ru.ioque.investfund.application.share.logger.LoggerFacade;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.event.TradingDataUpdatedEvent;
import ru.ioque.investfund.domain.datasource.value.HistoryValue;
import ru.ioque.investfund.domain.datasource.value.IntradayValue;

import java.util.List;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DatasourceManager implements SystemModule {
    DateTimeProvider dateTimeProvider;
    DatasourceProvider datasourceProvider;
    DatasourceRepository repository;
    UUIDProvider uuidProvider;
    LoggerFacade loggerFacade;
    EventBus eventBus;

    @Override
    public synchronized void execute() {
        if (repository.get().isPresent()) {
            integrateTradingData();
        }
    }

    public synchronized void registerDatasource(AddDatasourceCommand command) {
        repository.saveDatasource(command.factory(uuidProvider.generate()));
    }

    public synchronized void unregisterDatasource() {
        repository.deleteDatasource();
    }

    public synchronized void enableUpdate(List<String> tickers) {
        final Datasource datasource = getExchangeFromRepo();
        datasource.enableUpdate(tickers);
        repository.saveDatasource(datasource);
    }

    public synchronized void disableUpdate(List<String> tickers) {
        final Datasource datasource = getExchangeFromRepo();
        datasource.disableUpdate(tickers);
        repository.saveDatasource(datasource);
    }

    public synchronized void integrateInstruments() {
        final Datasource datasource = getExchangeFromRepo();
        loggerFacade.logRunSynchronizeWithDataSource(datasource.getName(), dateTimeProvider.nowDateTime());
        datasourceProvider.fetchInstruments(datasource).forEach(datasource::saveInstrument);
        repository.saveDatasource(datasource);
        loggerFacade.logFinishSynchronizeWithDataSource(datasource.getName(), dateTimeProvider.nowDateTime());
    }

    public synchronized void integrateTradingData() {
        Datasource datasource = getExchangeFromRepo();
        datasource.getUpdatableInstruments().forEach(instrument -> {
            loggerFacade.logRunUpdateMarketData(instrument, dateTimeProvider.nowDateTime());
            List<HistoryValue> history = datasourceProvider
                .fetchHistoryBy(datasource, instrument)
                .stream()
                .distinct()
                .toList();
            List<IntradayValue> intraday = datasourceProvider
                .fetchIntradayValuesBy(datasource, instrument)
                .stream()
                .distinct()
                .toList();
            instrument.updateLastTradingNumber(intraday);
            instrument.updateLastHistoryDate(history);
            repository.saveHistoryValues(history);
            repository.saveIntradayValues(intraday);
            loggerFacade.logFinishUpdateMarketData(instrument, dateTimeProvider.nowDateTime());
        });
        repository.saveDatasource(datasource);
        eventBus.publish(new TradingDataUpdatedEvent(uuidProvider.generate(), dateTimeProvider.nowDateTime()));
    }

    private Datasource getExchangeFromRepo() {
        return repository.get().orElseThrow(exchangeNotFound());
    }

    private Supplier<ApplicationException> exchangeNotFound() {
        return () -> new ApplicationException("Биржа не зарегистрирована.");
    }
}

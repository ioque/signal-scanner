package ru.ioque.investfund.application.modules.datasource;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DatasourceProvider;
import ru.ioque.investfund.application.adapters.DatasourceRepository;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.EventPublisher;
import ru.ioque.investfund.application.adapters.UUIDProvider;
import ru.ioque.investfund.application.share.logger.LoggerFacade;
import ru.ioque.investfund.domain.core.Command;
import ru.ioque.investfund.domain.datasource.command.CreateDatasourceCommand;
import ru.ioque.investfund.domain.datasource.command.DisableUpdateInstrumentsCommand;
import ru.ioque.investfund.domain.datasource.command.EnableUpdateInstrumentsCommand;
import ru.ioque.investfund.domain.datasource.command.IntegrateInstrumentsCommand;
import ru.ioque.investfund.domain.datasource.command.IntegrateTradingDataCommand;
import ru.ioque.investfund.domain.datasource.command.UnregisterDatasourceCommand;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.event.TradingDataUpdatedEvent;
import ru.ioque.investfund.domain.datasource.value.HistoryValue;
import ru.ioque.investfund.domain.datasource.value.IntradayValue;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DatasourceManager {
    Validator validator;
    DateTimeProvider dateTimeProvider;
    DatasourceProvider datasourceProvider;
    DatasourceRepository repository;
    UUIDProvider uuidProvider;
    LoggerFacade loggerFacade;
    EventPublisher eventPublisher;

    public synchronized void registerDatasource(final CreateDatasourceCommand command) {
        validateCommand(command);
        repository.saveDatasource(Datasource.from(uuidProvider.generate(), command));
    }

    public synchronized void unregisterDatasource(final UnregisterDatasourceCommand command) {
        validateCommand(command);
        repository.deleteDatasource(command.getDatasourceId());
    }

    public synchronized void enableUpdate(final EnableUpdateInstrumentsCommand command) {
        validateCommand(command);
        final Datasource datasource = getExchangeFromRepo(command.getDatasourceId());
        datasource.enableUpdate(command.getTickers());
        repository.saveDatasource(datasource);
    }

    public synchronized void disableUpdate(final DisableUpdateInstrumentsCommand command) {
        validateCommand(command);
        final Datasource datasource = getExchangeFromRepo(command.getDatasourceId());
        datasource.disableUpdate(command.getTickers());
        repository.saveDatasource(datasource);
    }

    public synchronized void integrateInstruments(final IntegrateInstrumentsCommand command) {
        validateCommand(command);
        final Datasource datasource = getExchangeFromRepo(command.getDatasourceId());
        loggerFacade.logRunSynchronizeWithDataSource(datasource.getName(), dateTimeProvider.nowDateTime());
        datasourceProvider.fetchInstruments(datasource).forEach(datasource::addInstrument);
        repository.saveDatasource(datasource);
        loggerFacade.logFinishSynchronizeWithDataSource(datasource.getName(), dateTimeProvider.nowDateTime());
    }

    public synchronized void integrateTradingData(final IntegrateTradingDataCommand command) {
        validateCommand(command);
        final Datasource datasource = getExchangeFromRepo(command.getDatasourceId());
        datasource.getUpdatableInstruments().forEach(instrument -> {
            loggerFacade.logRunUpdateMarketData(instrument, dateTimeProvider.nowDateTime());
            final List<HistoryValue> history = datasourceProvider
                .fetchHistoryBy(datasource, instrument)
                .stream()
                .distinct()
                .toList();
            final List<IntradayValue> intraday = datasourceProvider
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
        eventPublisher.publish(TradingDataUpdatedEvent.builder()
            .id(uuidProvider.generate())
            .dateTime(dateTimeProvider.nowDateTime())
            .datasourceId(datasource.getId())
            .build());
    }

    private Datasource getExchangeFromRepo(final UUID datasourceId) {
        return repository
            .getBy(datasourceId)
            .orElseThrow(
                () -> new IllegalArgumentException(
                    String.format("Источник данных[id=%s] не существует.", datasourceId)
                )
            );
    }

    private void validateCommand(Command command) {
        Set<ConstraintViolation<Command>> violations = validator.validate(command);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}

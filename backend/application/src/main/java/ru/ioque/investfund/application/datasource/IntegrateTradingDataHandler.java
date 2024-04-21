package ru.ioque.investfund.application.datasource;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.CommandHandler;
import ru.ioque.investfund.application.adapters.DatasourceProvider;
import ru.ioque.investfund.application.adapters.DatasourceRepository;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.EventPublisher;
import ru.ioque.investfund.application.adapters.IntradayValueRepository;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.adapters.UUIDProvider;
import ru.ioque.investfund.application.datasource.dto.HistoryBatch;
import ru.ioque.investfund.application.datasource.dto.IntradayBatch;
import ru.ioque.investfund.application.integration.event.DomainEventWrapper;
import ru.ioque.investfund.application.integration.event.TradingDataIntegratedEvent;
import ru.ioque.investfund.domain.datasource.command.IntegrateTradingDataCommand;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.value.AggregateHistory;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayValue;

import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class IntegrateTradingDataHandler extends CommandHandler<IntegrateTradingDataCommand> {
    UUIDProvider uuidProvider;
    DatasourceProvider datasourceProvider;
    DatasourceRepository datasourceRepository;
    IntradayValueRepository intradayValueRepository;
    EventPublisher eventPublisher;

    public IntegrateTradingDataHandler(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerProvider loggerProvider,
        UUIDProvider uuidProvider,
        DatasourceProvider datasourceProvider,
        DatasourceRepository datasourceRepository,
        IntradayValueRepository intradayValueRepository,
        EventPublisher eventPublisher
    ) {
        super(dateTimeProvider, validator, loggerProvider);
        this.uuidProvider = uuidProvider;
        this.datasourceProvider = datasourceProvider;
        this.datasourceRepository = datasourceRepository;
        this.intradayValueRepository = intradayValueRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    protected void businessProcess(IntegrateTradingDataCommand command) {
        final Datasource datasource = datasourceRepository.getById(command.getDatasourceId());
        final List<DomainEventWrapper> events = datasource
            .getUpdatableInstruments()
            .stream()
            .map(instrument -> integrateTradingDataFor(instrument, datasource))
            .flatMap(Collection::stream)
            .toList();
        datasourceRepository.save(datasource);
        events.forEach(eventPublisher::publish);
        eventPublisher.publish(TradingDataIntegratedEvent.builder()
            .id(uuidProvider.generate())
            .datasourceId(datasource.getId().getUuid())
            .dateTime(dateTimeProvider.nowDateTime())
            .updatedCount(datasource.getUpdatableInstruments().size())
            .build());
    }

    private List<DomainEventWrapper> integrateTradingDataFor(Instrument instrument, Datasource datasource) {
        final TreeSet<IntradayValue> intradayValues = getIntradayValues(instrument, datasource);
        final TreeSet<AggregateHistory> aggregateHistories = getAggregateHistories(instrument, datasource);
        intradayValueRepository.saveAll(intradayValues);
        instrument.updateTradingState(intradayValues);
        instrument.updateAggregateHistory(aggregateHistories);
        return instrument
            .getEvents()
            .stream()
            .map(event -> DomainEventWrapper.of(
                uuidProvider.generate(),
                event,
                dateTimeProvider.nowDateTime()
            )).toList();
    }

    private TreeSet<IntradayValue> getIntradayValues(Instrument instrument, Datasource datasource) {
        final IntradayBatch intradayBatch = datasourceProvider.fetchIntradayValues(
            datasource,
            instrument
        );
        validate(intradayBatch);
        return intradayBatch.getIntradayValues(validator);
    }

    private TreeSet<AggregateHistory> getAggregateHistories(Instrument instrument, Datasource datasource) {
        final HistoryBatch historyBatch = datasourceProvider.fetchAggregateHistory(
            datasource,
            instrument
        );
        validate(historyBatch);
        return historyBatch.getAggregateHistory(validator);
    }
}

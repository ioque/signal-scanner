package ru.ioque.investfund.application.modules.datasource.handler;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DatasourceProvider;
import ru.ioque.investfund.application.adapters.DatasourceRepository;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.EventJournal;
import ru.ioque.investfund.application.adapters.journal.IntradayJournal;
import ru.ioque.investfund.application.adapters.IntradayValueRepository;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.integration.event.TradingDataIntegrated;
import ru.ioque.investfund.application.integration.event.TradingStateChanged;
import ru.ioque.investfund.application.modules.api.CommandHandler;
import ru.ioque.investfund.application.modules.api.Result;
import ru.ioque.investfund.application.modules.datasource.command.PublishIntradayData;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.value.TradingState;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;

import java.util.TreeSet;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PublishIntradayDataHandler extends CommandHandler<PublishIntradayData> {
    DatasourceProvider datasourceProvider;
    IntradayJournal intradayJournal;
    DatasourceRepository datasourceRepository;
    IntradayValueRepository intradayValueRepository;
    EventJournal eventJournal;

    public PublishIntradayDataHandler(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerProvider loggerProvider,
        DatasourceProvider datasourceProvider,
        IntradayJournal intradayJournal,
        DatasourceRepository datasourceRepository,
        IntradayValueRepository intradayValueRepository,
        EventJournal eventJournal
    ) {
        super(dateTimeProvider, validator, loggerProvider);
        this.datasourceProvider = datasourceProvider;
        this.intradayJournal = intradayJournal;
        this.datasourceRepository = datasourceRepository;
        this.intradayValueRepository = intradayValueRepository;
        this.eventJournal = eventJournal;
    }

    @Override
    protected Result businessProcess(PublishIntradayData command) {
        final Datasource datasource = datasourceRepository.getBy(command.getDatasourceId());
        for (Instrument instrument : datasource.getUpdatableInstruments()) {
            integrateTradingDataFor(instrument, datasource);
        }
        datasourceRepository.save(datasource);
        eventJournal.publish(TradingDataIntegrated.builder()
            .datasourceId(datasource.getId().getUuid())
            .createdAt(dateTimeProvider.nowDateTime())
            .build());
        return Result.success();
    }

    private void integrateTradingDataFor(Instrument instrument, Datasource datasource) {
        final TreeSet<IntradayData> intradayData =
            new TreeSet<>(
                datasourceProvider.fetchIntradayValues(datasource, instrument)
                    .stream()
                    .filter(data -> data.getNumber() > instrument.getLastTradingNumber())
                    .toList()
            );
        intradayData.forEach(intradayJournal::publish);
        intradayValueRepository.saveAll(intradayData);
        if (instrument.updateTradingState(intradayData)) {
            eventJournal.publish(
                TradingStateChanged.builder()
                    .instrumentId(instrument.getId().getUuid())
                    .price(instrument.getTradingState().map(TradingState::getTodayLastPrice).orElse(null))
                    .value(instrument.getTradingState().map(TradingState::getTodayValue).orElse(null))
                    .createdAt(dateTimeProvider.nowDateTime())
                    .build()
            );
        }
    }
}

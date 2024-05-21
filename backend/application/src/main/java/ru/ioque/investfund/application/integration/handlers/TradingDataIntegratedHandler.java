package ru.ioque.investfund.application.integration.handlers;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.CommandJournal;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.integration.EventHandler;
import ru.ioque.investfund.application.integration.event.TradingDataIntegrated;
import ru.ioque.investfund.application.modules.scanner.command.ProduceSignal;
import ru.ioque.investfund.domain.datasource.entity.identity.DatasourceId;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TradingDataIntegratedHandler extends EventHandler<TradingDataIntegrated> {
    CommandJournal commandJournal;

    public TradingDataIntegratedHandler(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerProvider loggerProvider,
        CommandJournal commandJournal
    ) {
        super(dateTimeProvider, validator, loggerProvider);
        this.commandJournal = commandJournal;
    }

    @Override
    public void handle(TradingDataIntegrated event) {
        commandJournal.publish(
            ProduceSignal.builder()
                .datasourceId(DatasourceId.from(event.getDatasourceId()))
                .watermark(event.getCreatedAt())
                .build()
        );
    }
}

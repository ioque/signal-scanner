package ru.ioque.investfund.application.integration;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.CommandPublisher;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.integration.event.TradingDataIntegrated;
import ru.ioque.investfund.application.scanner.command.ProduceSignalCommand;
import ru.ioque.investfund.domain.datasource.entity.identity.DatasourceId;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TradingDataIntegratedEventHandler extends EventHandler<TradingDataIntegrated> {
    CommandPublisher commandPublisher;

    public TradingDataIntegratedEventHandler(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerProvider loggerProvider,
        CommandPublisher commandPublisher
    ) {
        super(dateTimeProvider, validator, loggerProvider);
        this.commandPublisher = commandPublisher;
    }

    @Override
    public void handle(TradingDataIntegrated event) {
        commandPublisher.publish(
            ProduceSignalCommand.builder()
                .datasourceId(DatasourceId.from(event.getDatasourceId()))
                .watermark(event.getCreatedAt())
                .build()
        );
    }
}

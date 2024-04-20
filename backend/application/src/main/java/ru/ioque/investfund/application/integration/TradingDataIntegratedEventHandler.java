package ru.ioque.investfund.application.integration;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.CommandPublisher;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.integration.event.TradingDataIntegratedEvent;
import ru.ioque.investfund.domain.datasource.entity.identity.DatasourceId;
import ru.ioque.investfund.domain.scanner.command.ProduceSignalCommand;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TradingDataIntegratedEventHandler extends EventHandler<TradingDataIntegratedEvent> {
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
    public void handle(TradingDataIntegratedEvent event) {
        commandPublisher.publish(
            ProduceSignalCommand.builder()
                .datasourceId(DatasourceId.from(event.getDatasourceId()))
                .watermark(event.getDateTime())
                .build()
        );
    }
}

package ru.ioque.investfund.application.event.handlers;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.CommandPublisher;
import ru.ioque.investfund.application.event.EventHandler;
import ru.ioque.investfund.domain.datasource.event.TradingDataIntegratedEvent;
import ru.ioque.investfund.domain.scanner.command.ProduceSignalCommand;

@Component
@AllArgsConstructor
public class TradingDataIntegratedHandler extends EventHandler<TradingDataIntegratedEvent> {
    CommandPublisher commandPublisher;

    @Override
    public void handle(TradingDataIntegratedEvent event) {
        commandPublisher.publish(
            ProduceSignalCommand.builder()
                .datasourceId(event.getDatasourceId())
                .watermark(event.getDateTime())
                .build()
        );
    }
}

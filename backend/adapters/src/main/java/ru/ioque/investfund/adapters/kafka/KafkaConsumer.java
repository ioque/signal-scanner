package ru.ioque.investfund.adapters.kafka;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import ru.ioque.investfund.application.modules.CommandBus;
import ru.ioque.investfund.application.modules.telegrambot.TelegramBotService;
import ru.ioque.investfund.domain.core.DomainEvent;
import ru.ioque.investfund.domain.datasource.event.TradingDataUpdatedEvent;
import ru.ioque.investfund.domain.scanner.command.ProduceSignalCommand;
import ru.ioque.investfund.domain.scanner.event.SignalEvent;

@Slf4j
@Service
@AllArgsConstructor
@Profile("!tests")
public class KafkaConsumer {
    private final TelegramBotService telegramBotService;
    private final CommandBus commandBus;

    @KafkaListener(topics = "events")
    public void process(@Payload DomainEvent event) {
        if (event instanceof TradingDataUpdatedEvent tradingDataUpdatedEvent) {
            commandBus.execute(
                ProduceSignalCommand.builder()
                    .datasourceId(tradingDataUpdatedEvent.getDatasourceId())
                    .watermark(tradingDataUpdatedEvent.getDateTime())
                    .build()
            );
        }
        if (event instanceof SignalEvent signalEvent) {
            telegramBotService.sendToAllChats(signalEvent.toString());
        }
    }
}

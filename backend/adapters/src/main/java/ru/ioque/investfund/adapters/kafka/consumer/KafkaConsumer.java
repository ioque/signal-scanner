package ru.ioque.investfund.adapters.kafka.consumer;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import ru.ioque.investfund.application.adapters.CommandJournal;
import ru.ioque.investfund.application.modules.api.CommandBus;
import ru.ioque.investfund.application.modules.api.Command;
import ru.ioque.investfund.application.modules.risk.command.CloseEmulatedPosition;
import ru.ioque.investfund.application.modules.risk.command.OpenEmulatedPosition;
import ru.ioque.investfund.application.modules.scanner.processor.StreamingScannerEngine;
import ru.ioque.investfund.application.modules.telegrambot.command.PublishSignal;
import ru.ioque.investfund.domain.datasource.value.InstrumentPerformance;
import ru.ioque.investfund.domain.scanner.entity.Signal;

import static ru.ioque.investfund.adapters.kafka.config.TopicConfiguration.COMMAND_TOPIC;
import static ru.ioque.investfund.adapters.kafka.config.TopicConfiguration.INTRADAY_STATISTIC_TOPIC;
import static ru.ioque.investfund.adapters.kafka.config.TopicConfiguration.SIGNAL_TOPIC;

@Slf4j
@Service
@AllArgsConstructor
@Profile("!tests")
public class KafkaConsumer {
    private final CommandBus commandBus;
    private final CommandJournal commandJournal;
    private final StreamingScannerEngine streamingScannerEngine;

    @KafkaListener(topics = COMMAND_TOPIC)
    public void processCommand(@Payload Command command) {
        commandBus.execute(command);
    }

    @KafkaListener(topics = INTRADAY_STATISTIC_TOPIC)
    public void process(@Payload InstrumentPerformance statistics) {
        streamingScannerEngine.process(statistics);
    }

    @KafkaListener(topics = SIGNAL_TOPIC)
    public void processSignal(@Payload Signal signal) {
        commandJournal.publish(
            PublishSignal.builder()
                .isBuy(signal.isBuy())
                .scannerId(signal.getScannerId())
                .instrumentId(signal.getInstrumentId())
                .build()
        );
        if (signal.isBuy()) {
            commandJournal.publish(
                OpenEmulatedPosition.builder()
                    .price(signal.getPrice())
                    .scannerId(signal.getScannerId())
                    .instrumentId(signal.getInstrumentId())
                    .build()
            );
        } else {
            commandJournal.publish(
                CloseEmulatedPosition.builder()
                    .price(signal.getPrice())
                    .scannerId(signal.getScannerId())
                    .instrumentId(signal.getInstrumentId())
                    .build()
            );
        }
    }
}

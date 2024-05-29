package ru.ioque.investfund.adapters.kafka.consumer;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import ru.ioque.investfund.application.modules.api.CommandBus;
import ru.ioque.investfund.application.modules.api.Command;
import ru.ioque.investfund.application.modules.risk.command.CloseEmulatedPosition;
import ru.ioque.investfund.application.modules.risk.command.OpenEmulatedPosition;
import ru.ioque.investfund.application.modules.scanner.processor.StreamingScannerEngine;
import ru.ioque.investfund.application.modules.telegrambot.command.PublishSignal;
import ru.ioque.investfund.domain.scanner.value.IntradayPerformance;
import ru.ioque.investfund.domain.scanner.entity.Signal;

import static ru.ioque.investfund.adapters.kafka.config.TopicConfiguration.INTRADAY_STATISTIC_TOPIC;
import static ru.ioque.investfund.adapters.kafka.config.TopicConfiguration.SIGNAL_TOPIC;

@Slf4j
@Service
@AllArgsConstructor
@Profile("!tests")
public class KafkaConsumer {
    private final CommandBus commandBus;
    private final StreamingScannerEngine streamingScannerEngine;

    @KafkaListener(topics = INTRADAY_STATISTIC_TOPIC)
    public void process(@Payload IntradayPerformance statistics) {
        streamingScannerEngine.process(statistics);
    }

    @KafkaListener(topics = SIGNAL_TOPIC)
    public void processSignal(@Payload Signal signal) {
        commandBus.execute(
            PublishSignal.builder()
                .signal(signal)
                .build()
        );
        if (signal.isBuy()) {
            commandBus.execute(
                OpenEmulatedPosition.builder()
                    .price(signal.getPrice())
                    .scannerId(signal.getScannerId())
                    .instrumentId(signal.getInstrumentId())
                    .build()
            );
        } else {
            commandBus.execute(
                CloseEmulatedPosition.builder()
                    .price(signal.getPrice())
                    .scannerId(signal.getScannerId())
                    .instrumentId(signal.getInstrumentId())
                    .build()
            );
        }
    }
}

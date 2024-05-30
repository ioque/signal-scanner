package ru.ioque.investfund.application.modules.pipeline.processor;

import java.text.DecimalFormat;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.TelegramMessageSender;
import ru.ioque.investfund.application.adapters.repository.TelegramChatRepository;
import ru.ioque.investfund.application.modules.pipeline.PipelineContext;
import ru.ioque.investfund.application.modules.pipeline.core.Processor;
import ru.ioque.investfund.domain.scanner.entity.Signal;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;
import ru.ioque.investfund.domain.scanner.value.InstrumentPerformance;

@Component
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TelegramPublisher implements Processor<Signal> {
    PipelineContext pipelineContext;
    TelegramMessageSender sender;
    TelegramChatRepository telegramChatRepository;

    @Override
    public void process(Signal signal) {
        final DecimalFormat decimalFormat = new DecimalFormat("#.##");
        final InstrumentPerformance performance = pipelineContext.getInstrumentPerformance(signal.getInstrumentId());
        final SignalScanner scanner = pipelineContext.getScanner(signal.getScannerId());
        final Double closePrice = performance.getAggregatedHistories().last().getClosePrice();
        final Double signalPricePerClosePrice = (signal.getPrice() / closePrice - 1) * 100;
        telegramChatRepository.findAll().forEach(chat -> sender.sendMessage(
            chat.getChatId(),
            String.format(
                """
                    #%s
                    Зафиксирован сигнал к %s, алгоритм "%s"
                                                    
                    %s
                    Изменение цены относительно цены закрытия предыдущего дня %s
                    """,
                performance.getTicker(),
                signal.isBuy() ? "покупке" : "продаже",
                scanner.getProperties().getType().getName(),
                signal.getSummary(),
                decimalFormat.format(signalPricePerClosePrice) + "%")
        ));
    }
}

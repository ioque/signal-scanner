package ru.ioque.investfund.application.modules.telegrambot.handler;

import java.text.DecimalFormat;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.repository.InstrumentRepository;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.adapters.repository.ScannerRepository;
import ru.ioque.investfund.application.adapters.repository.TelegramChatRepository;
import ru.ioque.investfund.application.adapters.TelegramMessageSender;
import ru.ioque.investfund.application.adapters.journal.AggregatedTotalsJournal;
import ru.ioque.investfund.application.modules.api.CommandHandler;
import ru.ioque.investfund.application.modules.api.Result;
import ru.ioque.investfund.application.modules.telegrambot.command.PublishSignal;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.value.history.AggregatedTotals;
import ru.ioque.investfund.domain.scanner.entity.Signal;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PublishSignalHandler extends CommandHandler<PublishSignal> {

    AggregatedTotalsJournal aggregatedTotalsJournal;
    ScannerRepository scannerRepository;
    InstrumentRepository instrumentRepository;
    TelegramChatRepository telegramChatRepository;
    TelegramMessageSender telegramMessageSender;

    public PublishSignalHandler(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerProvider loggerProvider,
        AggregatedTotalsJournal aggregatedTotalsJournal,
        ScannerRepository scannerRepository,
        InstrumentRepository instrumentRepository,
        TelegramChatRepository telegramChatRepository,
        TelegramMessageSender telegramMessageSender
    ) {
        super(dateTimeProvider, validator, loggerProvider);
        this.aggregatedTotalsJournal = aggregatedTotalsJournal;
        this.scannerRepository = scannerRepository;
        this.instrumentRepository = instrumentRepository;
        this.telegramChatRepository = telegramChatRepository;
        this.telegramMessageSender = telegramMessageSender;
    }

    @Override
    protected Result businessProcess(PublishSignal command) {
        final DecimalFormat decimalFormat = new DecimalFormat("#.##");
        final Signal signal = command.getSignal();
        final Instrument instrument = instrumentRepository.getBy(signal.getInstrumentId());
        final SignalScanner scanner = scannerRepository.getBy(signal.getScannerId());
        final AggregatedTotals aggregatedTotals = aggregatedTotalsJournal.findActualBy(instrument.getTicker()).orElseThrow();
        telegramChatRepository
            .findAll()
            .forEach(chat -> telegramMessageSender.sendMessage(
                    chat.getChatId(),
                    String.format(
                        """
                            #%s
                            Зафиксирован сигнал к %s, алгоритм "%s"
                                                            
                            %s
                            Изменение цены относительно цены закрытия предыдущего дня %s
                            """,
                        instrument.getTicker(),
                        signal.isBuy() ? "покупке" : "продаже",
                        scanner.getProperties().getType().getName(),
                        signal.getSummary(),
                        decimalFormat.format((signal.getPrice() / aggregatedTotals.getClosePrice() - 1) * 100) + "%")
                )
            );
        return Result.success();
    }
}

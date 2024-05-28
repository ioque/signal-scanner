package ru.ioque.investfund.application.modules.telegrambot;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.InstrumentRepository;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.adapters.ScannerRepository;
import ru.ioque.investfund.application.adapters.TelegramChatRepository;
import ru.ioque.investfund.application.adapters.TelegramMessageSender;
import ru.ioque.investfund.application.modules.api.CommandHandler;
import ru.ioque.investfund.application.modules.api.Result;
import ru.ioque.investfund.application.modules.telegrambot.command.PublishSignal;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.scanner.entity.Signal;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;

import java.text.DecimalFormat;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PublishSignalHandler extends CommandHandler<PublishSignal> {
    ScannerRepository scannerRepository;
    InstrumentRepository instrumentRepository;
    TelegramChatRepository telegramChatRepository;
    TelegramMessageSender telegramMessageSender;

    public PublishSignalHandler(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerProvider loggerProvider,
        ScannerRepository scannerRepository,
        InstrumentRepository instrumentRepository,
        TelegramChatRepository telegramChatRepository,
        TelegramMessageSender telegramMessageSender
    ) {
        super(dateTimeProvider, validator, loggerProvider);
        this.scannerRepository = scannerRepository;
        this.instrumentRepository = instrumentRepository;
        this.telegramChatRepository = telegramChatRepository;
        this.telegramMessageSender = telegramMessageSender;
    }

    @Override
    protected Result businessProcess(PublishSignal command) {
        final DecimalFormat decimalFormat = new DecimalFormat("#.##");
        final SignalScanner scanner = scannerRepository.getBy(command.getScannerId());
        final Instrument instrument = instrumentRepository.getBy(command.getInstrumentId());
        final Signal signal = scanner.getSignals()
            .stream()
            .filter(row -> row.getInstrumentId().equals(command.getInstrumentId()))
            .findFirst()
            .orElseThrow();
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
                    instrument
                        .getPrevClosePrice()
                        .map(closePrice ->
                            decimalFormat.format((signal.getPrice() / closePrice - 1) * 100) + "%")
                        .orElse("0%")
                )
            ));
        return Result.success();
    }
}

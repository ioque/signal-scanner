package ru.ioque.investfund.application.modules.telegrambot.handler;

import java.text.DecimalFormat;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.adapters.TelegramMessageSender;
import ru.ioque.investfund.application.adapters.repository.SignalRepository;
import ru.ioque.investfund.application.adapters.repository.AggregatedTotalsRepository;
import ru.ioque.investfund.application.adapters.repository.InstrumentRepository;
import ru.ioque.investfund.application.adapters.repository.ScannerRepository;
import ru.ioque.investfund.application.adapters.repository.TelegramChatRepository;
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
    TelegramChatRepository telegramChatRepository;
    SignalRepository signalRepository;
    AggregatedTotalsRepository aggregatedTotalsRepository;
    ScannerRepository scannerRepository;
    InstrumentRepository instrumentRepository;
    TelegramMessageSender sender;

    public PublishSignalHandler(DateTimeProvider dateTimeProvider,
        Validator validator, LoggerProvider loggerProvider,
        TelegramChatRepository telegramChatRepository, SignalRepository signalRepository,
        AggregatedTotalsRepository aggregatedTotalsRepository, ScannerRepository scannerRepository,
        InstrumentRepository instrumentRepository, TelegramMessageSender sender) {
        super(dateTimeProvider, validator, loggerProvider);
        this.telegramChatRepository = telegramChatRepository;
        this.signalRepository = signalRepository;
        this.aggregatedTotalsRepository = aggregatedTotalsRepository;
        this.scannerRepository = scannerRepository;
        this.instrumentRepository = instrumentRepository;
        this.sender = sender;
    }

    @Override
    protected Result businessProcess(PublishSignal command) {
        final DecimalFormat decimalFormat = new DecimalFormat("#.##");
        final Signal signal = signalRepository
            .findById(command.getSignalId())
            .orElseThrow();
        final AggregatedTotals aggregatedTotals = aggregatedTotalsRepository
            .findActualBy(signal.getInstrumentId())
            .orElseThrow();
        final SignalScanner scanner = scannerRepository.getBy(signal.getScannerId());
        final Instrument instrument = instrumentRepository.getBy(signal.getInstrumentId());
        final Double signalPricePerClosePrice = (signal.getOpenPrice() / aggregatedTotals.getClosePrice() - 1) * 100;
        telegramChatRepository.findAll().forEach(chat -> sender.sendMessage(
            chat.getChatId(),
            String.format(
                """
                    #%s
                    Зафиксирован сигнал, алгоритм "%s"

                    %s
                    Изменение цены относительно цены закрытия предыдущего дня %s
                    """,
                instrument.getTicker(),
                scanner.getProperties().getType().getName(),
                signal.getSummary(),
                decimalFormat.format(signalPricePerClosePrice) + "%")
        ));
        return Result.success();
    }
}

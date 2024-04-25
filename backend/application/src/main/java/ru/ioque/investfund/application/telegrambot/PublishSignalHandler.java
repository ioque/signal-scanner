package ru.ioque.investfund.application.telegrambot;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.adapters.ScannerRepository;
import ru.ioque.investfund.application.adapters.TelegramChatRepository;
import ru.ioque.investfund.application.adapters.TelegramMessageSender;
import ru.ioque.investfund.application.adapters.TradingSnapshotsRepository;
import ru.ioque.investfund.application.adapters.UUIDProvider;
import ru.ioque.investfund.application.api.command.CommandHandler;
import ru.ioque.investfund.application.telegrambot.command.PublishSignal;
import ru.ioque.investfund.domain.core.InfoLog;
import ru.ioque.investfund.domain.scanner.entity.Signal;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;
import ru.ioque.investfund.domain.scanner.value.TradingSnapshot;

import java.text.DecimalFormat;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PublishSignalHandler extends CommandHandler<PublishSignal> {
    ScannerRepository scannerRepository;
    TradingSnapshotsRepository tradingSnapshotsRepository;
    TelegramChatRepository telegramChatRepository;
    TelegramMessageSender telegramMessageSender;

    public PublishSignalHandler(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerProvider loggerProvider,
        UUIDProvider uuidProvider,
        ScannerRepository scannerRepository,
        TradingSnapshotsRepository tradingSnapshotsRepository,
        TelegramChatRepository telegramChatRepository,
        TelegramMessageSender telegramMessageSender
    ) {
        super(dateTimeProvider, validator, loggerProvider, uuidProvider);
        this.scannerRepository = scannerRepository;
        this.tradingSnapshotsRepository = tradingSnapshotsRepository;
        this.telegramChatRepository = telegramChatRepository;
        this.telegramMessageSender = telegramMessageSender;
    }

    @Override
    protected void businessProcess(PublishSignal command) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        SignalScanner scanner = scannerRepository.getBy(command.getScannerId());
        TradingSnapshot tradingSnapshot = tradingSnapshotsRepository.getBy(command.getInstrumentId());
        Signal signal = scanner.getSignals()
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
                    tradingSnapshot.getTicker(),
                    signal.isBuy() ? "покупке" : "продаже",
                    scanner.getProperties().getType().getName(),
                    signal.getSummary(),
                    tradingSnapshot
                        .getPrevClosePrice()
                        .map(closePrice ->
                            decimalFormat.format((signal.getPrice() / closePrice - 1) * 100) + "%")
                        .orElse("0%")
                )
            ));
        loggerProvider.log(new InfoLog(
            dateTimeProvider.nowDateTime(),
            String.format("Выполнена публикация сигнала[scannerId=%s, instrumentId=%s]", command.getScannerId(), command.getInstrumentId()),
            command.getTrack()
        ));
    }
}

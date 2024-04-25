package ru.ioque.investfund.application.telegrambot;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.adapters.ReportService;
import ru.ioque.investfund.application.adapters.TelegramChatRepository;
import ru.ioque.investfund.application.adapters.TelegramMessageSender;
import ru.ioque.investfund.application.adapters.UUIDProvider;
import ru.ioque.investfund.application.api.command.CommandHandler;
import ru.ioque.investfund.application.telegrambot.command.PublishDailyReport;
import ru.ioque.investfund.domain.core.InfoLog;

import java.io.File;
import java.io.IOException;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PublishDailyReportHandler extends CommandHandler<PublishDailyReport> {
    ReportService reportService;
    TelegramChatRepository telegramChatRepository;
    TelegramMessageSender telegramMessageSender;

    public PublishDailyReportHandler(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerProvider loggerProvider,
        UUIDProvider uuidProvider,
        ReportService reportService,
        TelegramChatRepository telegramChatRepository,
        TelegramMessageSender telegramMessageSender
    ) {
        super(dateTimeProvider, validator, loggerProvider, uuidProvider);
        this.reportService = reportService;
        this.telegramChatRepository = telegramChatRepository;
        this.telegramMessageSender = telegramMessageSender;
    }

    @Override
    protected void businessProcess(PublishDailyReport command) {
        try {
            File report = reportService.buildDailyReport();
            loggerProvider.log(new InfoLog(
                dateTimeProvider.nowDateTime(),
                "Сгенерирован ежедневный отчет",
                command.getTrack()
            ));
            if (command.getChatId() != null) {
                telegramMessageSender.sendMessage(
                    command.getChatId(),
                    "Ежедневный отчет",
                    report
                );
                return;
            }
            telegramChatRepository.findAll().forEach(telegramChat -> telegramMessageSender.sendMessage(
                telegramChat.getChatId(),
                "Ежедневный отчет",
                report
            ));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

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
import ru.ioque.investfund.application.api.command.CommandHandler;
import ru.ioque.investfund.application.telegrambot.command.PublishHourlyReport;
import ru.ioque.investfund.domain.core.ApplicationLog;
import ru.ioque.investfund.domain.core.InfoLog;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PublishHourlyReportHandler extends CommandHandler<PublishHourlyReport> {
    ReportService reportService;
    TelegramMessageSender telegramMessageSender;
    TelegramChatRepository telegramChatRepository;

    public PublishHourlyReportHandler(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerProvider loggerProvider,
        TelegramMessageSender telegramMessageSender,
        TelegramChatRepository telegramChatRepository,
        ReportService reportService
    ) {
        super(dateTimeProvider, validator, loggerProvider);
        this.reportService = reportService;
        this.telegramMessageSender = telegramMessageSender;
        this.telegramChatRepository = telegramChatRepository;
    }

    @Override
    protected List<ApplicationLog> businessProcess(PublishHourlyReport command) {
        try {
            File report = reportService.buildHourlyReport();
            if (command.getChatId() != null) {
                telegramMessageSender.sendMessage(
                    command.getChatId(),
                    "Ежечасный отчет",
                    report
                );
            } else {
                telegramChatRepository.findAll().forEach(telegramChat -> telegramMessageSender.sendMessage(
                    telegramChat.getChatId(),
                    "Ежечасный отчет",
                    report
                ));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return List.of(
            new InfoLog(
                dateTimeProvider.nowDateTime(),
                "Сгенерирован ежечасный отчет"
            )
        );
    }
}
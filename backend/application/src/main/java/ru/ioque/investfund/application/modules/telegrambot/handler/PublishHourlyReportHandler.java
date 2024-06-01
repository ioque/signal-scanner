package ru.ioque.investfund.application.modules.telegrambot.handler;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.repository.EmulatedPositionRepository;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.adapters.ReportService;
import ru.ioque.investfund.application.adapters.repository.TelegramChatRepository;
import ru.ioque.investfund.application.adapters.TelegramMessageSender;
import ru.ioque.investfund.application.modules.api.CommandHandler;
import ru.ioque.investfund.application.modules.api.Result;
import ru.ioque.investfund.application.modules.telegrambot.command.PublishHourlyReport;

import java.io.File;
import java.io.IOException;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PublishHourlyReportHandler extends CommandHandler<PublishHourlyReport> {
    ReportService reportService;
    EmulatedPositionRepository emulatedPositionRepository;
    TelegramMessageSender telegramMessageSender;
    TelegramChatRepository telegramChatRepository;

    public PublishHourlyReportHandler(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerProvider loggerProvider,
        TelegramMessageSender telegramMessageSender,
        TelegramChatRepository telegramChatRepository,
        ReportService reportService,
        EmulatedPositionRepository emulatedPositionRepository
    ) {
        super(dateTimeProvider, validator, loggerProvider);
        this.reportService = reportService;
        this.telegramMessageSender = telegramMessageSender;
        this.telegramChatRepository = telegramChatRepository;
        this.emulatedPositionRepository = emulatedPositionRepository;
    }

    @Override
    protected Result businessProcess(PublishHourlyReport command) {
        try {
            final File report = reportService.buildHourlyReport();
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
            return Result.error(new RuntimeException(e.getMessage()));
        }
        return Result.success();
    }
}

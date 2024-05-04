package ru.ioque.investfund.application.modules.telegrambot;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.EmulatedPositionRepository;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.adapters.ReportService;
import ru.ioque.investfund.application.adapters.TelegramChatRepository;
import ru.ioque.investfund.application.adapters.TelegramMessageSender;
import ru.ioque.investfund.application.modules.api.CommandHandler;
import ru.ioque.investfund.application.modules.api.Result;
import ru.ioque.investfund.application.modules.telegrambot.command.PublishDailyReport;
import ru.ioque.investfund.domain.core.InfoLog;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PublishDailyReportHandler extends CommandHandler<PublishDailyReport> {
    ReportService reportService;
    EmulatedPositionRepository emulatedPositionRepository;
    TelegramChatRepository telegramChatRepository;
    TelegramMessageSender telegramMessageSender;

    public PublishDailyReportHandler(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerProvider loggerProvider,
        ReportService reportService,
        EmulatedPositionRepository emulatedPositionRepository,
        TelegramChatRepository telegramChatRepository,
        TelegramMessageSender telegramMessageSender
    ) {
        super(dateTimeProvider, validator, loggerProvider);
        this.reportService = reportService;
        this.emulatedPositionRepository = emulatedPositionRepository;
        this.telegramChatRepository = telegramChatRepository;
        this.telegramMessageSender = telegramMessageSender;
    }

    @Override
    protected Result businessProcess(PublishDailyReport command) {
        try {
            if (!emulatedPositionRepository.existsOpenPositions()) {
                return Result.success(List.of(new InfoLog(dateTimeProvider.nowDateTime(), "Нет открытых позиций.")));
            }
            final File report = reportService.buildDailyReport();
            if (command.getChatId() != null) {
                telegramMessageSender.sendMessage(
                    command.getChatId(),
                    "Ежедневный отчет",
                    report
                );
            } else {
                telegramChatRepository.findAll().forEach(telegramChat -> telegramMessageSender.sendMessage(
                    telegramChat.getChatId(),
                    "Ежедневный отчет",
                    report
                ));
            }
        } catch (IOException e) {
            return Result.error(new RuntimeException(e.getMessage()));
        }
        return Result.success();
    }
}

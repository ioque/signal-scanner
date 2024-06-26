package ru.ioque.investfund.adapters.telegrambot;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.ioque.investfund.application.modules.api.CommandBus;
import ru.ioque.investfund.application.modules.telegrambot.command.PublishDailyReport;
import ru.ioque.investfund.application.modules.telegrambot.command.PublishHourlyReport;
import ru.ioque.investfund.application.modules.telegrambot.command.Subscribe;
import ru.ioque.investfund.application.modules.telegrambot.command.Unsubscribe;

@Slf4j
@Component
@Profile("!tests")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SignalTelegramBot extends TelegramBot {
    CommandBus commandBus;

    public SignalTelegramBot(
        @Value("${telegram-bot.token}") String botToken,
        CommandBus commandBus
    ) {
        super(botToken);
        this.commandBus = commandBus;
    }

    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            switch (update.getMessage().getText()) {
                case "/start":
                case "/subscribe":
                    commandBus.execute(
                        new Subscribe(
                            update.getMessage().getChatId(),
                            update.getMessage().getChat().getFirstName()
                        )
                    );
                    break;
                case "/unsubscribe":
                    commandBus.execute(new Unsubscribe(update.getMessage().getChatId()));
                    break;
                case "/hourly_report":
                    commandBus.execute(new PublishHourlyReport(update.getMessage().getChatId()));
                    break;
                case "/daily_report":
                    commandBus.execute(new PublishDailyReport(update.getMessage().getChatId()));
                    break;
            }
        }
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }
}

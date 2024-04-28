package ru.ioque.investfund.adapters.telegrambot;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.ioque.investfund.application.adapters.CommandPublisher;
import ru.ioque.investfund.application.telegrambot.command.PublishDailyReport;
import ru.ioque.investfund.application.telegrambot.command.PublishHourlyReport;
import ru.ioque.investfund.application.telegrambot.command.Subscribe;
import ru.ioque.investfund.application.telegrambot.command.Unsubscribe;

@Slf4j
@Component
@Profile("!tests")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SignalTelegramBot extends TelegramBot {
    CommandPublisher commandPublisher;

    public SignalTelegramBot(
        @Value("${telegram-bot.token}") String botToken,
        CommandPublisher commandPublisher
    ) {
        super(botToken);
        this.commandPublisher = commandPublisher;
    }

    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            switch (update.getMessage().getText()) {
                case "/start":
                case "/subscribe":
                    commandPublisher.publish(
                        new Subscribe(
                            update.getMessage().getChatId(),
                            update.getMessage().getChat().getFirstName()
                        )
                    );
                    break;
                case "/unsubscribe":
                    commandPublisher.publish(new Unsubscribe(update.getMessage().getChatId()));
                    break;
                case "/hourly_report":
                    commandPublisher.publish(new PublishHourlyReport(update.getMessage().getChatId()));
                    break;
                case "/daily_report":
                    commandPublisher.publish(new PublishDailyReport(update.getMessage().getChatId()));
                    break;
            }
        }
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }
}

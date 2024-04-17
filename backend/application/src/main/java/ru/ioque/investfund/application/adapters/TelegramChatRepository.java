package ru.ioque.investfund.application.adapters;

import ru.ioque.investfund.domain.telegrambot.TelegramChat;

import java.util.List;
import java.util.Optional;

public interface TelegramChatRepository {
    void save(TelegramChat telegramChat);
    Optional<TelegramChat> findBy(Long chatId);
    List<TelegramChat> findAll();
    void removeBy(long chatId);
}

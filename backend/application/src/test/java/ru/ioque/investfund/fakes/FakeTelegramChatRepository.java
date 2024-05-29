package ru.ioque.investfund.fakes;

import ru.ioque.investfund.application.adapters.repository.TelegramChatRepository;
import ru.ioque.investfund.domain.telegrambot.TelegramChat;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class FakeTelegramChatRepository implements TelegramChatRepository {
    Map<Long, TelegramChat> telegramChats = new ConcurrentHashMap<>();

    @Override
    public void save(TelegramChat telegramChat) {
        telegramChats.put(telegramChat.getChatId(), telegramChat);
    }

    @Override
    public Optional<TelegramChat> findBy(Long chatId) {
        return Optional.ofNullable(telegramChats.get(chatId));
    }

    @Override
    public List<TelegramChat> findAll() {
        return telegramChats.values().stream().toList();
    }

    @Override
    public void removeBy(long chatId) {
        telegramChats.remove(chatId);
    }
}

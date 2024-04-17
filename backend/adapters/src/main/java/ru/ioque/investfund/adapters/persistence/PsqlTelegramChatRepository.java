package ru.ioque.investfund.adapters.persistence;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.adapters.persistence.entity.telegrambot.TelegramChatEntity;
import ru.ioque.investfund.adapters.persistence.repositories.JpaTelegramChatRepository;
import ru.ioque.investfund.application.adapters.TelegramChatRepository;
import ru.ioque.investfund.domain.telegrambot.TelegramChat;

import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PsqlTelegramChatRepository implements TelegramChatRepository {
    JpaTelegramChatRepository jpaTelegramChatRepository;

    @Override
    public void save(TelegramChat telegramChat) {
        jpaTelegramChatRepository.save(new TelegramChatEntity(telegramChat.getChatId(), telegramChat.getCreatedAt()));
    }

    @Override
    public Optional<TelegramChat> findBy(Long chatId) {
        return jpaTelegramChatRepository.findById(chatId).map(entity -> new TelegramChat(
            entity.getChatId(),
            entity.getCreatedAt()
        ));
    }

    @Override
    public List<TelegramChat> findAll() {
        return jpaTelegramChatRepository
            .findAll()
            .stream()
            .map(entity -> new TelegramChat(entity.getChatId(), entity.getCreatedAt()))
            .toList();
    }

    @Override
    public void removeBy(long chatId) {
        jpaTelegramChatRepository.deleteById(chatId);
    }
}

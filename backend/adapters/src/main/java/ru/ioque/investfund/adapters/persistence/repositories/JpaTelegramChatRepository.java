package ru.ioque.investfund.adapters.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ioque.investfund.adapters.persistence.entity.telegrambot.TelegramChatEntity;

@Repository
public interface JpaTelegramChatRepository extends JpaRepository<TelegramChatEntity, Long> {
}
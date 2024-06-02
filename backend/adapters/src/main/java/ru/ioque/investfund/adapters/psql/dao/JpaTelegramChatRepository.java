package ru.ioque.investfund.adapters.psql.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ioque.investfund.adapters.psql.entity.telegrambot.TelegramChatEntity;

@Repository
public interface JpaTelegramChatRepository extends JpaRepository<TelegramChatEntity, Long> {
}

package ru.ioque.investfund.adapters.persistence.entity.telegrambot;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@Table(name = "telegram_chat")
@Entity(name = "TelegramChatEntity")
public class TelegramChatEntity {
    @Id
    @Column(unique = true, nullable = false)
    Long chatId;
    LocalDateTime createdAt;
}

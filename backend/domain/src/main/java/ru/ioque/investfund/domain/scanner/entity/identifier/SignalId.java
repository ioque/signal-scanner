package ru.ioque.investfund.domain.scanner.entity.identifier;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SignalId {
    @NotNull(message = "Не указан uuid")
    UUID uuid;

    public static SignalId from(UUID id) {
        return new SignalId(id);
    }

    @Override
    public String toString() {
        return uuid.toString();
    }
}

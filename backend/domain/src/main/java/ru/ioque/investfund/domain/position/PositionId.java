package ru.ioque.investfund.domain.position;

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
public class PositionId {
    @NotNull(message = "Передан пустой идентификатор.")
    UUID uuid;

    public static PositionId from(UUID uuid) {
        return new PositionId(uuid);
    }

    @Override
    public String toString() {
        return uuid.toString();
    }
}

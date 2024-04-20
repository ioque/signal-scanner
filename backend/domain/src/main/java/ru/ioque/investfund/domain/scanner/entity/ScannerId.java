package ru.ioque.investfund.domain.scanner.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScannerId {
    UUID uuid;

    public static ScannerId from(UUID id) {
        return new ScannerId(id);
    }

    @Override
    public String toString() {
        return uuid.toString();
    }
}

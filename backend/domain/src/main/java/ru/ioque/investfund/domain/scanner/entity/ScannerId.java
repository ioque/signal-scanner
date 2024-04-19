package ru.ioque.investfund.domain.scanner.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
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

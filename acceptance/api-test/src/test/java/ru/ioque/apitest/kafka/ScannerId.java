package ru.ioque.apitest.kafka;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class ScannerId {
    UUID uuid;

    @Override
    public String toString() {
        return uuid.toString();
    }
}

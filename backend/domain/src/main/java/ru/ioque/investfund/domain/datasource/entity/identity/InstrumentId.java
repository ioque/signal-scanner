package ru.ioque.investfund.domain.datasource.entity.identity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InstrumentId {
    UUID uuid;

    public static InstrumentId from(UUID uuid) {
        return new InstrumentId(uuid);
    }

    @Override
    public String toString() {
        return uuid.toString();
    }
}

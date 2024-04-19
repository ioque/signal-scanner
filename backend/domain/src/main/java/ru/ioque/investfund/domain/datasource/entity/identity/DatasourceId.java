package ru.ioque.investfund.domain.datasource.entity.identity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DatasourceId {
    UUID uuid;

    public static DatasourceId from(UUID uuid) {
        return new DatasourceId(uuid);
    }

    @Override
    public String toString() {
        return uuid.toString();
    }
}

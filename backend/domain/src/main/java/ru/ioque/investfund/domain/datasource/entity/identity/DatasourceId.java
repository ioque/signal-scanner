package ru.ioque.investfund.domain.datasource.entity.identity;

import jakarta.validation.constraints.NotNull;
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
public class DatasourceId {
    @NotNull(message = "Передан пустой идентификатор.")
    UUID uuid;

    public static DatasourceId from(UUID uuid) {
        return new DatasourceId(uuid);
    }

    @Override
    public String toString() {
        return uuid.toString();
    }
}

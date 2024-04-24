package ru.ioque.investfund.domain.risk;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.datasource.entity.identity.DatasourceId;

import java.util.UUID;

@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmulatedPositionId {
    UUID uuid;

    public static EmulatedPositionId from(UUID uuid) {
        return new EmulatedPositionId(uuid);
    }

    @Override
    public String toString() {
        return uuid.toString();
    }
}

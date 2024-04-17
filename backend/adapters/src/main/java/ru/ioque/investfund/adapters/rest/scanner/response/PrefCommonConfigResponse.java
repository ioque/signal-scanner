package ru.ioque.investfund.adapters.rest.scanner.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.adapters.persistence.entity.scanner.PrefSimpleScannerEntity;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PrefCommonConfigResponse extends SignalConfigResponse {
    Double spreadParam;

    public static SignalConfigResponse from(PrefSimpleScannerEntity scanner) {
        return new PrefCommonConfigResponse(scanner.getSpreadParam());
    }
}

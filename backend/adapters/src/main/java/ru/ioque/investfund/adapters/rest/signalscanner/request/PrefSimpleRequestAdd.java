package ru.ioque.investfund.adapters.rest.signalscanner.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.scanner.algorithms.PrefSimpleSignalConfig;
import ru.ioque.investfund.domain.scanner.entity.SignalConfig;

import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PrefSimpleRequestAdd extends AddSignalScannerRequest {
    Double spreadParam;

    @Builder
    public PrefSimpleRequestAdd(String description, List<UUID> ids, Double spreadParam) {
        super(description, ids);
        this.spreadParam = spreadParam;
    }


    @Override
    public SignalConfig buildConfig() {
        return new PrefSimpleSignalConfig(getIds(), spreadParam);
    }
}

package ru.ioque.investfund.adapters.rest.signalscanner.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.scanner.financial.algorithms.PrefSimpleSignalConfig;
import ru.ioque.investfund.domain.scanner.financial.entity.SignalConfig;

import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PrefSimpleRequest extends SignalScannerRequest {
    Double spreadParam;

    @Builder
    public PrefSimpleRequest(String description, List<UUID> ids, Double spreadParam) {
        super(description, ids);
        this.spreadParam = spreadParam;
    }


    @Override
    public SignalConfig buildConfig() {
        return new PrefSimpleSignalConfig(spreadParam);
    }
}

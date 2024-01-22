package ru.ioque.acceptance.client.signalscanner.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PrefSimpleScannerConfigRequest extends DataScannerConfigRequest {
    Double spreadParam;

    @Builder
    public PrefSimpleScannerConfigRequest(
        List<UUID> ids,
        UUID marketIndexId,
        Double spreadParam
    ) {
        super(ids, marketIndexId);
        this.spreadParam = spreadParam;
    }
}

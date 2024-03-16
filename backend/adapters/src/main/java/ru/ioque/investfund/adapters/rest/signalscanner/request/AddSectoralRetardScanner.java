package ru.ioque.investfund.adapters.rest.signalscanner.request;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.scanner.entity.sectoralretard.SectoralRetardSignalConfig;
import ru.ioque.investfund.domain.scanner.entity.SignalConfig;

import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddSectoralRetardScanner extends AddSignalScannerRequest {
    @NotNull(message = "The historyScale is required.")
    Double historyScale;
    @NotNull(message = "The intradayScale is required.")
    Double intradayScale;

    @Builder
    public AddSectoralRetardScanner(
        String description,
        List<UUID> ids,
        Double historyScale,
        Double intradayScale
    ) {
        super(description, ids);
        this.historyScale = historyScale;
        this.intradayScale = intradayScale;
    }

    @Override
    public SignalConfig buildConfig() {
        return new SectoralRetardSignalConfig(getIds(), historyScale, intradayScale);
    }
}

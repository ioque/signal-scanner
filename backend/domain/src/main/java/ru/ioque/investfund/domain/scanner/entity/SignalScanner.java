package ru.ioque.investfund.domain.scanner.entity;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.core.Domain;
import ru.ioque.investfund.domain.datasource.entity.identity.DatasourceId;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.scanner.algorithms.core.AlgorithmFactory;
import ru.ioque.investfund.domain.scanner.algorithms.core.ScannerAlgorithm;
import ru.ioque.investfund.domain.scanner.algorithms.properties.AlgorithmProperties;
import ru.ioque.investfund.domain.scanner.entity.identifier.ScannerId;
import ru.ioque.investfund.domain.scanner.value.InstrumentTradingState;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SignalScanner extends Domain<ScannerId> {

    String description;
    ScannerStatus status;
    final DatasourceId datasourceId;
    List<InstrumentId> instrumentIds;
    Integer workPeriodInMinutes;
    ScannerAlgorithm algorithm;
    AlgorithmProperties properties;

    @Builder
    public SignalScanner(
        ScannerId id,
        Integer workPeriodInMinutes,
        String description,
        ScannerStatus status,
        DatasourceId datasourceId,
        AlgorithmProperties properties,
        List<InstrumentId> instrumentIds
    ) {
        super(id);
        this.status = status;
        this.workPeriodInMinutes = workPeriodInMinutes;
        this.description = description;
        this.datasourceId = datasourceId;
        this.properties = properties;
        this.instrumentIds = instrumentIds;
        this.algorithm = AlgorithmFactory.factoryBy(properties);
    }

    public synchronized List<Signal> scanning(List<InstrumentTradingState> instruments, LocalDateTime watermark) {
        return algorithm.findSignals(instruments, watermark)
            .stream()
            .peek(signal -> signal.setScannerId(getId()))
            .toList();
    }

    public void updateWorkPeriod(Integer workPeriodInMinutes) {
        this.workPeriodInMinutes = workPeriodInMinutes;
    }

    public void updateInstrumentIds(List<InstrumentId> instrumentIds) {
        this.instrumentIds = instrumentIds;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void updateProperties(AlgorithmProperties properties) {
        if (!properties.getType().equals(getProperties().getType())) {
            throw new IllegalArgumentException("Невозможно изменить тип алгоритма.");
        }
        this.properties = properties;
    }

    public boolean isActive() {
        return status.equals(ScannerStatus.ACTIVE);
    }

    public void activate() {
        this.status = ScannerStatus.ACTIVE;
    }

    public void deactivate() {
        this.status = ScannerStatus.INACTIVE;
    }
}

package ru.ioque.investfund.adapters.persistence.entity.scanner;

import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.adapters.persistence.entity.AbstractEntity;
import ru.ioque.investfund.domain.configurator.entity.AlgorithmConfig;
import ru.ioque.investfund.domain.configurator.entity.ScannerConfig;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;
import ru.ioque.investfund.domain.scanner.value.TradingSnapshot;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@Table(name = "scanner")
@Entity(name = "ScannerEntity")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "SCANNER_TYPE", discriminatorType = DiscriminatorType.STRING, columnDefinition = "varchar(255)")
public abstract class ScannerEntity extends AbstractEntity {
    Integer workPeriodInMinutes;
    String description;
    UUID datasourceId;
    @ElementCollection(fetch = FetchType.EAGER)
    List<String> tickers;
    LocalDateTime lastExecutionDateTime;
    @OneToMany(mappedBy = "scanner", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    List<SignalEntity> signals;

    public ScannerEntity(
        UUID id,
        Integer workPeriodInMinutes,
        String description,
        UUID datasourceId,
        List<String> tickers,
        LocalDateTime lastExecutionDateTime,
        List<SignalEntity> signals
    ) {
        super(id);
        this.workPeriodInMinutes = workPeriodInMinutes;
        this.description = description;
        this.datasourceId = datasourceId;
        this.tickers = tickers != null ? new ArrayList<>(tickers) : new ArrayList<>();
        this.lastExecutionDateTime = lastExecutionDateTime;
        this.signals = signals != null ? new ArrayList<>(signals) : new ArrayList<>();
    }

    public abstract SignalScanner toDomain(List<TradingSnapshot> instruments);

    public void updateConfig(ScannerConfig config) {
        workPeriodInMinutes = config.getWorkPeriodInMinutes();
        description = config.getDescription();
        datasourceId = config.getDatasourceId();
        tickers = config.getTickers() != null ? new ArrayList<>(config.getTickers()) : new ArrayList<>();
        updateAlgorithmConfig(config.getAlgorithmConfig());
    }

    public abstract void updateAlgorithmConfig(AlgorithmConfig algorithmConfig);
}

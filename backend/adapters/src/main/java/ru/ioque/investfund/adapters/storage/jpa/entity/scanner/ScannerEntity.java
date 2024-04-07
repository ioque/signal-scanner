package ru.ioque.investfund.adapters.storage.jpa.entity.scanner;

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
import ru.ioque.investfund.adapters.storage.jpa.entity.AbstractEntity;
import ru.ioque.investfund.domain.configurator.SignalScannerConfig;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;
import ru.ioque.investfund.domain.scanner.entity.TradingSnapshot;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@Table(name = "signal_scanner")
@Entity(name = "SignalScannerEntity")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "SIGNAL_SCANNER_TYPE", discriminatorType = DiscriminatorType.STRING, columnDefinition = "varchar(255)")
public abstract class ScannerEntity extends AbstractEntity {
    Integer workPeriodInMinutes;
    String description;
    @ElementCollection(fetch = FetchType.EAGER)
    List<String> tickers;
    LocalDateTime lastExecutionDateTime;
    @OneToMany(mappedBy = "scanner", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    List<SignalEntity> signals;

    public ScannerEntity(
        UUID id,
        Integer workPeriodInMinutes,
        String description,
        List<String> tickers,
        LocalDateTime lastExecutionDateTime,
        List<SignalEntity> signals
    ) {
        super(id);
        this.workPeriodInMinutes = workPeriodInMinutes;
        this.description = description;
        this.tickers = tickers;
        this.lastExecutionDateTime = lastExecutionDateTime;
        this.signals = new ArrayList<>(signals.stream().peek(row -> row.setScanner(this)).toList());
    }

    public abstract SignalScanner toDomain(List<TradingSnapshot> instruments);

    public abstract void updateConfig(SignalScannerConfig config);
}

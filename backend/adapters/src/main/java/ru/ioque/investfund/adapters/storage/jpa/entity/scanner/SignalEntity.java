package ru.ioque.investfund.adapters.storage.jpa.entity.scanner;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.scanner.value.Signal;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "signal")
@Entity(name = "Signal")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SignalEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @ManyToOne
    @JoinColumn(name="scanner_id", nullable=false)
    ScannerEntity scanner;
    String ticker;
    boolean isBuy;
    LocalDateTime dateTime;

    public Signal toDomain() {
        return Signal.builder()
            .dateTime(dateTime)
            .ticker(ticker)
            .isBuy(isBuy)
            .build();
    }

    public static SignalEntity from(Signal signal) {
        return SignalEntity.builder()
            .dateTime(signal.getDateTime())
            .ticker(signal.getTicker())
            .isBuy(signal.isBuy())
            .build();
    }
}

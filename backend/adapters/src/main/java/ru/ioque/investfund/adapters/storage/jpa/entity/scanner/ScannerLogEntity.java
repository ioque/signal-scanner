package ru.ioque.investfund.adapters.storage.jpa.entity.scanner;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.scanner.value.ScannerLog;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "scanner_log")
@Entity(name = "ScannerLog")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScannerLogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    UUID scannerId;
    String message;
    LocalDateTime dateTime;

    public ScannerLog toDomain() {
        return new ScannerLog(message, dateTime);
    }
}

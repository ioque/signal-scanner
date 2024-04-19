package ru.ioque.investfund.domain.scanner.event;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.core.DomainEvent;
import ru.ioque.investfund.domain.datasource.entity.identity.DatasourceId;

import java.time.LocalDateTime;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScanningFinishedEvent implements DomainEvent {
    DatasourceId datasourceId;
    LocalDateTime watermark;
    LocalDateTime dateTime;
}

package ru.ioque.investfund.application.scanner.event;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.application.api.event.IntegrationEvent;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DatasourceScanned implements IntegrationEvent {
    UUID datasourceId;
    LocalDateTime watermark;
    LocalDateTime createdAt;
}

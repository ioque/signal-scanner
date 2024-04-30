package ru.ioque.core.dto.datasource.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InstrumentResponse {
    UUID id;
    String name;
    String ticker;
    String shortName;
    Double todayValue;
    Boolean updatable;
    Double todayLastPrice;
    Double todayFirstPrice;
    Long lastIntradayNumber;
    LocalDateTime lastUpdate;
    List<AggregatedHistoryResponse> historyValues;
}

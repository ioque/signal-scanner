package ru.ioque.core.dto.exchange.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

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
    String ticker;
    String shortName;
    String name;

    List<HistoryValueResponse> historyValues;
    List<IntradayValueResponse> intradayValues;
}

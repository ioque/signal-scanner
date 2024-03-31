package ru.ioque.apitest.dto.exchange;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.UUID;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class InstrumentResponse {
    UUID id;
    String ticker;
    String shortName;
    String name;

    List<HistoryValueResponse> historyValues;
    List<IntradayValueResponse> intradayValues;
}

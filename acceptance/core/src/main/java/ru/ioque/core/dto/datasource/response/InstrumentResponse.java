package ru.ioque.core.dto.datasource.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InstrumentResponse {
    Long id;
    String ticker;
    String shortName;
    String name;

    List<HistoryValueResponse> historyValues;
    List<IntradayValueResponse> intradayValues;
}

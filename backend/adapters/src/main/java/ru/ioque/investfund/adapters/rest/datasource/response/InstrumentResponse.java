package ru.ioque.investfund.adapters.rest.datasource.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import ru.ioque.investfund.adapters.persistence.entity.datasource.historyvalue.HistoryValueEntity;
import ru.ioque.investfund.adapters.persistence.entity.datasource.instrument.InstrumentEntity;
import ru.ioque.investfund.adapters.persistence.entity.datasource.intradayvalue.IntradayValueEntity;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class InstrumentResponse implements Serializable {
    UUID id;
    String name;
    String shortName;
    String ticker;
    List<HistoryValueResponse> historyValues;
    List<IntradayValueResponse> intradayValues;

    public static InstrumentResponse of(InstrumentEntity instrument, List<HistoryValueEntity> history, List<IntradayValueEntity> intraday) {
        return InstrumentResponse.builder()
            .id(instrument.getId())
            .ticker(instrument.getTicker())
            .name(instrument.getName())
            .shortName(instrument.getShortName())
            .historyValues(history.stream().map(HistoryValueResponse::fromDomain).toList())
            .intradayValues(intraday.stream().map(IntradayValueResponse::fromDomain).toList())
            .build();
    }
}

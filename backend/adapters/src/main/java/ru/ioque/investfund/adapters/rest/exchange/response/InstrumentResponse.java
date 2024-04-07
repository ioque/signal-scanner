package ru.ioque.investfund.adapters.rest.exchange.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import ru.ioque.investfund.domain.datasource.entity.Instrument;

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
    List<DailyValueResponse> historyValues;
    List<IntradayValueResponse> intradayValues;

    public static InstrumentResponse fromDomain(Instrument instrument) {
        return InstrumentResponse.builder()
            .id(instrument.getId())
            .ticker(instrument.getTicker())
            .name(instrument.getName())
            .shortName(instrument.getShortName())
            .historyValues(instrument.getHistoryValues().stream().map(DailyValueResponse::fromDomain).toList())
            .intradayValues(instrument.getIntradayValues().stream().map(IntradayValueResponse::fromDomain).toList())
            .build();
    }
}

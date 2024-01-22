package ru.ioque.investfund.adapters.rest.exchange.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import ru.ioque.investfund.domain.exchange.entity.Instrument;

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
    List<DailyTradingResultResponse> dailyTradingResults;
    List<IntradayValueResponse> intradayValues;

    public static InstrumentResponse fromDomain(Instrument instrument) {
        return InstrumentResponse.builder()
            .id(instrument.getId())
            .ticker(instrument.getTicker())
            .name(instrument.getName())
            .shortName(instrument.getShortName())
            .dailyTradingResults(instrument.getDailyValues().stream().map(DailyTradingResultResponse::fromDomain).toList())
            .intradayValues(instrument.getIntradayValues().stream().map(IntradayValueResponse::fromDomain).toList())
            .build();

    }
}

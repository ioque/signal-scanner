package ru.ioque.investfund.adapters.rest.datasource.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import ru.ioque.investfund.adapters.persistence.entity.datasource.instrument.InstrumentEntity;
import ru.ioque.investfund.adapters.persistence.entity.datasource.instrument.TradingStateEmbeddable;

import java.io.Serializable;
import java.time.LocalDateTime;
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
    String ticker;
    String shortName;
    Double todayValue;
    Boolean updatable;
    Double todayLastPrice;
    Double todayFirstPrice;
    Long lastIntradayNumber;
    LocalDateTime lastUpdate;
    List<AggregatedHistoryResponse> historyValues;

    public static InstrumentResponse from(InstrumentEntity instrument) {
        return InstrumentResponse.builder()
            .id(instrument.getId())
            .ticker(instrument.getTicker())
            .name(instrument.getName())
            .shortName(instrument.getShortName())
            .updatable(instrument.getUpdatable())
            .historyValues(instrument
                .getHistory()
                .stream()
                .map(AggregatedHistoryResponse::fromDomain)
                .toList()
            )
            .lastUpdate(
                instrument
                    .getTradingState()
                    .map(TradingStateEmbeddable::getDateTime)
                    .orElse(null)
            )
            .todayValue(instrument
                .getTradingState()
                .map(TradingStateEmbeddable::getTodayValue)
                .orElse(null)
            )
            .todayLastPrice(instrument
                .getTradingState()
                .map(TradingStateEmbeddable::getTodayLastPrice)
                .orElse(null)
            )
            .todayFirstPrice(instrument
                .getTradingState()
                .map(TradingStateEmbeddable::getTodayFirstPrice)
                .orElse(null)
            )
            .lastIntradayNumber(instrument
                .getTradingState()
                .map(TradingStateEmbeddable::getLastIntradayNumber)
                .orElse(null))
            .build();
    }
}

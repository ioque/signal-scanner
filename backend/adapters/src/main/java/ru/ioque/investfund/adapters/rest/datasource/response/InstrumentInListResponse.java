package ru.ioque.investfund.adapters.rest.datasource.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.adapters.psql.entity.datasource.instrument.InstrumentEntity;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InstrumentInListResponse implements Serializable {
    UUID id;
    String ticker;
    String shortName;
    Double todayValue;
    Double todayLastPrice;

    public static InstrumentInListResponse from(InstrumentEntity instrument) {
        return InstrumentInListResponse.builder()
            .id(instrument.getId())
            .ticker(instrument.getDetails().getTicker())
            .shortName(instrument.getDetails().getShortName())
            .build();
    }
}

package ru.ioque.investfund.adapters.rest.exchange.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.exchange.entity.Instrument;

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
    String shortName;
    String ticker;

    public static InstrumentInListResponse fromDomain(Instrument instrument) {
        return InstrumentInListResponse.builder()
            .id(instrument.getId())
            .shortName(instrument.getName())
            .ticker(instrument.getTicker())
            .build();
    }
}

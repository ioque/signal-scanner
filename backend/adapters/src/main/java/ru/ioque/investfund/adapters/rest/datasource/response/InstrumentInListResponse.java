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
import ru.ioque.investfund.adapters.persistence.entity.datasource.instrument.InstrumentEntity;

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

    public static InstrumentInListResponse from(InstrumentEntity instrument) {
        return InstrumentInListResponse.builder()
            .id(instrument.getId())
            .shortName(instrument.getShortName())
            .ticker(instrument.getTicker())
            .build();
    }
}

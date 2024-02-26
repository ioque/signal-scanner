package ru.ioque.investfund.adapters.exchange.moex.client.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.exchange.entity.Instrument;

import java.util.UUID;

@Getter
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
public abstract class InstrumentDto {
    String ticker;
    String shortName;
    String name;

    public abstract Instrument toDomain(UUID id);
}

package ru.ioque.investfund.adapters.datasource.client.dto.intraday;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.datasource.value.Delta;
import ru.ioque.investfund.domain.datasource.value.IntradayValue;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeltaDto extends IntradayValueDto {
    @Builder
    public DeltaDto(Long number, LocalDateTime dateTime, String ticker, Double value, Double price) {
        super(number, dateTime, ticker, value, price);
    }

    @Override
    public IntradayValue toDomain(UUID datasourceId) {
        return Delta.builder()
            .datasourceId(datasourceId)
            .number(getNumber())
            .ticker(getTicker())
            .dateTime(getDateTime())
            .value(getValue())
            .price(getPrice())
            .build();
    }
}

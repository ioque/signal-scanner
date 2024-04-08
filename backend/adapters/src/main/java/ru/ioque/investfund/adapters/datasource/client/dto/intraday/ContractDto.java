package ru.ioque.investfund.adapters.datasource.client.dto.intraday;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.datasource.value.Contract;
import ru.ioque.investfund.domain.datasource.value.IntradayValue;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContractDto extends IntradayValueDto {
    Integer qnt;

    @Builder
    public ContractDto(Long number, LocalDateTime dateTime, String ticker, Double value, Double price, Integer qnt) {
        super(number, dateTime, ticker, value, price);
        this.qnt = qnt;
    }

    @Override
    public IntradayValue toDomain(UUID datasourceId) {
        return Contract.builder()
            .datasourceId(datasourceId)
            .number(getNumber())
            .ticker(getTicker())
            .dateTime(getDateTime())
            .value(getValue())
            .price(getPrice())
            .qnt(qnt)
            .build();
    }
}

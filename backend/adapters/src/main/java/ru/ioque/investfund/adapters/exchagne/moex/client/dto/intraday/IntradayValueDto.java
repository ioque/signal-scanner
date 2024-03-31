package ru.ioque.investfund.adapters.exchagne.moex.client.dto.intraday;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.exchange.value.IntradayValue;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonSubTypes({
    @JsonSubTypes.Type(value = DeltaDto.class, name = "DeltaDto"),
    @JsonSubTypes.Type(value = ContractDto.class, name = "ContractDto"),
    @JsonSubTypes.Type(value = DealDto.class, name = "DealDto")
})
public abstract class IntradayValueDto {
    LocalDateTime dateTime;
    String ticker;
    Double value;
    Double price;

    public abstract IntradayValue toDomain();
}

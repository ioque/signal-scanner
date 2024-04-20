package ru.ioque.investfund.adapters.datasource.client.dto.instrument;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.datasource.value.details.InstrumentDetails;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonSubTypes({
    @JsonSubTypes.Type(value = CurrencyPairDto.class, name = "CurrencyPairDto"),
    @JsonSubTypes.Type(value = FuturesDto.class, name = "FuturesDto"),
    @JsonSubTypes.Type(value = IndexDto.class, name = "IndexDto"),
    @JsonSubTypes.Type(value = StockDto.class, name = "StockDto") }
)
public abstract class InstrumentDto {
    String ticker;
    String shortName;
    String name;

    public abstract InstrumentDetails toDetails();
}

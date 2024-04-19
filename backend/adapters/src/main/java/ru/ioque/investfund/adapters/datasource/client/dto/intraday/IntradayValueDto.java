package ru.ioque.investfund.adapters.datasource.client.dto.intraday;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.datasource.entity.identity.DatasourceId;
import ru.ioque.investfund.domain.datasource.value.IntradayValue;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION, defaultImpl = DeltaDto.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonSubTypes({
    @JsonSubTypes.Type(value = DeltaDto.class, name = "DeltaDto"),
    @JsonSubTypes.Type(value = ContractDto.class, name = "ContractDto"),
    @JsonSubTypes.Type(value = DealDto.class, name = "DealDto")
})
public abstract class IntradayValueDto {
    Long number;
    LocalDateTime dateTime;
    String ticker;
    Double value;
    Double price;

    public abstract IntradayValue toDomain(DatasourceId datasourceId);
}

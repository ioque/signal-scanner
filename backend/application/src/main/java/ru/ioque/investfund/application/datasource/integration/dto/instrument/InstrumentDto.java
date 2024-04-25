package ru.ioque.investfund.application.datasource.integration.dto.instrument;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
    @NotBlank(message = "Не заполнен тикер инструмента.")
    @Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "Тикер должен быть непустой строкой, состоящей из латинских букв или цифр.")
    String ticker;
    @NotBlank(message = "Не заполнено краткое наименование инструмента.")
    String shortName;
    @NotBlank(message = "Не заполнено полное наименование инструмента.")
    String name;

    public abstract InstrumentDetails toDetails();
}

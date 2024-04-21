package ru.ioque.investfund.application.datasource.dto.intraday;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayValue;

import java.time.LocalDateTime;
import java.util.Objects;

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
public abstract class IntradayValueDto implements Comparable<IntradayValue> {
    @NotBlank(message = "Не заполнен тикер.")
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "Тикер должен быть непустой строкой, состоящей из латинских букв или цифр.")
    String ticker;

    @NotNull(message = "Не указан номер.")
    @Min(value = 0, message = "Номер должен быть больше нуля.")
    Long number;

    @NotNull(message = "Не указано время.")
    LocalDateTime dateTime;

    @NotNull(message = "Не указана цена.")
    @DecimalMin(value = "0", inclusive = false, message = "Цена не может быть отрицательной.")
    Double price;

    @NotNull(message = "Не указан объем.")
    @DecimalMin(value = "0", inclusive = false, message = "Объем не может быть отрицательным.")
    Double value;

    @Override
    public int compareTo(IntradayValue intradayValue) {
        return Objects.compare(getNumber(), intradayValue.getNumber(), Long::compareTo);
    }

    public abstract IntradayValue toIntradayValue();
}

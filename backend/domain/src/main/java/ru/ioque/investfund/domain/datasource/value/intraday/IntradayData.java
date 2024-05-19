package ru.ioque.investfund.domain.datasource.value.intraday;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class IntradayData implements Comparable<IntradayData> {
    @NotBlank(message = "Не заполнен тикер.")
    @Valid Ticker ticker;
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
    public int compareTo(IntradayData intradayData) {
        return Objects.compare(getNumber(), intradayData.getNumber(), Long::compareTo);
    }
}

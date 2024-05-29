package ru.ioque.investfund.domain.datasource.value.history;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

import java.time.LocalDate;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AggregatedTotals implements Comparable<AggregatedTotals> {
    @NotBlank(message = "Не заполнен тикер.")
    @Valid Ticker ticker;
    @NotNull(message = "Не заполнена дата агрегированных итогов.")
    LocalDate date;
    @NotNull(message = "Не заполнена минимальная цена.")
    @DecimalMin(value = "0", inclusive = false, message = "Минимальная цена не может быть отрицательной.")
    Double lowPrice;
    @NotNull(message = "Не заполнена цена закрытия.")
    @DecimalMin(value = "0", inclusive = false, message = "Цена закрытия не может быть отрицательной.")
    Double highPrice;
    @NotNull(message = "Не заполнена цена открытия.")
    @DecimalMin(value = "0", inclusive = false, message = "Цена открытия не может быть отрицательной.")
    Double openPrice;
    @NotNull(message = "Не заполнена цена закрытия.")
    @DecimalMin(value = "0", inclusive = false, message = "Цена закрытия не может быть отрицательной.")
    Double closePrice;
    @DecimalMin(value = "0", inclusive = false, message = "Средневзвешенная цена не может быть отрицательной.")
    Double waPrice;
    @NotNull(message = "Не заполнен объем.")
    @DecimalMin(value = "0", inclusive = false, message = "Объем не может быть отрицательным.")
    Double value;

    @Override
    public int compareTo(AggregatedTotals aggregatedTotals) {
        return date.compareTo(aggregatedTotals.date);
    }

    public boolean isBetween(LocalDate from, LocalDate to) {
        return from.compareTo(date) * date.compareTo(to) >= 0;
    }
}

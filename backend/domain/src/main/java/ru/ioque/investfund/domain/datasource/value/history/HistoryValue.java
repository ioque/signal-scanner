package ru.ioque.investfund.domain.datasource.value.history;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

import java.time.LocalDate;
import java.util.Objects;

@ToString
@EqualsAndHashCode
@Getter(AccessLevel.PUBLIC)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class HistoryValue implements Comparable<HistoryValue> {
    @NotNull(message = "Не заполнен тикер.")
    @Valid
    Ticker ticker;

    @NotNull(message = "Не заполнена дата агрегированных итогов.")
    LocalDate tradeDate;

    @NotNull(message = "Не заполнена цена открытия.")
    @DecimalMin(value = "0", inclusive = false, message = "Цена открытия не может быть отрицательной.")
    Double openPrice;

    @NotNull(message = "Не заполнена цена закрытия.")
    @DecimalMin(value = "0", inclusive = false, message = "Цена закрытия не может быть отрицательной.")
    Double closePrice;

    @NotNull(message = "Не заполнена минимальная цена.")
    @DecimalMin(value = "0", inclusive = false, message = "Минимальная цена не может быть отрицательной.")
    Double lowPrice;

    @NotNull(message = "Не заполнена максимальная цена.")
    @DecimalMin(value = "0", inclusive = false, message = "Максимальная цена не может быть отрицательной.")
    Double highPrice;

    @DecimalMin(value = "0", inclusive = false, message = "Средневзвешенная цена не может быть отрицательной.")
    Double waPrice;

    @NotNull(message = "Не заполнен объем.")
    @DecimalMin(value = "0", inclusive = false, message = "Объем не может быть отрицательным.")
    Double value;

    @Builder
    public HistoryValue(
        Ticker ticker,
        LocalDate tradeDate,
        Double openPrice,
        Double closePrice,
        Double lowPrice,
        Double highPrice,
        Double waPrice,
        Double value
    ) {
        this.ticker = ticker;
        this.tradeDate = tradeDate;
        this.openPrice = openPrice;
        this.closePrice = closePrice;
        this.lowPrice = lowPrice;
        this.highPrice = highPrice;
        this.waPrice = waPrice;
        this.value = value;
    }

    @Override
    public int compareTo(HistoryValue historyValue) {
        return Objects.compare(getTradeDate(), historyValue.getTradeDate(), LocalDate::compareTo);
    }
}

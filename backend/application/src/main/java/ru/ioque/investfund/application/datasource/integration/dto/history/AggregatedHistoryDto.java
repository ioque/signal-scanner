package ru.ioque.investfund.application.datasource.integration.dto.history;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.datasource.value.AggregatedHistory;

import java.time.LocalDate;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AggregatedHistoryDto {
    @NotBlank(message = "Не заполнен тикер.")
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "Тикер должен быть непустой строкой, состоящей из латинских букв или цифр.")
    String ticker;

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

    public AggregatedHistory toAggregateHistory() {
        return AggregatedHistory.builder()
            .date(tradeDate)
            .openPrice(openPrice)
            .closePrice(closePrice)
            .highPrice(highPrice)
            .lowPrice(lowPrice)
            .waPrice(waPrice)
            .value(value)
            .build();
    }
}

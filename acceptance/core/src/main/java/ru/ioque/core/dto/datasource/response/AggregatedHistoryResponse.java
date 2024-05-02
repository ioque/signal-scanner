package ru.ioque.core.dto.datasource.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AggregatedHistoryResponse {
    Double value;
    @JsonInclude(Include.NON_NULL)
    Double waPrice;
    Double lowPrice;
    Double highPrice;
    Double openPrice;
    Double closePrice;
    LocalDate tradeDate;
}

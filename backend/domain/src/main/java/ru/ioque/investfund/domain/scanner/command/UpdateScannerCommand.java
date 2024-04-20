package ru.ioque.investfund.domain.scanner.command;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.core.Command;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;
import ru.ioque.investfund.domain.scanner.algorithms.properties.AlgorithmProperties;
import ru.ioque.investfund.domain.scanner.entity.ScannerId;

import java.util.List;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateScannerCommand implements Command {
    @NotNull(message = "Не передан идентификатор сканера.")
    ScannerId scannerId;
    @NotNull(message = "Не передан период работы сканера.")
    @Min(value = 1, message = "Период работы сканера должен быть больше 0.")
    Integer workPeriodInMinutes;
    @NotBlank(message = "Не передано описание сканера.")
    String description;
    @NotEmpty(message = "Не передан список тикеров анализируемых инструментов.")
    List<@Valid Ticker> tickers;
    @NotNull(message = "Не переданы параметры алгоритма.")
    @Valid AlgorithmProperties properties;
}

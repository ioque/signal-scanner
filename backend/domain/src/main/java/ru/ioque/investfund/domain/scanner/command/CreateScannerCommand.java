package ru.ioque.investfund.domain.scanner.command;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.scanner.value.algorithms.properties.AlgorithmProperties;

import java.util.List;
import java.util.UUID;

@ToString
@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CreateScannerCommand {
    @NotNull(message = "Не передан идентификатор источника данных.")
    UUID datasourceID;
    @NotNull(message = "Не передан период работы сканера.")
    @Min(value = 1, message = "Период работы сканера должен быть больше 0.")
    Integer workPeriodInMinutes;
    @NotNull(message = "Не передано описание сканера.")
    String description;
    @NotEmpty(message = "Не передан список тикеров анализируемых инструментов.")
    List<@NotBlank(message = "Тикер не может быть пустой строкой.") String> tickers;
    @NotNull(message = "Не переданы параметры алгоритма.")
    AlgorithmProperties properties;
}

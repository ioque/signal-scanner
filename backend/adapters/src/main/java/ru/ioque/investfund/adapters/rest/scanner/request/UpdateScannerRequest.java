package ru.ioque.investfund.adapters.rest.scanner.request;

import jakarta.validation.Valid;
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
import ru.ioque.investfund.domain.datasource.value.types.Ticker;
import ru.ioque.investfund.application.modules.scanner.command.UpdateScanner;
import ru.ioque.investfund.domain.scanner.entity.identifier.ScannerId;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateScannerRequest {
    @NotNull(message = "Не передан период работы сканера.")
    Integer workPeriodInMinutes;
    @NotBlank(message = "Не передано описание сканера.")
    String description;
    @NotEmpty(message = "Не передан список тикеров анализируемых инструментов.")
    List<@NotBlank(message = "Тикер не может быть пустым") String> tickers;
    @NotNull(message = "Не переданы параметры алгоритма.")
    @Valid
    AlgorithmPropertiesDto properties;

    public UpdateScanner toCommand(UUID scannerId) {
        return UpdateScanner.builder()
            .scannerId(ScannerId.from(scannerId))
            .properties(properties.toDomain())
            .workPeriodInMinutes(workPeriodInMinutes)
            .description(description)
            .tickers(tickers.stream().map(Ticker::from).toList())
            .build();
    }
}

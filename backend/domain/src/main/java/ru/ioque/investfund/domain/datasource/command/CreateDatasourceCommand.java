package ru.ioque.investfund.domain.datasource.command;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.core.Command;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CreateDatasourceCommand implements Command {
    @NotBlank(message = "Не передано название источника данных.")
    String name;
    @NotBlank(message = "Не передан адрес источника данных.")
    String url;
    @NotBlank(message = "Не передано описание источника данных.")
    String description;
}

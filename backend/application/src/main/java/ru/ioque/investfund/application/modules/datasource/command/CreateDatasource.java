package ru.ioque.investfund.application.modules.datasource.command;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.application.modules.api.Command;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateDatasource implements Command {
    @NotBlank(message = "Не передано название источника данных.")
    String name;
    @NotBlank(message = "Не передан адрес источника данных.")
    String url;
    @NotBlank(message = "Не передано описание источника данных.")
    String description;
}

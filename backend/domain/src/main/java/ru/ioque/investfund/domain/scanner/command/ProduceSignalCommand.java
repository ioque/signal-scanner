package ru.ioque.investfund.domain.scanner.command;

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
import ru.ioque.investfund.domain.datasource.entity.indetity.DatasourceId;

import java.time.LocalDateTime;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProduceSignalCommand implements Command {
    @NotNull(message = "Не передан идентификатор источника данных.")
    DatasourceId datasourceId;
    @NotNull(message = "Не передан watermark.")
    LocalDateTime watermark;
}

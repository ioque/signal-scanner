package ru.ioque.investfund.application.modules.scanner;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.application.share.exception.ApplicationException;
import ru.ioque.investfund.domain.scanner.financial.entity.SignalConfig;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@ToString
@EqualsAndHashCode
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AddScannerCommand {
    String description;
    List<UUID> ids;
    SignalConfig signalConfig;

    @Builder
    public AddScannerCommand(
        String description,
        List<UUID> ids,
        SignalConfig signalConfig
    ) {
        this.ids = ids;
        this.description = description;
        this.signalConfig = signalConfig;
        validateInputData();
    }

    private void validateInputData() {
        if (Objects.isNull(signalConfig)) {
            throw new ApplicationException("Не передана конфигурация алгоритма.");
        }
        if (ids == null || ids.isEmpty()) {
            throw new ApplicationException("Список анализируемых инструментов не может быть пуст.");
        }
        if (description == null || description.isEmpty()) {
            throw new ApplicationException("Описание сканера не может быть пустым.");
        }
    }
}

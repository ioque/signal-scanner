package ru.ioque.investfund.application.modules.configurator;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DatasourceRepository;

@Component
@AllArgsConstructor
public class CommandValidator {
    private DatasourceRepository datasourceRepository;

    public void validate(AddNewScannerCommand command) {

    }
}

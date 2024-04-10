package ru.ioque.investfund.domain.configurator.validator;

import ru.ioque.investfund.domain.configurator.entity.ScannerConfig;
import ru.ioque.investfund.domain.core.ValidatorException;

import java.util.ArrayList;
import java.util.List;

public class ScannerConfigValidator {
    private final List<String> existsTickers;
    private final ScannerConfig config;

    public ScannerConfigValidator(List<String> existsTickers, ScannerConfig config) {
        this.existsTickers = existsTickers;
        this.config = config;
    }

    public void validate() {
        List<String> errors = new ArrayList<>();
        config.getTickers().forEach(ticker -> {
            if (!existsTickers.contains(ticker)) {
                errors.add("Инструмент с тикером " + ticker + " не найден.");
            }
        });
        if(!errors.isEmpty()) {
            throw new ValidatorException(errors);
        }
    }
}

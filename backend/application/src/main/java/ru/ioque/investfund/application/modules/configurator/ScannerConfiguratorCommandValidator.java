package ru.ioque.investfund.application.modules.configurator;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DatasourceRepository;
import ru.ioque.investfund.application.share.exception.ValidatorException;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.entity.Instrument;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@AllArgsConstructor
public class ScannerConfiguratorCommandValidator {
    private DatasourceRepository datasourceRepository;

    public void validate(AddNewScannerCommand command) {
        final List<String> errors = new ArrayList<>();
        final Optional<Datasource> datasource = datasourceRepository.getBy(command.getDatasourceId());

        if (datasource.isEmpty()) {
            errors.add("Источник данных с идентификатором " + command.getDatasourceId() + " не найден.");
        }

        if (datasource.isPresent()) {
            List<String> tickers = datasource.get().getInstruments().stream().map(Instrument::getTicker).toList();
            command.getTickers().forEach(ticker -> {
                if (!tickers.contains(ticker)) {
                    errors.add("Инструмент с тикером " + ticker + " не найден.");
                }
            });
        }

        if(Objects.isNull(command.getDescription()) || command.getDescription().isBlank()) {
            errors.add("Не заполнено описание сканера.");
        }

        if(Objects.isNull(command.getWorkPeriodInMinutes())) {
            errors.add("Не указан период работы сканера.");
        }


        if (!errors.isEmpty()) {
            throw new ValidatorException(errors);
        }
    }
}

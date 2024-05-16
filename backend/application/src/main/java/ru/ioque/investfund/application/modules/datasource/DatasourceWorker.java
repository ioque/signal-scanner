package ru.ioque.investfund.application.modules.datasource;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.application.adapters.DatasourceProvider;
import ru.ioque.investfund.application.adapters.IntradayValueRepository;
import ru.ioque.investfund.application.modules.datasource.handler.integration.dto.intraday.IntradayDataDto;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.entity.Instrument;

import java.util.List;

@Builder
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DatasourceWorker {
    Datasource datasource;
    List<Instrument> instruments;
    DatasourceProvider datasourceProvider;
    IntradayValueRepository intradayValueRepository;

    public void work() {
        instruments.forEach(instrument -> {
            datasourceProvider.fetchIntradayValues(datasource, instrument)
                .stream()
                .map(IntradayDataDto::toIntradayValue)
                .filter(data -> data.getNumber() > instrument.getLastTradingNumber())
                .forEach(intradayValueRepository::publish);
        });
    }
}

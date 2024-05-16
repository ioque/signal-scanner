package ru.ioque.investfund.application.modules.datasource;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.application.adapters.DatasourceProvider;
import ru.ioque.investfund.application.adapters.IntradayJournalPublisher;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.entity.Instrument;

@Builder
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DatasourceWorker {
    Datasource datasource;
    DatasourceProvider datasourceProvider;
    IntradayJournalPublisher intradayJournalPublisher;

    public void work() {
        datasource.getInstruments()
            .stream()
            .filter(Instrument::isUpdatable)
            .forEach(instrument -> datasourceProvider.fetchIntradayValues(datasource, instrument)
                .stream()
                .filter(data -> data.getNumber() > instrument.getLastTradingNumber())
                .forEach(intradayJournalPublisher::publish));
    }
}

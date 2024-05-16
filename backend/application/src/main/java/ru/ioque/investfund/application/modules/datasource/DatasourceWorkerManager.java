package ru.ioque.investfund.application.modules.datasource;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DatasourceProvider;
import ru.ioque.investfund.application.adapters.IntradayJournalPublisher;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.entity.identity.DatasourceId;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DatasourceWorkerManager {
    Map<DatasourceId, DatasourceWorker> workers = new ConcurrentHashMap<>();
    DatasourceProvider datasourceProvider;
    IntradayJournalPublisher intradayJournalPublisher;

    public void createWorker(Datasource datasource) {
        workers.put(
            datasource.getId(),
            DatasourceWorker.builder()
                .datasource(datasource)
                .datasourceProvider(datasourceProvider)
                .intradayJournalPublisher(intradayJournalPublisher)
                .build()
        );
    }

    public void runWorkers(DatasourceId datasourceId) {
        Optional.ofNullable(workers.get(datasourceId)).ifPresent(DatasourceWorker::work);
    }

    public void deleteWorker(DatasourceId datasourceId) {
        workers.remove(datasourceId);
    }
}

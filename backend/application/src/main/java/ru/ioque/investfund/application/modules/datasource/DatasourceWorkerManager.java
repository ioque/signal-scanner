package ru.ioque.investfund.application.modules.datasource;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DatasourceProvider;
import ru.ioque.investfund.application.adapters.IntradayValueRepository;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.entity.identity.DatasourceId;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DatasourceWorkerManager {
    Map<DatasourceId, DatasourceWorker> workers = new ConcurrentHashMap<>();
    DatasourceProvider datasourceProvider;
    IntradayValueRepository intradayValueRepository;

    public void createWorker(Datasource datasource, List<Instrument> instruments) {
        workers.put(
            datasource.getId(),
            DatasourceWorker.builder()
                .datasource(datasource)
                .instruments(instruments)
                .datasourceProvider(datasourceProvider)
                .intradayValueRepository(intradayValueRepository)
                .build()
        );
    }

    public void runWorkers() {
        workers.values().forEach(DatasourceWorker::work);
    }

    public void deleteWorker(DatasourceId datasourceId) {
        workers.remove(datasourceId);
    }
}

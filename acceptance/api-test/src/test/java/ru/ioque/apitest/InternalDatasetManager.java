package ru.ioque.apitest;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.ioque.apitest.repos.DatasetRepository;
import ru.ioque.core.dataset.Dataset;

@Component
@Profile("local")
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class InternalDatasetManager implements DatasetManager {
    DatasetRepository datasetRepository;

    @Override
    public void initDataset(Dataset dataset) {
        datasetRepository.initInstruments(dataset.getInstruments());
        datasetRepository.initIntradayValues(dataset.getIntradayValues());
        datasetRepository.initHistoryValues(dataset.getHistoryValues());
    }
}

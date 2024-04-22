package ru.ioque.apitest.dataset;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.core.dataset.Dataset;

@Component
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

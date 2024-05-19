package ru.ioque.core.dataset;

public class DatasetStorageFactory {
    public static DatasetStorage createDatasetStorage(Dataset dataset) {
        if (dataset.getWorkMode().equals(WorkMode.BATCH)) {
            return new BatchStorage(dataset);
        }
        return new StreamingStorage(dataset);
    }
}

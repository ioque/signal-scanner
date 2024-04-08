package ru.ioque.investfund.application.adapters;

import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.value.HistoryValue;
import ru.ioque.investfund.domain.datasource.value.IntradayValue;

import java.util.List;
import java.util.Optional;

public interface DatasourceRepository {
    Optional<Datasource> get();
    void saveDatasource(Datasource datasource);
    void saveIntradayValues(List<IntradayValue> intradayValues);
    void saveHistoryValues(List<HistoryValue> historyValues);
    void deleteDatasource();
}
package ru.ioque.investfund.application.adapters;

import ru.ioque.investfund.domain.datasource.entity.identity.DatasourceId;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.scanner.value.TradingSnapshot;

import java.util.List;

public interface TradingSnapshotsRepository {
    List<TradingSnapshot> findAllBy(DatasourceId datasourceId, List<InstrumentId> instrumentIds);
}

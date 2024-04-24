package ru.ioque.investfund.application.adapters;

import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.scanner.value.TradingSnapshot;

import java.util.List;

public interface TradingSnapshotsRepository {
    List<TradingSnapshot> findAllBy(List<InstrumentId> instrumentIds);
    TradingSnapshot getBy(InstrumentId instrumentId);
}

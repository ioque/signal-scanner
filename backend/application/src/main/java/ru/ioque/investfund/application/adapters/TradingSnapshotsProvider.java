package ru.ioque.investfund.application.adapters;

import ru.ioque.investfund.domain.scanner.value.TradingSnapshot;

import java.util.List;
import java.util.UUID;

public interface TradingSnapshotsProvider {
    List<TradingSnapshot> findBy(UUID datasourceId, List<String> tickers);
}

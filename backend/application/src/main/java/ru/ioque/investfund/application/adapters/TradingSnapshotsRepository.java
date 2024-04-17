package ru.ioque.investfund.application.adapters;

import ru.ioque.investfund.domain.scanner.value.TradingSnapshot;

import java.util.List;
import java.util.UUID;

public interface TradingSnapshotsRepository {
    List<TradingSnapshot> findAllBy(UUID datasourceId, List<String> tickers);
}

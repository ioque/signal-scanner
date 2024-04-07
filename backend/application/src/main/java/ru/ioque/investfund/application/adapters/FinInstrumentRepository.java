package ru.ioque.investfund.application.adapters;

import ru.ioque.investfund.domain.scanner.entity.TradingSnapshot;

import java.util.List;

public interface FinInstrumentRepository {
    List<TradingSnapshot> getBy(List<String> tickers);
}

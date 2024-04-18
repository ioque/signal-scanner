package ru.ioque.investfund.domain.scanner.value.algorithms;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.core.DomainException;
import ru.ioque.investfund.domain.scanner.value.SignalSign;
import ru.ioque.investfund.domain.scanner.value.TradingSnapshot;
import ru.ioque.investfund.domain.scanner.value.algorithms.properties.SectoralRetardProperties;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SectoralRetardAlgorithm extends ScannerAlgorithm {
    Double historyScale;
    Double intradayScale;

    public SectoralRetardAlgorithm(SectoralRetardProperties properties) {
        super(properties.getType().getName());
        setHistoryScale(properties.getHistoryScale());
        setIntradayScale(properties.getIntradayScale());
    }

    @Override
    public List<SignalSign> run(List<TradingSnapshot> tradingSnapshots, LocalDateTime watermark) {
        List<SignalSign> signalSigns = new ArrayList<>();
        List<TradingSnapshot> riseInstruments = getRiseInstruments(tradingSnapshots);
        List<TradingSnapshot> otherInstruments = getSectoralRetards(tradingSnapshots, riseInstruments);
        if (!otherInstruments.isEmpty() && Math.round((double) riseInstruments.size() / tradingSnapshots.size() * 100) >= 70) {
            otherInstruments.forEach(snapshot -> {
                final String summary = String.format(
                    "Растущие инструменты сектора: %s",
                    riseInstruments.stream().map(TradingSnapshot::getTicker).toList()
                );
                signalSigns.add(
                    SignalSign.builder()
                        .isBuy(true)
                        .summary(summary)
                        .dateTime(watermark)
                        .ticker(snapshot.getTicker())
                        .price(snapshot.getTodayLastPrice().orElse(0D))
                        .build()
                );
            });
        }
        return signalSigns;
    }

    private static List<TradingSnapshot> getSectoralRetards(
        List<TradingSnapshot> tradingSnapshots,
        List<TradingSnapshot> riseInstruments
    ) {
        return tradingSnapshots
            .stream()
            .filter(row -> !riseInstruments.contains(row))
            .toList();
    }

    private List<TradingSnapshot> getRiseInstruments(List<TradingSnapshot> tradingSnapshots) {
        return tradingSnapshots
            .stream()
            .filter(row -> row.isRiseInLastTwoDay(historyScale, intradayScale))
            .toList();
    }

    private void setIntradayScale(Double intradayScale) {
        if (intradayScale == null) {
            throw new DomainException("Не передан параметр intradayScale.");
        }
        if (intradayScale <= 0) {
            throw new DomainException("Параметр intradayScale должен быть больше нуля.");
        }
        this.intradayScale = intradayScale;
    }

    private void setHistoryScale(Double historyScale) {
        if (historyScale == null) {
            throw new DomainException("Не передан параметр historyScale.");
        }
        if (historyScale <= 0) {
            throw new DomainException("Параметр historyScale должен быть больше нуля.");
        }
        this.historyScale = historyScale;
    }
}

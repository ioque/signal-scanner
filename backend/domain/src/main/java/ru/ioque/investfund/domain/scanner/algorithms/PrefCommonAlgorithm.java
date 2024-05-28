package ru.ioque.investfund.domain.scanner.algorithms;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.core.DomainException;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.value.TradingState;
import ru.ioque.investfund.domain.scanner.algorithms.properties.PrefCommonProperties;
import ru.ioque.investfund.domain.scanner.entity.Signal;
import ru.ioque.investfund.domain.scanner.value.PrefSimplePair;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PrefCommonAlgorithm extends ScannerAlgorithm {
    final Double spreadValue;

    public PrefCommonAlgorithm(PrefCommonProperties properties) {
        super(properties.getType().getName());
        this.spreadValue = properties.getSpreadValue();
    }

    @Override
    public List<Signal> findSignals(List<Instrument> instruments, LocalDateTime watermark) {
        List<Signal> signals = new ArrayList<>();
        findAllPrefAndSimplePairs(instruments).forEach(pair -> {
            final double currentDelta = pair.getCurrentDelta();
            final double historyDelta = pair.getHistoryDelta();
            final double multiplier = currentDelta / historyDelta;
            DecimalFormat formatter = new DecimalFormat("#,###.##");
            if (multiplier > spreadValue) {
                signals.add(Signal.builder()
                    .instrumentId(pair.getPref().getId())
                    .isBuy(true)
                    .summary(
                        String.format(
                            """
                            Текущая дельта: %s;
                            Историческая дельта: %s;
                            Отношение текущей дельты к исторической: %s.""",
                            formatter.format(currentDelta), formatter.format(historyDelta), formatter.format(multiplier)
                        )
                    )
                    .watermark(watermark)
                    .price(pair.getPref().getTradingState().map(TradingState::getTodayLastPrice).orElse(0D))
                    .build()
                );
            }
        });
        return signals;
    }

    private List<PrefSimplePair> findAllPrefAndSimplePairs(List<Instrument> instruments) {
        return instruments
            .stream()
            .filter(Instrument::isPref)
            .map(pref -> new PrefSimplePair(pref, findSimplePair(instruments, pref)))
            .toList();
    }

    private Instrument findSimplePair(
        List<Instrument> instruments,
        Instrument instrument
    ) {
        return instruments
            .stream()
            .filter(instrument::isSimplePair)
            .findFirst()
            .orElseThrow(() -> new DomainException(
                "Для привилегированной акции " + instrument.getTicker() + " не найдена обычная акция."));
    }
}

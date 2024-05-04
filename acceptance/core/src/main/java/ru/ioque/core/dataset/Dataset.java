package ru.ioque.core.dataset;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.ioque.core.datagenerator.history.HistoryValue;
import ru.ioque.core.datagenerator.instrument.Instrument;
import ru.ioque.core.datagenerator.intraday.IntradayValue;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Dataset {
    @Builder.Default
    List<? extends Instrument> instruments = new ArrayList<>();
    @Builder.Default
    List<? extends IntradayValue> intradayValues = new ArrayList<>();
    @Builder.Default
    List<HistoryValue> historyValues = new ArrayList<>();

    @Override
    public String toString() {
        return "Dataset, tickers: " + instruments.stream().map(Instrument::getTicker).toList();
    }
}

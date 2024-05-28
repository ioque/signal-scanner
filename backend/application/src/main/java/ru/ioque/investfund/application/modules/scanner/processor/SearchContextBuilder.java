package ru.ioque.investfund.application.modules.scanner.processor;

import java.util.List;
import java.util.Scanner;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.InstrumentRepository;
import ru.ioque.investfund.application.adapters.journal.AggregatedHistoryJournal;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;
import ru.ioque.investfund.domain.scanner.value.InstrumentPerformance;
import ru.ioque.investfund.domain.scanner.value.SearchContext;

@Component
@AllArgsConstructor
public class SearchContextBuilder {
    final InstrumentRepository instrumentRepository;
    final AggregatedHistoryJournal aggregatedHistoryJournal;

    public SearchContext build(List<SignalScanner> scanners) {
        return new SearchContext(
            scanners
                .stream()
                .map(SignalScanner::getInstrumentIds)
                .flatMap(List::stream)
                .distinct()
                .map(instrumentId -> {
                    final Instrument instrument = instrumentRepository.getBy(instrumentId);
                    return InstrumentPerformance.builder()
                        .ticker(instrument.getTicker())
                        .instrumentId(instrument.getId())
                        .aggregatedHistories(aggregatedHistoryJournal.findAllBy(instrument.getTicker()))
                        .build();
                })
                .toList()
        );
    }
}

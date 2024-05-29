package ru.ioque.investfund.application.modules.scanner.processor;

import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.repository.InstrumentRepository;
import ru.ioque.investfund.application.adapters.journal.AggregatedTotalsJournal;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;
import ru.ioque.investfund.domain.scanner.value.InstrumentPerformance;
import ru.ioque.investfund.domain.scanner.value.SearchContext;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SearchContextManager {
    final InstrumentRepository instrumentRepository;
    final AggregatedTotalsJournal aggregatedTotalsJournal;

    @Getter
    SearchContext searchContext;
    @Getter
    List<SignalScanner> scanners;

    public void initSearchContext(List<SignalScanner> scanners) {
        this.scanners = scanners;
        this.searchContext = new SearchContext(
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
                        .aggregatedHistories(aggregatedTotalsJournal.findAllBy(instrument.getTicker()))
                        .build();
                })
                .toList()
        );
    }
}

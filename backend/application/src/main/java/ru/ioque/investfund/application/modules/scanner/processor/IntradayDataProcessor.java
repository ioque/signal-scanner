package ru.ioque.investfund.application.modules.scanner.processor;

import java.util.List;
import java.util.Map;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.InstrumentRepository;
import ru.ioque.investfund.application.adapters.ScannerRepository;
import ru.ioque.investfund.application.adapters.journal.SignalJournal;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.entity.identity.DatasourceId;
import ru.ioque.investfund.domain.datasource.value.IntradayStatistic;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;
import ru.ioque.investfund.domain.scanner.entity.ScannerId;
import ru.ioque.investfund.domain.scanner.entity.SearchContext;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;
import ru.ioque.investfund.domain.scanner.value.WorkScannerReport;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IntradayDataProcessor {

    final ScannerRepository scannerRepository;
    final InstrumentRepository instrumentRepository;
    final SignalJournal signalJournal;
    final DateTimeProvider dateTimeProvider;

    SearchContext searchContext;
    Map<Ticker, List<ScannerId>> subscribers;
    List<SignalScanner> scanners;

    public void initIntradayDataProcessor(DatasourceId datasourceId) {
        this.scanners = scannerRepository.findAllBy(datasourceId);
        this.searchContext = new SearchContext(
            scanners
                .stream()
                .map(SignalScanner::getInstrumentIds)
                .flatMap(List::stream)
                .distinct()
                .map(instrumentRepository::getBy)
                .toList()
        );
    }

    public void process(IntradayStatistic intradayStatistic) {
        log.info("receive {}", intradayStatistic);
        searchContext.updateStockPerformance(intradayStatistic);
        Instrument instrument = searchContext.getInstrumentBy(intradayStatistic.getTicker());
        instrument.updatePerformance(intradayStatistic);
        scanners.stream()
            .filter(scanner -> scanner.getInstrumentIds().contains(instrument.getId()))
            .forEach(scanner -> {
                log.info("scanner {} started scanning", intradayStatistic);
                final List<Instrument> instruments = scanner.getInstrumentIds().stream().map(searchContext::getInstrumentBy).toList();
                final WorkScannerReport report = scanner.scanning(instruments, dateTimeProvider.nowDateTime());
                report.getRegisteredSignals().forEach(signalJournal::publish);
                log.info("result scanning: {}", report);
            });
    }
}

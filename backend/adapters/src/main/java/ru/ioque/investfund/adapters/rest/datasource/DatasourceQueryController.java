package ru.ioque.investfund.adapters.rest.datasource;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.ioque.investfund.adapters.persistence.entity.datasource.historyvalue.HistoryValueEntity;
import ru.ioque.investfund.adapters.persistence.entity.datasource.instrument.InstrumentEntity;
import ru.ioque.investfund.adapters.persistence.entity.datasource.intradayvalue.IntradayValueEntity;
import ru.ioque.investfund.adapters.persistence.filter.InstrumentFilterParams;
import ru.ioque.investfund.adapters.persistence.DatasourceQueryRepository;
import ru.ioque.investfund.adapters.rest.datasource.response.ExchangeResponse;
import ru.ioque.investfund.adapters.rest.datasource.response.InstrumentInListResponse;
import ru.ioque.investfund.adapters.rest.datasource.response.InstrumentResponse;
import ru.ioque.investfund.application.adapters.DateTimeProvider;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@Tag(name = "DatasourceQueryController", description = "Контроллер запросов к модулю \"DATASOURCE\"")
public class DatasourceQueryController {
    DatasourceQueryRepository datasourceQueryRepository;
    DateTimeProvider dateTimeProvider;

    @GetMapping("/api/datasource")
    public List<ExchangeResponse> getAllDatasource() {
        return datasourceQueryRepository.getAllDatasource().stream().map(ExchangeResponse::from).toList();
    }

    @GetMapping("/api/datasource/{datasourceId}")
    public ExchangeResponse getDatasourceBy(@PathVariable UUID datasourceId) {
        return ExchangeResponse.from(datasourceQueryRepository.findDatasourceBy(datasourceId));
    }

    @GetMapping("/api/datasource/{datasourceId}/instrument/{ticker}")
    public InstrumentResponse getInstrumentBy(@PathVariable UUID datasourceId, @PathVariable String ticker) {
        InstrumentEntity instrument = datasourceQueryRepository.findInstrumentBy(ticker);
        List<HistoryValueEntity> history = datasourceQueryRepository.findHistory(
            datasourceId,
            instrument,
            dateTimeProvider.nowDate().minusMonths(6)
        );
        List<IntradayValueEntity> intraday = datasourceQueryRepository.findIntraday(
            datasourceId,
            instrument,
            dateTimeProvider.nowDate().atStartOfDay()
        );
        return InstrumentResponse.from(instrument, history, intraday);
    }

    @GetMapping("/api/datasource/{datasourceId}/instrument")
    public List<InstrumentInListResponse> findInstruments(
        @PathVariable UUID datasourceId,
        @RequestParam(required = false) String ticker,
        @RequestParam(required = false) String type,
        @RequestParam(required = false) String shortname,
        @RequestParam(defaultValue = "0") Integer pageNumber,
        @RequestParam(defaultValue = "100") Integer pageSize,
        @RequestParam(defaultValue = "ASC") String orderValue,
        @RequestParam(defaultValue = "shortName") String orderField
    ) {
        return datasourceQueryRepository
            .findInstruments(
                InstrumentFilterParams.builder()
                    .datasourceId(datasourceId)
                    .ticker(ticker)
                    .type(type)
                    .shortName(shortname)
                    .pageNumber(pageNumber)
                    .pageSize(pageSize)
                    .orderDirection(orderValue)
                    .orderField(orderField)
                    .build()
            )
            .stream()
            .map(InstrumentInListResponse::from).toList();
    }
}

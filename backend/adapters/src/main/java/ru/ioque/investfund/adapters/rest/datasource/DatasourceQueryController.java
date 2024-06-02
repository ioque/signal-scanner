package ru.ioque.investfund.adapters.rest.datasource;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.ioque.investfund.adapters.psql.entity.datasource.instrument.InstrumentEntity;
import ru.ioque.investfund.adapters.service.view.PsqlDatasourceViewService;
import ru.ioque.investfund.adapters.service.view.filter.InstrumentFilterParams;
import ru.ioque.investfund.adapters.rest.Pagination;
import ru.ioque.investfund.adapters.rest.datasource.response.DatasourceResponse;
import ru.ioque.investfund.adapters.rest.datasource.response.InstrumentInListResponse;
import ru.ioque.investfund.adapters.rest.datasource.response.InstrumentResponse;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.domain.datasource.value.types.InstrumentType;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@Tag(name = "DatasourceQueryController", description = "Контроллер запросов к модулю \"DATASOURCE\"")
public class DatasourceQueryController {
    PsqlDatasourceViewService psqlDatasourceViewService;
    DateTimeProvider dateTimeProvider;

    @GetMapping("/api/datasource")
    public List<DatasourceResponse> getAllDatasource() {
        return psqlDatasourceViewService.getAllDatasource().stream().map(DatasourceResponse::from).toList();
    }

    @GetMapping("/api/datasource/{datasourceId}")
    public DatasourceResponse getDatasourceBy(@PathVariable UUID datasourceId) {
        return DatasourceResponse.from(psqlDatasourceViewService.findDatasourceBy(datasourceId));
    }

    @GetMapping("/api/datasource/{datasourceId}/instrument/{ticker}")
    public InstrumentResponse getInstrumentBy(@PathVariable UUID datasourceId, @PathVariable String ticker) {
        InstrumentEntity instrument = psqlDatasourceViewService.findInstrumentBy(datasourceId, ticker);
        return InstrumentResponse.from(instrument);
    }

    @GetMapping("/api/datasource/{datasourceId}/instrument")
    public Pagination<InstrumentInListResponse> findInstruments(
        @PathVariable UUID datasourceId,
        @RequestParam(required = false) String ticker,
        @RequestParam(required = false) InstrumentType type,
        @RequestParam(required = false) String shortname,
        @RequestParam(defaultValue = "0") Integer pageNumber,
        @RequestParam(defaultValue = "100") Integer pageSize,
        @RequestParam(defaultValue = "ASC") String orderValue,
        @RequestParam(defaultValue = "details.ticker") String orderField
    ) {
        return psqlDatasourceViewService
            .getPagination(
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
            .to(InstrumentInListResponse::from);
    }
}

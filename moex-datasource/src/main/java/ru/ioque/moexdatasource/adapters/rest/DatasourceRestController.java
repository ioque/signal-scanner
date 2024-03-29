package ru.ioque.moexdatasource.adapters.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ioque.moexdatasource.adapters.rest.response.DailyValueResponse;
import ru.ioque.moexdatasource.adapters.rest.response.InstrumentResponse;
import ru.ioque.moexdatasource.adapters.rest.response.IntradayValueResponse;

import java.util.List;


@RestController("/api")
public class DatasourceRestController {
    @GetMapping("/instruments")
    public List<InstrumentResponse> instruments() {
        return List.of();
    }

    @GetMapping("/instruments/{ticker}/history")
    public List<DailyValueResponse> history() {
        return List.of();
    }

    @GetMapping("/instruments/{ticker}/intraday-values")
    public List<IntradayValueResponse> intradayValues() {
        return List.of();
    }
}

package ru.ioque.investfund.adapters.rest.exchange;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.ioque.investfund.adapters.rest.exchange.request.DisableUpdateInstrumentRequest;
import ru.ioque.investfund.adapters.rest.exchange.request.EnableUpdateInstrumentRequest;
import ru.ioque.investfund.adapters.rest.exchange.request.RegisterDatasourceRequest;
import ru.ioque.investfund.application.modules.exchange.AddDatasourceCommand;
import ru.ioque.investfund.application.modules.exchange.ExchangeManager;

@RestController
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Tag(name="ExchangeCommandController", description="Контроллер команд к модулю \"EXCHANGE\"")
public class ExchangeCommandController {
    ExchangeManager exchangeManager;

    @PostMapping("/api/datasource")
    public void registerDatasource(@RequestBody RegisterDatasourceRequest request) {
        exchangeManager.registerDatasource(
            AddDatasourceCommand.builder()
                .name(request.getName())
                .description(request.getDescription())
                .url(request.getUrl())
                .build()
        );
    }

    @PostMapping("/api/integrate")
    public void integrateInstruments() {
        exchangeManager.integrateInstruments();
    }

    @PostMapping("/api/daily-integrate")
    public void integrateTradingData() {
        exchangeManager.execute();
    }

    @PatchMapping("/api/enable-update")
    public void enableUpdate(@RequestBody EnableUpdateInstrumentRequest request) {
        exchangeManager.enableUpdate(request.getInstrumentIds());
    }

    @PatchMapping("/api/disable-update")
    public void disableUpdate(@RequestBody DisableUpdateInstrumentRequest request) {
        exchangeManager.disableUpdate(request.getInstrumentIds());
    }
}

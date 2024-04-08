package ru.ioque.investfund.adapters.rest.datasource;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.ioque.investfund.adapters.rest.datasource.request.DisableUpdateInstrumentRequest;
import ru.ioque.investfund.adapters.rest.datasource.request.EnableUpdateInstrumentRequest;
import ru.ioque.investfund.adapters.rest.datasource.request.RegisterDatasourceRequest;
import ru.ioque.investfund.application.modules.datasource.AddDatasourceCommand;
import ru.ioque.investfund.application.modules.datasource.DatasourceManager;

@RestController
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Tag(name="ExchangeCommandController", description="Контроллер команд к модулю \"EXCHANGE\"")
public class ExchangeCommandController {
    DatasourceManager datasourceManager;

    @PostMapping("/api/datasource")
    public void registerDatasource(@RequestBody RegisterDatasourceRequest request) {
        datasourceManager.registerDatasource(
            AddDatasourceCommand.builder()
                .name(request.getName())
                .description(request.getDescription())
                .url(request.getUrl())
                .build()
        );
    }

    @PostMapping("/api/integrate")
    public void integrateInstruments() {
        datasourceManager.integrateInstruments();
    }

    @PostMapping("/api/daily-integrate")
    public void integrateTradingData() {
        datasourceManager.execute();
    }

    @PatchMapping("/api/enable-update")
    public void enableUpdate(@RequestBody EnableUpdateInstrumentRequest request) {
        datasourceManager.enableUpdate(request.getTickers());
    }

    @PatchMapping("/api/disable-update")
    public void disableUpdate(@RequestBody DisableUpdateInstrumentRequest request) {
        datasourceManager.disableUpdate(request.getTickers());
    }
}

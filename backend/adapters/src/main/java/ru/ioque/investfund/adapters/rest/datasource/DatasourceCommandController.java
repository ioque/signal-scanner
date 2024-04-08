package ru.ioque.investfund.adapters.rest.datasource;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.ioque.investfund.adapters.rest.datasource.request.DisableUpdateInstrumentRequest;
import ru.ioque.investfund.adapters.rest.datasource.request.EnableUpdateInstrumentRequest;
import ru.ioque.investfund.adapters.rest.datasource.request.RegisterDatasourceRequest;
import ru.ioque.investfund.application.modules.datasource.AddDatasourceCommand;
import ru.ioque.investfund.application.modules.datasource.DatasourceManager;

import java.util.UUID;

@RestController
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Tag(name="DatasourceCommandController", description="Контроллер команд к модулю \"DATASOURCE\"")
public class DatasourceCommandController {
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

    @PostMapping("/api/datasource/{datasourceId}/instruments")
    public void integrateInstruments(@PathVariable UUID datasourceId) {
        datasourceManager.integrateInstruments();
    }

    @PostMapping("/api/datasource/{datasourceId}/trading-data")
    public void integrateTradingData(@PathVariable UUID datasourceId) {
        datasourceManager.integrateTradingData();
    }

    @PatchMapping("/api/datasource/{datasourceId}/enable-update")
    public void enableUpdate(@PathVariable UUID datasourceId, @RequestBody EnableUpdateInstrumentRequest request) {
        datasourceManager.enableUpdate(request.getTickers());
    }

    @PatchMapping("/api/datasource/{datasourceId}/disable-update")
    public void disableUpdate(@PathVariable UUID datasourceId, @RequestBody DisableUpdateInstrumentRequest request) {
        datasourceManager.disableUpdate(request.getTickers());
    }
}

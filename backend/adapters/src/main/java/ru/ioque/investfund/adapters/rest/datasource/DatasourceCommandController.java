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
import ru.ioque.investfund.adapters.rest.datasource.request.SaveDatasourceRequest;
import ru.ioque.investfund.domain.datasource.command.CreateDatasourceCommand;
import ru.ioque.investfund.application.modules.datasource.DatasourceManager;
import ru.ioque.investfund.domain.datasource.command.DisableUpdateInstrumentsCommand;
import ru.ioque.investfund.domain.datasource.command.EnableUpdateInstrumentsCommand;
import ru.ioque.investfund.domain.datasource.command.IntegrateInstrumentsCommand;
import ru.ioque.investfund.domain.datasource.command.IntegrateTradingDataCommand;
import ru.ioque.investfund.domain.datasource.command.UpdateDatasourceCommand;

import java.util.UUID;

@RestController
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Tag(name="DatasourceCommandController", description="Контроллер команд к модулю \"DATASOURCE\"")
public class DatasourceCommandController {
    DatasourceManager datasourceManager;

    @PostMapping("/api/datasource")
    public void registerDatasource(@RequestBody SaveDatasourceRequest request) {
        datasourceManager.registerDatasource(
            CreateDatasourceCommand.builder()
                .name(request.getName())
                .description(request.getDescription())
                .url(request.getUrl())
                .build()
        );
    }

    @PatchMapping("/api/datasource/{datasourceId}")
    public void registerDatasource(@PathVariable UUID datasourceId, @RequestBody SaveDatasourceRequest request) {
        datasourceManager.updateDatasource(
            UpdateDatasourceCommand.builder()
                .id(datasourceId)
                .name(request.getName())
                .description(request.getDescription())
                .url(request.getUrl())
                .build()
        );
    }

    @PostMapping("/api/datasource/{datasourceId}/instrument")
    public void integrateInstruments(@PathVariable UUID datasourceId) {
        datasourceManager.integrateInstruments(new IntegrateInstrumentsCommand(datasourceId));
    }

    @PostMapping("/api/datasource/{datasourceId}/trading-data")
    public void integrateTradingData(@PathVariable UUID datasourceId) {
        datasourceManager.integrateTradingData(new IntegrateTradingDataCommand(datasourceId));
    }

    @PatchMapping("/api/datasource/{datasourceId}/enable-update")
    public void enableUpdate(@PathVariable UUID datasourceId, @RequestBody EnableUpdateInstrumentRequest request) {
        datasourceManager.enableUpdate(new EnableUpdateInstrumentsCommand(datasourceId, request.getTickers()));
    }

    @PatchMapping("/api/datasource/{datasourceId}/disable-update")
    public void disableUpdate(@PathVariable UUID datasourceId, @RequestBody DisableUpdateInstrumentRequest request) {
        datasourceManager.disableUpdate(new DisableUpdateInstrumentsCommand(datasourceId, request.getTickers()));
    }
}

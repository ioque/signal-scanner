package ru.ioque.investfund.adapters.rest.datasource;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.ioque.investfund.adapters.rest.datasource.request.DisableUpdateInstrumentRequest;
import ru.ioque.investfund.adapters.rest.datasource.request.EnableUpdateInstrumentRequest;
import ru.ioque.investfund.adapters.rest.datasource.request.SaveDatasourceRequest;
import ru.ioque.investfund.application.modules.api.CommandBus;
import ru.ioque.investfund.application.modules.datasource.command.CreateDatasource;
import ru.ioque.investfund.application.modules.datasource.command.DisableUpdateInstruments;
import ru.ioque.investfund.application.modules.datasource.command.EnableUpdateInstruments;
import ru.ioque.investfund.application.modules.datasource.command.RunDatasourceWorker;
import ru.ioque.investfund.application.modules.datasource.command.StopDatasourceWorker;
import ru.ioque.investfund.application.modules.datasource.command.SynchronizeDatasource;
import ru.ioque.investfund.application.modules.datasource.command.ExecuteDatasourceWorker;
import ru.ioque.investfund.application.modules.datasource.command.UnregisterDatasource;
import ru.ioque.investfund.application.modules.datasource.command.UpdateDatasource;
import ru.ioque.investfund.domain.datasource.entity.identity.DatasourceId;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

import java.util.UUID;

@RestController
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Tag(name = "DatasourceCommandController", description = "Контроллер команд к модулю \"DATASOURCE\"")
public class DatasourceCommandController {
    CommandBus commandBus;

    @PostMapping("/api/datasource")
    public void createDatasource(@RequestBody SaveDatasourceRequest request) {
        commandBus.execute(
            CreateDatasource.builder()
                .name(request.getName())
                .description(request.getDescription())
                .url(request.getUrl())
                .build()
        );
    }

    @PatchMapping("/api/datasource/{datasourceId}")
    public void updateDatasource(@PathVariable UUID datasourceId, @RequestBody SaveDatasourceRequest request) {
        commandBus.execute(
            UpdateDatasource.builder()
                .id(DatasourceId.from(datasourceId))
                .name(request.getName())
                .description(request.getDescription())
                .url(request.getUrl())
                .build()
        );
    }

    @PostMapping("/api/datasource/{datasourceId}/instrument")
    public void integrateInstruments(@PathVariable UUID datasourceId) {
        commandBus.execute(new SynchronizeDatasource(DatasourceId.from(datasourceId)));
    }

    @PostMapping("/api/datasource/{datasourceId}/run")
    public void runDatasource(@PathVariable UUID datasourceId) {
        commandBus.execute(new RunDatasourceWorker(DatasourceId.from(datasourceId)));
    }

    @PostMapping("/api/datasource/{datasourceId}/stop")
    public void stopDatasource(@PathVariable UUID datasourceId) {
        commandBus.execute(new StopDatasourceWorker(DatasourceId.from(datasourceId)));
    }

    @PostMapping("/api/datasource/{datasourceId}/trading-data")
    public void integrateTradingData(@PathVariable UUID datasourceId) {
        commandBus.execute(new ExecuteDatasourceWorker(DatasourceId.from(datasourceId)));
    }

    @PatchMapping("/api/datasource/{datasourceId}/enable-update")
    public void enableUpdate(@PathVariable UUID datasourceId, @RequestBody EnableUpdateInstrumentRequest request) {
        commandBus.execute(new EnableUpdateInstruments(
                DatasourceId.from(datasourceId),
                request.getTickers().stream().map(Ticker::from).toList()
            )
        );
    }

    @PatchMapping("/api/datasource/{datasourceId}/disable-update")
    public void disableUpdate(@PathVariable UUID datasourceId, @RequestBody DisableUpdateInstrumentRequest request) {
        commandBus.execute(new DisableUpdateInstruments(
                DatasourceId.from(datasourceId),
                request.getTickers().stream().map(Ticker::from).toList()
            )
        );
    }

    @DeleteMapping("/api/datasource/{datasourceId}")
    public void removeDatasource(@PathVariable UUID datasourceId) {
        commandBus.execute(new UnregisterDatasource(DatasourceId.from(datasourceId)));
    }
}

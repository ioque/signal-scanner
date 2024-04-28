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
import ru.ioque.investfund.application.api.command.CommandBus;
import ru.ioque.investfund.application.datasource.command.CreateDatasourceCommand;
import ru.ioque.investfund.application.datasource.command.DisableUpdateInstrumentsCommand;
import ru.ioque.investfund.application.datasource.command.EnableUpdateInstrumentsCommand;
import ru.ioque.investfund.application.datasource.command.IntegrateInstrumentsCommand;
import ru.ioque.investfund.application.datasource.command.IntegrateTradingDataCommand;
import ru.ioque.investfund.application.datasource.command.UnregisterDatasourceCommand;
import ru.ioque.investfund.application.datasource.command.UpdateDatasourceCommand;
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
            CreateDatasourceCommand.builder()
                .name(request.getName())
                .description(request.getDescription())
                .url(request.getUrl())
                .build()
        );
    }

    @PatchMapping("/api/datasource/{datasourceId}")
    public void updateDatasource(@PathVariable UUID datasourceId, @RequestBody SaveDatasourceRequest request) {
        commandBus.execute(
            UpdateDatasourceCommand.builder()
                .id(DatasourceId.from(datasourceId))
                .name(request.getName())
                .description(request.getDescription())
                .url(request.getUrl())
                .build()
        );
    }

    @PostMapping("/api/datasource/{datasourceId}/instrument")
    public void integrateInstruments(@PathVariable UUID datasourceId) {
        commandBus.execute(new IntegrateInstrumentsCommand(DatasourceId.from(datasourceId)));
    }

    @PostMapping("/api/datasource/{datasourceId}/trading-data")
    public void integrateTradingData(@PathVariable UUID datasourceId) {
        commandBus.execute(new IntegrateTradingDataCommand(DatasourceId.from(datasourceId)));
    }

    @PatchMapping("/api/datasource/{datasourceId}/enable-update")
    public void enableUpdate(@PathVariable UUID datasourceId, @RequestBody EnableUpdateInstrumentRequest request) {
        commandBus.execute(new EnableUpdateInstrumentsCommand(
                DatasourceId.from(datasourceId),
                request.getTickers().stream().map(Ticker::from).toList()
            )
        );
    }

    @PatchMapping("/api/datasource/{datasourceId}/disable-update")
    public void disableUpdate(@PathVariable UUID datasourceId, @RequestBody DisableUpdateInstrumentRequest request) {
        commandBus.execute(new DisableUpdateInstrumentsCommand(
                DatasourceId.from(datasourceId),
                request.getTickers().stream().map(Ticker::from).toList()
            )
        );
    }

    @DeleteMapping("/api/datasource/{datasourceId}")
    public void removeDatasource(@PathVariable UUID datasourceId) {
        commandBus.execute(new UnregisterDatasourceCommand(DatasourceId.from(datasourceId)));
    }
}

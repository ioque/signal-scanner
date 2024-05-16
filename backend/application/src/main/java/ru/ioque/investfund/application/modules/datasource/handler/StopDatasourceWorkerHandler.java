package ru.ioque.investfund.application.modules.datasource.handler;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.modules.api.CommandHandler;
import ru.ioque.investfund.application.modules.api.Result;
import ru.ioque.investfund.application.modules.datasource.DatasourceWorkerManager;
import ru.ioque.investfund.application.modules.datasource.command.StopDatasourceWorker;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class StopDatasourceWorkerHandler extends CommandHandler<StopDatasourceWorker> {
    DatasourceWorkerManager datasourceWorkerManager;

    public StopDatasourceWorkerHandler(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerProvider loggerProvider,
        DatasourceWorkerManager datasourceWorkerManager
    ) {
        super(dateTimeProvider, validator, loggerProvider);
        this.datasourceWorkerManager = datasourceWorkerManager;
    }

    @Override
    protected Result businessProcess(StopDatasourceWorker command) {
        datasourceWorkerManager.deleteWorker(command.getDatasourceId());
        return Result.success();
    }
}

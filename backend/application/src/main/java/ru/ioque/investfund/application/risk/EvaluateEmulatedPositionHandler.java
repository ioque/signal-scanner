package ru.ioque.investfund.application.risk;

import jakarta.validation.Validator;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.EmulatedPositionRepository;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.api.command.CommandHandler;
import ru.ioque.investfund.application.risk.command.EvaluateEmulatedPosition;
import ru.ioque.investfund.domain.core.ApplicationLog;
import ru.ioque.investfund.domain.core.InfoLog;
import ru.ioque.investfund.domain.risk.EmulatedPosition;

import java.util.List;

@Component
public class EvaluateEmulatedPositionHandler extends CommandHandler<EvaluateEmulatedPosition> {
    EmulatedPositionRepository emulatedPositionRepository;

    public EvaluateEmulatedPositionHandler(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerProvider loggerProvider,
        EmulatedPositionRepository emulatedPositionRepository
    ) {
        super(dateTimeProvider, validator, loggerProvider);
        this.emulatedPositionRepository = emulatedPositionRepository;
    }

    @Override
    protected List<ApplicationLog> businessProcess(EvaluateEmulatedPosition command) {
        List<EmulatedPosition> emulatedPositions = emulatedPositionRepository.findAllBy(command.getInstrumentId());
        emulatedPositions.forEach(emulatedPosition -> emulatedPosition.updateLastPrice(command.getPrice()));
        emulatedPositionRepository.saveAll(emulatedPositions);
        return List.of(
            new InfoLog(
                dateTimeProvider.nowDateTime(),
                String.format("Выполнена переоценка прибыльности позиций по инструменту[id=%s]", command.getInstrumentId())
            )
        );
    }
}

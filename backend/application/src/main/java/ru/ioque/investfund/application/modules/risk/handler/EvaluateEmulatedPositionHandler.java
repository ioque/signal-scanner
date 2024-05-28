package ru.ioque.investfund.application.modules.risk.handler;

import jakarta.validation.Validator;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.journal.EmulatedPositionJournal;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.modules.api.CommandHandler;
import ru.ioque.investfund.application.modules.api.Result;
import ru.ioque.investfund.application.modules.risk.command.EvaluateEmulatedPosition;
import ru.ioque.investfund.domain.risk.EmulatedPosition;

import java.util.List;

@Component
public class EvaluateEmulatedPositionHandler extends CommandHandler<EvaluateEmulatedPosition> {
    EmulatedPositionJournal emulatedPositionJournal;

    public EvaluateEmulatedPositionHandler(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerProvider loggerProvider,
        EmulatedPositionJournal emulatedPositionJournal
    ) {
        super(dateTimeProvider, validator, loggerProvider);
        this.emulatedPositionJournal = emulatedPositionJournal;
    }

    @Override
    protected Result businessProcess(EvaluateEmulatedPosition command) {
        List<EmulatedPosition> emulatedPositions = emulatedPositionJournal.findAllBy(command.getInstrumentId());
        emulatedPositions.forEach(emulatedPosition -> {
            if (emulatedPosition.getIsOpen()) {
                emulatedPosition.updateLastPrice(command.getPrice());
                emulatedPositionJournal.publish(emulatedPosition);
            }
        });
        return Result.success();
    }
}

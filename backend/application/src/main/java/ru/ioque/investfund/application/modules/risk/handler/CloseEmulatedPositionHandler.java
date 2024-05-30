package ru.ioque.investfund.application.modules.risk.handler;

import jakarta.validation.Validator;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.journal.EmulatedPositionJournal;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.modules.api.CommandHandler;
import ru.ioque.investfund.application.modules.api.Result;
import ru.ioque.investfund.application.modules.risk.command.CloseEmulatedPosition;
import ru.ioque.investfund.domain.core.EntityNotFoundException;
import ru.ioque.investfund.domain.position.EmulatedPosition;

@Component
public class CloseEmulatedPositionHandler extends CommandHandler<CloseEmulatedPosition> {
    final EmulatedPositionJournal emulatedPositionJournal;

    public CloseEmulatedPositionHandler(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerProvider loggerProvider,
        EmulatedPositionJournal emulatedPositionJournal
    ) {
        super(dateTimeProvider, validator, loggerProvider);
        this.emulatedPositionJournal = emulatedPositionJournal;
    }

    @Override
    protected Result businessProcess(CloseEmulatedPosition command) {
        final EmulatedPosition emulatedPosition = emulatedPositionJournal
            .findActualBy(command.getInstrumentId(), command.getScannerId())
            .orElseThrow(
                () -> new EntityNotFoundException(
                    String.format(
                        "Позиции по инструменту[id=%s] и сканеру[id=%s] не существует.",
                        command.getInstrumentId(),
                        command.getScannerId()
                    )
                )
            );
        emulatedPosition.closePosition(command.getPrice());
        emulatedPositionJournal.publish(emulatedPosition);
        return Result.success();
    }
}

package ru.ioque.investfund.application.risk;

import jakarta.validation.Validator;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.EmulatedPositionRepository;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.adapters.UUIDProvider;
import ru.ioque.investfund.application.api.command.CommandHandler;
import ru.ioque.investfund.application.risk.command.CloseEmulatedPosition;
import ru.ioque.investfund.domain.core.EntityNotFoundException;
import ru.ioque.investfund.domain.core.InfoLog;
import ru.ioque.investfund.domain.risk.EmulatedPosition;

@Component
public class CloseEmulatedPositionHandler extends CommandHandler<CloseEmulatedPosition> {
    final EmulatedPositionRepository emulatedPositionRepository;

    public CloseEmulatedPositionHandler(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerProvider loggerProvider,
        UUIDProvider uuidProvider,
        EmulatedPositionRepository emulatedPositionRepository
    ) {
        super(dateTimeProvider, validator, loggerProvider, uuidProvider);
        this.emulatedPositionRepository = emulatedPositionRepository;
    }

    @Override
    protected void businessProcess(CloseEmulatedPosition command) {
        final EmulatedPosition emulatedPosition = emulatedPositionRepository
            .findBy(command.getInstrumentId(), command.getScannerId())
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
        emulatedPositionRepository.save(emulatedPosition);
        loggerProvider.log(new InfoLog(
            dateTimeProvider.nowDateTime(),
            String.format("Закрыта эмуляция позиции[id=%s]", emulatedPosition.getId()),
            command.getTrack()
        ));
    }
}

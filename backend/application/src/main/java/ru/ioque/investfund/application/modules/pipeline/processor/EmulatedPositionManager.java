package ru.ioque.investfund.application.modules.pipeline.processor;

import java.util.Optional;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.journal.EmulatedPositionJournal;
import ru.ioque.investfund.application.adapters.journal.Processor;
import ru.ioque.investfund.application.modules.pipeline.PipelineContext;
import ru.ioque.investfund.domain.position.EmulatedPosition;
import ru.ioque.investfund.domain.scanner.entity.Signal;

@Component
@AllArgsConstructor
public class EmulatedPositionManager implements Processor<Signal> {
    PipelineContext pipelineContext;
    EmulatedPositionJournal emulatedPositionJournal;

    @Override
    public void process(Signal signal) {
        final Optional<EmulatedPosition> emulatedPosition = pipelineContext
            .getEmulatedPosition(signal.getInstrumentId(), signal.getScannerId());

        if (emulatedPosition.isPresent()) {
            if (!emulatedPosition.get().getIsOpen()) {
                openPosition(signal);
                return;
            }
            if (signal.isSell()) {
                closePosition(emulatedPosition.get(), signal.getPrice());
            }
        }
        if (signal.isBuy()) {
            openPosition(signal);
        }
    }

    private void openPosition(Signal signal) {
        publish(EmulatedPosition.from(signal));
    }

    private void closePosition(EmulatedPosition emulatedPosition, Double closePrice) {
        emulatedPosition.closePosition(closePrice);
        publish(emulatedPosition);
    }

    private void publish(EmulatedPosition emulatedPosition) {
        pipelineContext.addEmulatedPosition(emulatedPosition);
        emulatedPositionJournal.publish(emulatedPosition);
    }
}

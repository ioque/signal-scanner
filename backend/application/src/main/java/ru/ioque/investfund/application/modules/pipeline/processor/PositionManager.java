package ru.ioque.investfund.application.modules.pipeline.processor;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.journal.EmulatedPositionJournal;
import ru.ioque.investfund.application.modules.pipeline.core.Processor;
import ru.ioque.investfund.application.modules.pipeline.PipelineContext;
import ru.ioque.investfund.domain.position.EmulatedPosition;
import ru.ioque.investfund.domain.scanner.entity.Signal;

@Component
@AllArgsConstructor
public class PositionManager implements Processor<Signal> {
    PipelineContext pipelineContext;
    EmulatedPositionJournal emulatedPositionJournal;

    @Override
    public void process(Signal signal) {
        final EmulatedPosition emulatedPosition = pipelineContext
            .getEmulatedPosition(signal.getInstrumentId(), signal.getScannerId())
            .filter(EmulatedPosition::getIsOpen)
            .map(row -> {
                row.closePosition(signal.getPrice());
                return row;
            })
            .orElse(EmulatedPosition.from(signal));
        pipelineContext.addEmulatedPosition(emulatedPosition);
        emulatedPositionJournal.publish(emulatedPosition);
    }
}

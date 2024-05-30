package ru.ioque.investfund.adapters.kafka.journal;

import java.util.List;
import java.util.Optional;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.journal.EmulatedPositionJournal;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.position.EmulatedPosition;
import ru.ioque.investfund.domain.scanner.entity.ScannerId;

@Component
@Profile("!tests")
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class KafkaEmulatedPositionJournal implements EmulatedPositionJournal {

    @Override
    public void publish(EmulatedPosition emulatedPosition) {

    }

    @Override
    public List<EmulatedPosition> findAllBy(InstrumentId instrumentId) {
        return List.of();
    }

    @Override
    public Optional<EmulatedPosition> findActualBy(InstrumentId instrumentId, ScannerId scannerId) {
        return Optional.empty();
    }

    @Override
    public EmulatedPositionId nextId() {
        return null;
    }
}

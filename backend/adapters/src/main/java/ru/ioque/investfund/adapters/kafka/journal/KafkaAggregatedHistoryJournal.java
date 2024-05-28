package ru.ioque.investfund.adapters.kafka.journal;

import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.journal.AggregatedHistoryJournal;
import ru.ioque.investfund.domain.datasource.value.history.AggregatedHistory;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

@Component
@Profile("!tests")
@AllArgsConstructor
public class KafkaAggregatedHistoryJournal implements AggregatedHistoryJournal {

    @Override
    public void publish(AggregatedHistory aggregatedHistory) {

    }

    @Override
    public List<AggregatedHistory> findAllBy(Ticker ticker) {
        return List.of();
    }

    @Override
    public Optional<AggregatedHistory> findActualBy(Ticker ticker) {
        return Optional.empty();
    }
}

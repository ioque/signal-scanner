package ru.ioque.investfund.adapters.kafka.journal;

import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.journal.AggregatedTotalsJournal;
import ru.ioque.investfund.domain.datasource.value.history.AggregatedTotals;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

@Component
@Profile("!tests")
@AllArgsConstructor
public class KafkaAggregatedTotalsJournal implements AggregatedTotalsJournal {

    @Override
    public void publish(AggregatedTotals aggregatedTotals) {

    }

    @Override
    public List<AggregatedTotals> findAllBy(Ticker ticker) {
        return List.of();
    }

    @Override
    public Optional<AggregatedTotals> findActualBy(Ticker ticker) {
        return Optional.empty();
    }
}

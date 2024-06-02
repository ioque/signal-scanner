package ru.ioque.investfund.adapters.psql;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import ru.ioque.investfund.adapters.psql.dao.JpaIntradayValueRepository;
import ru.ioque.investfund.adapters.psql.entity.datasource.intradayvalue.IntradayDataEntity;
import ru.ioque.investfund.application.adapters.IntradayDataJournal;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;

@Slf4j
@Component
@AllArgsConstructor
public class PsqlIntradayDataJournal implements IntradayDataJournal {

    private final JpaIntradayValueRepository intradayValueRepository;
    private final Sinks.Many<IntradayData> sink = Sinks.many().multicast().onBackpressureBuffer();

    @Override
    @Transactional
    public void publish(IntradayData intradayData) {
        intradayValueRepository.save(IntradayDataEntity.fromDomain(intradayData));
        sink.tryEmitNext(intradayData).orThrow();
    }

    @Override
    public Flux<IntradayData> stream() {
        return sink.asFlux();
    }
}

package ru.ioque.investfund.adapters.integration;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;

@Slf4j
@Getter
@Component
public class FakeIntradayJournal implements IntradayJournal {

    @Override
    public void publish(IntradayData intradayData) {
        log.info(intradayData.toString());
    }

    @Override
    public Flux<IntradayData> stream() {
        return Flux.empty();
    }
}

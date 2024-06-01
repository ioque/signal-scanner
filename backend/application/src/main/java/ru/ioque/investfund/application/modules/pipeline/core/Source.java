package ru.ioque.investfund.application.modules.pipeline.core;

import reactor.core.publisher.Flux;

public interface Source<ENTITY> {
    void publish(ENTITY intradayData);
    Flux<ENTITY> stream();
}

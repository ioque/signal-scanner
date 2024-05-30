package ru.ioque.investfund.application.modules.pipeline.core;

import reactor.core.publisher.Flux;

public interface Journal<ENTITY> {
    void publish(ENTITY entity);
    Flux<ENTITY> stream();
}

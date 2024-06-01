package ru.ioque.investfund.application.modules.pipeline.core;

import reactor.core.publisher.Flux;

public interface Source<ENTITY> {
    Flux<ENTITY> stream();
}

package ru.ioque.investfund.application.modules.pipeline.core;

public interface Sink<ENTITY> {
    void consume(ENTITY entity);
}

package ru.ioque.investfund.application.modules.pipeline.core;

public interface Processor<ENTITY> {
    void process(ENTITY entity);
}

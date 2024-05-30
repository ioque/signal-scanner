package ru.ioque.investfund.domain.pipeline;

public interface Processor<ENTITY> {
    void process(ENTITY entity);
}

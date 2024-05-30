package ru.ioque.investfund.domain.pipeline;

public interface Journal<ENTITY> {
    void publish(ENTITY entity);
    void subscribe(Processor<ENTITY> processor);
}

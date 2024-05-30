package ru.ioque.investfund.application.adapters.journal;

public interface Journal<ENTITY> {
    void publish(ENTITY entity);
    void subscribe(Processor<ENTITY> processor);
}

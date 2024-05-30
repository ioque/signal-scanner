package ru.ioque.investfund.application.adapters.journal;

public interface Processor<ENTITY> {
    void process(ENTITY entity);
}

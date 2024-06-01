package ru.ioque.investfund.application.modules.pipeline.core;

public interface Subscriber<ENTITY> {
    void receive(ENTITY entity);
}

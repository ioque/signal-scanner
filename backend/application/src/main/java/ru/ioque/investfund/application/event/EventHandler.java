package ru.ioque.investfund.application.event;

public abstract class EventHandler<E> {
    public abstract void handle(E event);
}

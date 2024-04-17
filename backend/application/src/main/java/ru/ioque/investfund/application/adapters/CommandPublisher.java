package ru.ioque.investfund.application.adapters;

import ru.ioque.investfund.domain.core.Command;

public interface CommandPublisher {
    void publish(Command command);
}

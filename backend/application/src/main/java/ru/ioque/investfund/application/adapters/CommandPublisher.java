package ru.ioque.investfund.application.adapters;

import ru.ioque.investfund.application.api.command.Command;

public interface CommandPublisher {
    void publish(Command command);
}

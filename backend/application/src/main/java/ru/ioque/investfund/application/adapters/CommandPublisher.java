package ru.ioque.investfund.application.adapters;

import ru.ioque.investfund.application.modules.api.Command;

public interface CommandPublisher {
    void publish(Command command);
}

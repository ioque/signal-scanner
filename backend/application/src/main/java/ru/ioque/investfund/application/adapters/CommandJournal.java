package ru.ioque.investfund.application.adapters;

import ru.ioque.investfund.application.modules.api.Command;

public interface CommandJournal {
    void publish(Command command);
}

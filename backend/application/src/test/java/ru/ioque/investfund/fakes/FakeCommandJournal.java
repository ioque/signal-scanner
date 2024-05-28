package ru.ioque.investfund.fakes;

import ru.ioque.investfund.application.adapters.CommandJournal;
import ru.ioque.investfund.application.modules.api.Command;

public class FakeCommandJournal implements CommandJournal {

    @Override
    public void publish(Command command) {

    }
}

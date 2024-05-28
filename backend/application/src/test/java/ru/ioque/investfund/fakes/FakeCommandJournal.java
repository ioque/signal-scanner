package ru.ioque.investfund.fakes;

import java.util.HashSet;
import java.util.Set;

import ru.ioque.investfund.application.adapters.CommandJournal;
import ru.ioque.investfund.application.modules.api.Command;

public class FakeCommandJournal implements CommandJournal {
    private final Set<Command> commands = new HashSet<>();

    @Override
    public void publish(Command command) {
        commands.add(command);
    }
}

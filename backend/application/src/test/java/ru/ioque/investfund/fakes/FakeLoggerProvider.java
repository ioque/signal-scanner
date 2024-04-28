package ru.ioque.investfund.fakes;

import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.domain.core.ApplicationLog;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FakeLoggerProvider implements LoggerProvider {
    public final List<ApplicationLog> log = new ArrayList<>();

    public void clearLogs() {
        log.clear();
    }

    @Override
    public void log(ApplicationLog logPart) {
        log.add(logPart);
    }

    @Override
    public void log(UUID trackId, ApplicationLog logPart) {
        log.add(logPart);
    }
}

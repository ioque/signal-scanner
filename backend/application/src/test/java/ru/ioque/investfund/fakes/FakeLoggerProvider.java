package ru.ioque.investfund.fakes;

import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.domain.core.ApplicationLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FakeLoggerProvider implements LoggerProvider {
    public final Map<UUID, List<ApplicationLog>> logs = new HashMap<>();

    public void clearLogs() {
        logs.clear();
    }

    @Override
    public void log(UUID trackId, ApplicationLog logPart) {
        logs.computeIfAbsent(trackId, k -> new ArrayList<>());
        logs.get(trackId).add(logPart);
    }
}

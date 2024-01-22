package ru.ioque.investfund.fakes;

import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.share.logger.ApplicationLog;

import java.util.ArrayList;
import java.util.List;

public class FakeLoggerProvider implements LoggerProvider {
    public final List<ApplicationLog> log = new ArrayList<>();

    public void clearLogs() {
        log.clear();
    }

    public boolean logContainsMessageParts(String... msgParts) {
        return log.stream().map(ApplicationLog::getMsg).toList().containsAll(List.of(msgParts));
    }

    @Override
    public void addToLog(ApplicationLog logPart) {
        log.add(logPart);
    }
}

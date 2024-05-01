package ru.ioque.investfund.application.adapters;

import ru.ioque.investfund.domain.core.ApplicationLog;

import java.util.UUID;

public interface LoggerProvider {
    void log(UUID trackId, ApplicationLog log);
}

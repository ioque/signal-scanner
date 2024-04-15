package ru.ioque.investfund.application.adapters;

import ru.ioque.investfund.domain.core.ApplicationLog;

public interface LoggerProvider {
    void log(ApplicationLog log);
}

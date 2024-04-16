package ru.ioque.investfund.adapters.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.domain.core.ApplicationLog;
import ru.ioque.investfund.domain.core.ErrorLog;
import ru.ioque.investfund.domain.core.InfoLog;
import ru.ioque.investfund.domain.core.WarningLog;

@Component
public class AsyncFileLoggerProvider implements LoggerProvider {
    private final Logger log = LoggerFactory.getLogger("ASYNC_FILE_LOGGER");

    @Override
    public void log(ApplicationLog logPart) {
        if (logPart instanceof InfoLog infoLog) {
            log.info(infoLog.getMsg());
        }
        if (logPart instanceof WarningLog warningLog) {
            log.warn(warningLog.getMsg());
        }
        if (logPart instanceof ErrorLog errorLog) {
            log.error(errorLog.getMsg(), errorLog.getError());
        }
    }
}

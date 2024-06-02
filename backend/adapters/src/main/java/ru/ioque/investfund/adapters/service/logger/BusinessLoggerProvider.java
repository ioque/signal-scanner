package ru.ioque.investfund.adapters.service.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.domain.core.ApplicationLog;
import ru.ioque.investfund.domain.core.ErrorLog;
import ru.ioque.investfund.domain.core.InfoLog;
import ru.ioque.investfund.domain.core.WarningLog;

import java.util.UUID;

@Component
public class BusinessLoggerProvider implements LoggerProvider {
    private final Logger log = LoggerFactory.getLogger("BUSINESS_LOGGER");

    @Override
    public void log(UUID trackId, ApplicationLog logPart) {
        MDC.put("trackId", trackId.toString());
        if (logPart instanceof InfoLog infoLog) {
            log.info(infoLog.getMsg());
        }
        if (logPart instanceof WarningLog warningLog) {
            log.warn(warningLog.getMsg());
        }
        if (logPart instanceof ErrorLog errorLog) {
            log.error(errorLog.getMsg(), errorLog.getError());
        }
        MDC.clear();
    }
}

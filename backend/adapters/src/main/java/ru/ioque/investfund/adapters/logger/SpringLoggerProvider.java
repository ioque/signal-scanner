package ru.ioque.investfund.adapters.logger;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.domain.core.ApplicationLog;
import ru.ioque.investfund.domain.core.ErrorLog;
import ru.ioque.investfund.domain.core.InfoLog;
import ru.ioque.investfund.domain.core.WarningLog;

@Slf4j
@Component
public class SpringLoggerProvider implements LoggerProvider {
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

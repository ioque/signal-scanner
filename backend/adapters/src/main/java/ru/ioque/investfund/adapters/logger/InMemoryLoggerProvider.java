package ru.ioque.investfund.adapters.logger;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.share.logger.ApplicationLog;
import ru.ioque.investfund.application.share.logger.ErrorLog;
import ru.ioque.investfund.application.share.logger.InfoLog;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.share.logger.WarningLog;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Component
public class InMemoryLoggerProvider implements LoggerProvider {
    public List<ApplicationLog> logList = new CopyOnWriteArrayList<>();

    @Override
    public void log(ApplicationLog logPart) {
        logList.add(logPart);
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

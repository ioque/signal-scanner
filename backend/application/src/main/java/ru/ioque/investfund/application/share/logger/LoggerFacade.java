package ru.ioque.investfund.application.share.logger;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;

import java.time.LocalDateTime;

@Component
@AllArgsConstructor
public class LoggerFacade {
    DateTimeProvider dateTimeProvider;
    LoggerProvider loggerProvider;

    public void log(ApplicationLog infoLog) {
        loggerProvider.addToLog(infoLog);
    }

    public void logRunScanning(LocalDateTime now) {
        log(new InfoLog(
            dateTimeProvider.nowDateTime(),
            String.format(
                "Начато сканирование хранилища финансовых инструментов, текущее время: %s.",
                now
            )
        ));
    }

    public void logFinishedScanning(LocalDateTime now) {
        log(new InfoLog(
            dateTimeProvider.nowDateTime(),
            String.format(
                "Завершено сканирование хранилища финансовых инструментов, текущее время: %s.",
                now
            )
        ));
    }

    public void logRunWorkScanner(SignalScanner scanner) {
        log(
            new InfoLog(
                dateTimeProvider.nowDateTime(),
                String.format(
                    "Начал работу сканер сигналов c идентификатором %s, алгоритм сканера - %s.",
                    scanner.getId(),
                    scanner.getProperties().getType()
                )
            )
        );
    }

    public void logFinishWorkScanner(SignalScanner scanner) {
        log(
            new InfoLog(
                dateTimeProvider.nowDateTime(),
                String.format(
                    "Завершил работу сканер сигналов c идентификатором %s, алгоритм сканера - %s. Текущие сигналы: %s",
                    scanner.getId(),
                    scanner.getProperties().getType(),
                    scanner.getSignals()
                )
            )
        );
    }
}

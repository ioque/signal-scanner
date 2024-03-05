package ru.ioque.investfund.application.share.logger;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.modules.scanner.AddScannerCommand;
import ru.ioque.investfund.application.modules.scanner.UpdateScannerCommand;
import ru.ioque.investfund.domain.exchange.entity.Instrument;
import ru.ioque.investfund.domain.scanner.financial.entity.SignalScannerBot;
import ru.ioque.investfund.domain.schedule.ScheduleUnit;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
@AllArgsConstructor
public class LoggerFacade {
    LoggerProvider loggerProvider;

    public void logNotActualScheduleUnit(LocalDateTime scheduleDateTime) {
        log(new InfoLog(String.format("Нет актуальных задач. Текущее время: %s", scheduleDateTime)));
    }

    public void logSaveNewDataScanner(UUID id) {
        log(
            new InfoLog(
                String.format(
                    "Успешно создан и сохранен в постоянное хранилище новый сканер сигналов, идентификатор объекта: %s",
                    id
                )
            )
        );
    }

    public void logRunExecuteSchedule(LocalDateTime executeDateTime) {
        log(new InfoLog(
            String.format(
                "Начато выполнение актуальных задач расписания. Текущее время - %s, текущая дата - %s.",
                executeDateTime.toLocalTime(),
                executeDateTime.toLocalDate()
            )
        ));
    }

    public void logFinishExecuteSchedule(LocalDateTime start, LocalDateTime stop) {
        log(new InfoLog(String.format(
            "Завершено выполнение актуальных задач расписания. Время начала выполнения задач - %s. Текущее время - %s, текущая дата - %s. Время выполнения < %s мс.",
            start.toLocalTime(),
            stop.toLocalTime(),
            stop.toLocalDate(),
            Duration.between(start, stop).toMillis() > 100 ? Duration.between(start, stop).toMillis() : "1"
        )));
    }

    public void logRunCreateSignalScanner(AddScannerCommand command) {
        log(
            new InfoLog(
                String
                    .format(
                        "Начато создание сканера сигналов, идентификаторы инструментов %s.",
                        command.getIds()
                    )
            )
        );
    }

    public void log(ApplicationLog infoLog) {
        loggerProvider.addToLog(infoLog);
    }

    public void logAddNewScheduleUnit(ScheduleUnit scheduleUnit) {
        log(
            new InfoLog(
                String.format(
                    "Расписание модуля %s сохранено.",
                    scheduleUnit.getSystemModule()
                )
            )
        );
    }

    public void logRunUpdateMarketData(Instrument instrument, LocalDateTime dateTime) {
        log(
            new InfoLog(
                String.format(
                    "Начато обновление торговых данных инструмента %s, %s. %s",
                    instrument.getTicker(),
                    dateTime.toString(),
                    instrument.printMarketStat()
                )
            )
        );
    }

    public void logFinishUpdateMarketData(Instrument instrument, LocalDateTime dateTime) {
        log(
            new InfoLog(
                String.format(
                    "Завершено обновление торговых данных инструмента %s, %s. %s",
                    instrument.getTicker(),
                    dateTime.toString(),
                    instrument.printMarketStat()
                )
            )
        );
    }

    public void logRunScanning(LocalDateTime now) {
        log(new InfoLog(String.format(
            "Начато сканирование хранилища финансовых инструментов, текущее время: %s.",
            now
        )));
    }

    public void logFinishedScanning(LocalDateTime now) {
        log(new InfoLog(String.format(
            "Завершено сканирование хранилища финансовых инструментов, текущее время: %s.",
            now
        )));
    }

    public void logRunSynchronizeWithDataSource(String sourceName, LocalDateTime today) {
        log(new InfoLog(String.format(
            "Начата синхронизация с источником данных \"%s\". Текущее время: %s.",
            sourceName,
            today
        )));
    }

    public void logFinishSynchronizeWithDataSource(String sourceName, LocalDateTime today) {
        log(new InfoLog(String.format(
            "Завершена синхронизация с источником данных \"%s\". Текущее время: %s.",
            sourceName,
            today
        )));
    }

    public void logRunWorkScanner(SignalScannerBot scanner) {
        log(
            new InfoLog(
                String.format(
                    "Начал работу сканер сигналов c идентификатором %s, алгоритм сканера - %s.",
                    scanner.getId(),
                    scanner.getConfig().factorySearchAlgorithm().getName()
                )
            )
        );
    }

    public void logFinishWorkScanner(SignalScannerBot scanner) {
        log(
            new InfoLog(
                String.format(
                    "Завершил работу сканер сигналов c идентификатором %s, алгоритм сканера - %s. Текущие сигналы: %s",
                    scanner.getId(),
                    scanner.getConfig().factorySearchAlgorithm().getName(),
                    scanner.getSignals()
                )
            )
        );
    }

    public void logUpdateSignalScanner(UpdateScannerCommand command) {
        log(
            new InfoLog("Обновлен сканер сигналов с идентификатором " + command.getId() + ".")
        );
    }
}

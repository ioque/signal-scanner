package ru.ioque.investfund.adapters.scheduler;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.adapters.psql.dao.JpaDatasourceRepository;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.modules.api.CommandBus;
import ru.ioque.investfund.application.modules.datasource.command.PublishIntradayData;
import ru.ioque.investfund.application.modules.telegrambot.command.PublishDailyReport;
import ru.ioque.investfund.application.modules.telegrambot.command.PublishHourlyReport;
import ru.ioque.investfund.domain.datasource.entity.identity.DatasourceId;

import java.util.concurrent.TimeUnit;

@Component
@AllArgsConstructor
@Profile("production")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SpringScheduler {
    Logger log = LoggerFactory.getLogger("SCHEDULE_LOGGER");
    DateTimeProvider dateTimeProvider;
    JpaDatasourceRepository jpaDatasourceRepository;
    CommandBus commandBus;

    @Scheduled(fixedDelay = 30, timeUnit = TimeUnit.MINUTES)
    public void healthcheck() {
        log.info("scheduler is working");
    }

    @Scheduled(cron = "0 */15 10-20 * * MON-SUN", zone = "Europe/Moscow")
    public void integrateTradingData() {
        log.info("start integrate trading data, current time {}", dateTimeProvider.nowDateTime());
        jpaDatasourceRepository.findAll().forEach(datasource -> commandBus.execute(
            new PublishIntradayData(
                DatasourceId.from(datasource.getId())
            )
        ));
        log.info("finish integrate trading data, current time {}", dateTimeProvider.nowDateTime());
    }

    @Scheduled(cron = "0 0 11-20 * * MON-SUN", zone = "Europe/Moscow")
    public void publishHourlyReport() {
        log.info("start publish hourly report, current time {}", dateTimeProvider.nowDateTime());
        commandBus.execute(new PublishHourlyReport());
        log.info("finish publish hourly report, current time {}", dateTimeProvider.nowDateTime());
    }

    @Scheduled(cron = "0 15 20 * * MON-SUN", zone = "Europe/Moscow")
    public void publishDailyReport() {
        log.info("start publish daily report, current time {}", dateTimeProvider.nowDateTime());
        commandBus.execute(new PublishDailyReport());
        log.info("finish publish daily report, current time {}", dateTimeProvider.nowDateTime());
    }
}

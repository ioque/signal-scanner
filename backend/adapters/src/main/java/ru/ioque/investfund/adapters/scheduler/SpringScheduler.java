package ru.ioque.investfund.adapters.scheduler;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.adapters.persistence.repositories.JpaDatasourceRepository;
import ru.ioque.investfund.application.adapters.CommandPublisher;
import ru.ioque.investfund.application.datasource.command.IntegrateTradingDataCommand;
import ru.ioque.investfund.application.telegrambot.command.PublishDailyReport;
import ru.ioque.investfund.application.telegrambot.command.PublishHourlyReport;
import ru.ioque.investfund.domain.datasource.entity.identity.DatasourceId;

@Component
@AllArgsConstructor
@Profile("production")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SpringScheduler {
    JpaDatasourceRepository jpaDatasourceRepository;
    CommandPublisher commandPublisher;

    @Scheduled(cron = "0 */15 10-20 * * MON-FRI")
    public void integrateTradingData() {
        jpaDatasourceRepository.findAll().forEach(datasource -> {
            commandPublisher.publish(
                new IntegrateTradingDataCommand(
                    DatasourceId.from(datasource.getId())
                )
            );
        });
    }

    @Scheduled(cron = "0 0 11-20 * * MON-FRI")
    public void publishHourlyReport() {
        commandPublisher.publish(new PublishHourlyReport());
    }

    @Scheduled(cron = "59 59 23 * * MON-FRI")
    public void publishDailyReport() {
        commandPublisher.publish(new PublishDailyReport());
    }
}

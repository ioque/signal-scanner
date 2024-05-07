package ru.ioque.investfund.adapters.scheduler;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Profile("production")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SpringScheduler {
//    @Scheduled(cron = "0 */15 10-20 * * MON-SUN")
//    public void integrateTradingData() {
//        jpaDatasourceRepository.findAll().forEach(datasource -> commandPublisher.publish(
//            new IntegrateTradingDataCommand(
//                DatasourceId.from(datasource.getId())
//            )
//        ));
//    }
//
//    @Scheduled(cron = "0 0 11-20 * * MON-SUN")
//    public void publishHourlyReport() {
//        commandPublisher.publish(new PublishHourlyReport());
//    }
//
//    @Scheduled(cron = "59 59 23 * * MON-SUN")
//    public void publishDailyReport() {
//        commandPublisher.publish(new PublishDailyReport());
//    }
}

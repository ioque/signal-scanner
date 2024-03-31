package ru.ioque.moexdatasource.adapters.scheduler;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.ioque.moexdatasource.application.InstrumentService;

@Slf4j
@Component
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Scheduler {
    InstrumentService instrumentService;
    @Scheduled(cron="0 0 8 * * MON-FRI")
    @EventListener(ApplicationReadyEvent.class)
    public void onSchedule() {
        log.info("download instruments was run.");
        instrumentService.downloadInstruments();
        log.info("download instruments was finished.");
    }
}

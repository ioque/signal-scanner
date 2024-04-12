package ru.ioque.investfund.adapters.event;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.EventPublisher;
import ru.ioque.investfund.application.modules.scanner.ScannerManager;
import ru.ioque.investfund.domain.core.DomainEvent;
import ru.ioque.investfund.domain.datasource.event.TradingDataUpdatedEvent;
import ru.ioque.investfund.domain.scanner.command.ScanningCommand;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class FakeEventPublisher implements EventPublisher {
    @Getter
    List<DomainEvent> events = new CopyOnWriteArrayList<>();
    ScannerManager scannerManager;

    @Override
    public void publish(DomainEvent event) {
        if (events.stream().filter(row -> row.getId().equals(event.getId())).findFirst().isEmpty()) {
            events.add(event);
            if (event instanceof TradingDataUpdatedEvent tradingDataUpdatedEvent) {
                scannerManager.scanning(
                    ScanningCommand.builder()
                        .datasourceId(tradingDataUpdatedEvent.getDatasourceId())
                        .watermark(tradingDataUpdatedEvent.getDateTime())
                        .build()
                );
            }
        }
    }
}

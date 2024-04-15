package ru.ioque.investfund.adapters.rest.service;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.ioque.investfund.adapters.persistence.repositories.JpaArchivedIntradayValueRepository;
import ru.ioque.investfund.adapters.persistence.repositories.JpaDatasourceRepository;
import ru.ioque.investfund.adapters.persistence.repositories.JpaHistoryValueRepository;
import ru.ioque.investfund.adapters.persistence.repositories.JpaInstrumentRepository;
import ru.ioque.investfund.adapters.persistence.repositories.JpaIntradayValueRepository;
import ru.ioque.investfund.adapters.persistence.repositories.JpaSignalScannerRepository;
import ru.ioque.investfund.adapters.rest.service.request.InitDateTimeRequest;
import ru.ioque.investfund.application.adapters.DateTimeProvider;

@Hidden
@RestController
@Profile("test")
@AllArgsConstructor
@Tag(name = "Служебный контролер", description = "Работает в окружении test, позволяет очищать стейт приложения.")
public class ServiceController {
    JpaDatasourceRepository exchangeEntityRepository;
    JpaInstrumentRepository instrumentEntityRepository;
    JpaHistoryValueRepository jpaHistoryValueRepository;
    JpaIntradayValueRepository jpaIntradayValueRepository;
    JpaSignalScannerRepository signalScannerEntityRepository;
    JpaArchivedIntradayValueRepository jpaArchivedIntradayValueRepository;
    DateTimeProvider dateTimeProvider;

    @PostMapping("/api/service/date-time")
    public void initDateTime(@RequestBody InitDateTimeRequest request) {
        dateTimeProvider.initToday(request.getDate().atTime(request.getTime()));
    }

    @DeleteMapping("/api/service/state")
    public void clearState() {
        instrumentEntityRepository.deleteAll();
        jpaHistoryValueRepository.deleteAll();
        jpaIntradayValueRepository.deleteAll();
        exchangeEntityRepository.deleteAll();
        signalScannerEntityRepository.deleteAll();
        jpaArchivedIntradayValueRepository.deleteAll();
        dateTimeProvider.initToday(null);
    }
}

package ru.ioque.investfund.adapters.rest.testingsystem;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.ioque.investfund.adapters.rest.exchange.response.DailyValueResponse;
import ru.ioque.investfund.adapters.rest.exchange.response.IntradayValueResponse;
import ru.ioque.investfund.adapters.storage.jpa.repositories.ArchivedDailyValueEntityRepository;
import ru.ioque.investfund.adapters.storage.jpa.repositories.ArchivedIntradayValueEntityRepository;

import java.util.List;

@RestController
@AllArgsConstructor
@Tag(name = "TestingSystemQueryController", description = "Контроллер запросов к модулю \"Система тестирования\"")
public class TestingSystemQueryController {
    ArchivedDailyValueEntityRepository archivedDailyValueEntityRepository;
    ArchivedIntradayValueEntityRepository archivedIntradayValueEntityRepository;

    @PostMapping("/api/v1/run-data-transfer")
    public void runDataTransfer() {
        archivedDailyValueEntityRepository.archivingDailyValues();
        archivedIntradayValueEntityRepository.archivingIntradayValues();
    }

    @GetMapping("/api/v1/daily-values")
    public List<DailyValueResponse> getDailyValues(
        @RequestParam(defaultValue = "0") Integer pageNumber,
        @RequestParam(defaultValue = "100") Integer pageSize
    ) {
        return archivedDailyValueEntityRepository
            .findAll(PageRequest.of(pageNumber, pageSize))
            .stream()
            .map(DailyValueResponse::fromEntity)
            .toList();
    }

    @GetMapping("/api/v1/intraday-values")
    public List<IntradayValueResponse> getIntradayValues(
        @RequestParam(defaultValue = "0") Integer pageNumber,
        @RequestParam(defaultValue = "100") Integer pageSize
    ) {
        return archivedIntradayValueEntityRepository
            .findAll(PageRequest.of(pageNumber, pageSize))
            .stream()
            .map(IntradayValueResponse::fromEntity)
            .toList();
    }
}

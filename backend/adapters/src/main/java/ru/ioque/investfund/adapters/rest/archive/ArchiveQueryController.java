package ru.ioque.investfund.adapters.rest.archive;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.ioque.investfund.adapters.rest.exchange.response.DailyValueResponse;
import ru.ioque.investfund.adapters.rest.exchange.response.IntradayValueResponse;
import ru.ioque.investfund.adapters.storage.jpa.repositories.ArchivedHistoryValueEntityRepository;
import ru.ioque.investfund.adapters.storage.jpa.repositories.ArchivedIntradayValueEntityRepository;

import java.util.List;

@RestController
@AllArgsConstructor
@Tag(name = "TestingSystemQueryController", description = "Контроллер запросов к модулю \"Система тестирования\"")
public class ArchiveQueryController {
    ArchivedHistoryValueEntityRepository archivedHistoryValueEntityRepository;
    ArchivedIntradayValueEntityRepository archivedIntradayValueEntityRepository;

    @GetMapping("/api/history-values")
    public List<DailyValueResponse> getDailyValues(
        @RequestParam(defaultValue = "0") Integer pageNumber,
        @RequestParam(defaultValue = "100") Integer pageSize
    ) {
        return archivedHistoryValueEntityRepository
            .findAll(PageRequest.of(pageNumber, pageSize))
            .stream()
            .map(DailyValueResponse::fromEntity)
            .toList();
    }

    @GetMapping("/api/intraday-values")
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

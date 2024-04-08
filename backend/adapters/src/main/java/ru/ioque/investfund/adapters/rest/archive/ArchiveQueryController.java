package ru.ioque.investfund.adapters.rest.archive;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.ioque.investfund.adapters.rest.datasource.response.IntradayValueResponse;
import ru.ioque.investfund.adapters.persistence.repositories.JpaArchivedIntradayValueRepository;

import java.util.List;

@RestController
@AllArgsConstructor
@Tag(name = "TestingSystemQueryController", description = "Контроллер запросов к модулю \"Система тестирования\"")
public class ArchiveQueryController {
    JpaArchivedIntradayValueRepository jpaArchivedIntradayValueRepository;

    @GetMapping("/api/intraday-values")
    public List<IntradayValueResponse> getIntradayValues(
        @RequestParam(defaultValue = "0") Integer pageNumber,
        @RequestParam(defaultValue = "100") Integer pageSize
    ) {
        return jpaArchivedIntradayValueRepository
            .findAll(PageRequest.of(pageNumber, pageSize))
            .stream()
            .map(IntradayValueResponse::fromEntity)
            .toList();
    }
}

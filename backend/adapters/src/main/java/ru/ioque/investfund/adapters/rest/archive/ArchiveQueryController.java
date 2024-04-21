package ru.ioque.investfund.adapters.rest.archive;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.ioque.investfund.adapters.persistence.repositories.JpaIntradayValueRepository;

import java.util.List;

@RestController
@AllArgsConstructor
@Tag(name = "ArchiveQueryController", description = "Контроллер запросов к модулю \"Архив внутридневных сделок\"")
public class ArchiveQueryController {
    JpaIntradayValueRepository jpaIntradayValueRepository;

    @GetMapping("/api/archive/intraday")
    public List<IntradayDtoResponse> getIntradayValues(
        @RequestParam(defaultValue = "0") Integer pageNumber,
        @RequestParam(defaultValue = "100") Integer pageSize
    ) {
        return jpaIntradayValueRepository
            .findAll(PageRequest.of(pageNumber, pageSize))
            .stream()
            .map(IntradayDtoResponse::from)
            .toList();
    }
}

package ru.ioque.investfund.adapters.rest.archive;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ioque.investfund.adapters.persistence.repositories.JpaArchivedIntradayValueRepository;
import ru.ioque.investfund.adapters.persistence.repositories.JpaIntradayValueRepository;

@RestController
@AllArgsConstructor
@Tag(name = "ArchiveCommandController", description = "Контроллер команд к модулю \"Архив\"")
public class ArchiveCommandController {
    JpaArchivedIntradayValueRepository jpaArchivedIntradayValueRepository;
    JpaIntradayValueRepository jpaIntradayValueRepository;

    @PostMapping("/api/archive")
    public void archiving() {
        jpaArchivedIntradayValueRepository.archivingIntradayValues();
        jpaIntradayValueRepository.deleteAll();
    }
}
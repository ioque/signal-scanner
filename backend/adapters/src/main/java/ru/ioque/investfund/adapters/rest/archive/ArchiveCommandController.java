package ru.ioque.investfund.adapters.rest.archive;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ioque.investfund.adapters.storage.jpa.repositories.ArchivedDailyValueEntityRepository;
import ru.ioque.investfund.adapters.storage.jpa.repositories.ArchivedIntradayValueEntityRepository;

@RestController
@AllArgsConstructor
@Tag(name = "ArchiveCommandController", description = "Контроллер команд к модулю \"Архив\"")
public class ArchiveCommandController {
    ArchivedDailyValueEntityRepository archivedDailyValueEntityRepository;
    ArchivedIntradayValueEntityRepository archivedIntradayValueEntityRepository;

    @PostMapping("/api/v1/archiving")
    public void archiving() {
        archivedDailyValueEntityRepository.archivingDailyValues();
        archivedIntradayValueEntityRepository.archivingIntradayValues();
    }
}

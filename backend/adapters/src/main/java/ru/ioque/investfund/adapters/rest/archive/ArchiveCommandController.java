package ru.ioque.investfund.adapters.rest.archive;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ioque.investfund.adapters.storage.jpa.repositories.ArchivedHistoryValueEntityRepository;
import ru.ioque.investfund.adapters.storage.jpa.repositories.ArchivedIntradayValueEntityRepository;

@RestController
@AllArgsConstructor
@Tag(name = "ArchiveCommandController", description = "Контроллер команд к модулю \"Архив\"")
public class ArchiveCommandController {
    ArchivedHistoryValueEntityRepository archivedHistoryValueEntityRepository;
    ArchivedIntradayValueEntityRepository archivedIntradayValueEntityRepository;

    @PostMapping("/api/archiving")
    public void archiving() {
        archivedHistoryValueEntityRepository.archivingHistoryValues();
        archivedIntradayValueEntityRepository.archivingIntradayValues();
    }
}

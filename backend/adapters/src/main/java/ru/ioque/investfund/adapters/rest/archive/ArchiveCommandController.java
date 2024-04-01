package ru.ioque.investfund.adapters.rest.archive;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ioque.investfund.adapters.storage.jpa.repositories.ArchivedIntradayValueEntityRepository;
import ru.ioque.investfund.adapters.storage.jpa.repositories.IntradayValueEntityRepository;

@RestController
@AllArgsConstructor
@Tag(name = "ArchiveCommandController", description = "Контроллер команд к модулю \"Архив\"")
public class ArchiveCommandController {
    ArchivedIntradayValueEntityRepository archivedIntradayValueEntityRepository;
    IntradayValueEntityRepository intradayValueEntityRepository;

    @PostMapping("/api/archiving")
    public void archiving() {
        archivedIntradayValueEntityRepository.archivingIntradayValues();
        intradayValueEntityRepository.deleteAll();
    }
}
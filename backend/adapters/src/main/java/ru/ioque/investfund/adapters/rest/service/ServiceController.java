package ru.ioque.investfund.adapters.rest.service;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.ioque.investfund.adapters.persistence.repositories.JpaDatasourceRepository;
import ru.ioque.investfund.adapters.persistence.repositories.JpaInstrumentRepository;
import ru.ioque.investfund.adapters.persistence.repositories.JpaIntradayValueRepository;
import ru.ioque.investfund.adapters.persistence.repositories.JpaSignalRepository;
import ru.ioque.investfund.adapters.persistence.repositories.JpaScannerRepository;
import ru.ioque.investfund.adapters.rest.service.request.InitDateTimeRequest;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.modules.pipeline.PipelineManager;

@Hidden
@RestController
@Profile("!production")
@AllArgsConstructor
@Tag(name = "Служебный контролер", description = "Работает в окружении test")
public class ServiceController {
    JpaDatasourceRepository jpaDatasourceRepository;
    JpaInstrumentRepository jpaInstrumentRepository;
    JpaIntradayValueRepository jpaIntradayValueRepository;
    JpaSignalRepository jpaSignalRepository;
    JpaScannerRepository jpaScannerRepository;
    DateTimeProvider dateTimeProvider;
    PipelineManager pipelineManager;

    @PostMapping("/api/service/pipeline/initialize")
    public void initializePipeline() {
        pipelineManager.initializeContexts();
        pipelineManager.runPipeline();
    }

    @PostMapping("/api/service/date-time")
    public void initDateTime(@RequestBody InitDateTimeRequest request) {
        dateTimeProvider.initToday(request.getDate().atTime(request.getTime()));
    }

    @DeleteMapping("/api/service/state")
    public void clearState() {
        jpaSignalRepository.deleteAll();
        jpaScannerRepository.deleteAll();
        jpaInstrumentRepository.deleteAll();
        jpaIntradayValueRepository.deleteAll();
        jpaDatasourceRepository.deleteAll();
        dateTimeProvider.initToday(null);
        pipelineManager.resetContexts();
    }
}

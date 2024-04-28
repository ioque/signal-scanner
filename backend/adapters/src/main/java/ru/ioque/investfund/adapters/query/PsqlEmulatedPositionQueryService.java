package ru.ioque.investfund.adapters.query;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.adapters.persistence.entity.risk.EmulatedPositionEntity;
import ru.ioque.investfund.adapters.persistence.repositories.JpaEmulatedPositionRepository;
import ru.ioque.investfund.adapters.persistence.repositories.JpaInstrumentRepository;
import ru.ioque.investfund.adapters.query.filter.EmulatedPositionFilterParams;

import java.util.List;

@Component
@AllArgsConstructor
public class PsqlEmulatedPositionQueryService {
    JpaEmulatedPositionRepository emulatedPositionRepository;
    JpaInstrumentRepository instrumentRepository;

    public List<EmulatedPositionEntity> findEmulatedPositions(EmulatedPositionFilterParams filterParams) {
        if (filterParams.specificationIsEmpty() && filterParams.pageRequestIsEmpty()) return getAllEmulatedPositions();
        if (filterParams.specificationIsEmpty()) return findEmulatedPositions(filterParams.pageRequest());
        if (filterParams.pageRequestIsEmpty()) return findEmulatedPositions(filterParams.specification());
        return emulatedPositionRepository.findAll(filterParams.specification(), filterParams.pageRequest()).toList();
    }

    public List<EmulatedPositionEntity> findEmulatedPositions(Specification<EmulatedPositionEntity> specification) {
        return emulatedPositionRepository.findAll(specification);
    }

    public List<EmulatedPositionEntity> findEmulatedPositions(PageRequest pageRequest) {
        return emulatedPositionRepository.findAll(pageRequest).toList();
    }

    public List<EmulatedPositionEntity> getAllEmulatedPositions() {
        return emulatedPositionRepository.findAll();
    }
}

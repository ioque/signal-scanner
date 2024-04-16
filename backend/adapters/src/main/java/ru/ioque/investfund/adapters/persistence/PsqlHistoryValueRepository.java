package ru.ioque.investfund.adapters.persistence;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.adapters.persistence.entity.datasource.historyvalue.HistoryValueEntity;
import ru.ioque.investfund.adapters.persistence.repositories.JpaHistoryValueRepository;
import ru.ioque.investfund.application.adapters.HistoryValueRepository;
import ru.ioque.investfund.domain.datasource.value.HistoryValue;

import java.util.List;

@Component
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PsqlHistoryValueRepository implements HistoryValueRepository {
    JpaHistoryValueRepository jpaHistoryValueRepository;

    @Override
    @Transactional
    public void saveAll(List<HistoryValue> intradayValues) {
        jpaHistoryValueRepository.saveAll(intradayValues.stream().map(HistoryValueEntity::fromDomain).toList());
    }
}

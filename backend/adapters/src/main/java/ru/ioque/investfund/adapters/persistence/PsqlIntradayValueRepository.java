package ru.ioque.investfund.adapters.persistence;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.adapters.persistence.entity.datasource.intradayvalue.IntradayValueEntity;
import ru.ioque.investfund.adapters.persistence.repositories.JpaIntradayValueRepository;
import ru.ioque.investfund.application.adapters.IntradayValueRepository;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;

import java.util.Collection;

@Component
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PsqlIntradayValueRepository implements IntradayValueRepository {
    JpaIntradayValueRepository jpaIntradayValueRepository;

    @Override
    @Transactional
    public void saveAll(Collection<IntradayData> intradayData) {
        jpaIntradayValueRepository.saveAll(intradayData.stream().map(IntradayValueEntity::fromDomain).toList());
    }

    @Override
    public void publish(IntradayData intradayData) {

    }
}

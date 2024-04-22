package ru.ioque.investfund.adapters.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ioque.investfund.adapters.persistence.entity.datasource.intradayvalue.IntradayPk;
import ru.ioque.investfund.adapters.persistence.entity.datasource.intradayvalue.IntradayValueEntity;

@Repository
public interface JpaIntradayValueRepository extends JpaRepository<IntradayValueEntity, IntradayPk> {
}

package ru.ioque.investfund.fakes;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.application.adapters.repository.DatasourceRepository;
import ru.ioque.investfund.domain.core.EntityNotFoundException;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.entity.identity.DatasourceId;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FakeDatasourceRepository implements DatasourceRepository {
    final Map<DatasourceId, Datasource> exchanges = new ConcurrentHashMap<>();

    public List<Datasource> getAll() {
        return exchanges.values().stream().toList();
    }

    @Override
    public Optional<Datasource> findBy(DatasourceId datasourceId) {
        return Optional.ofNullable(exchanges.get(datasourceId));
    }

    @Override
    public void save(Datasource datasource) {
        exchanges.put(datasource.getId(), datasource);
    }

    @Override
    public void remove(Datasource datasource) {
        exchanges.remove(datasource.getId());
    }

    @Override
    public Datasource getBy(DatasourceId datasourceId) throws EntityNotFoundException {
        return findBy(datasourceId).orElseThrow(
            () -> new EntityNotFoundException(
                String.format("Источник данных[id=%s] не существует.", datasourceId)
            )
        );
    }

    @Override
    public DatasourceId nextId() {
        return DatasourceId.from(UUID.randomUUID());
    }

    public Instrument getInstrumentBy(Ticker ticker) {
        return exchanges.values()
            .stream()
            .map(Datasource::getInstruments)
            .flatMap(Collection::stream).filter(row -> row.getTicker().equals(ticker))
            .findFirst()
            .orElseThrow();
    }

    public Instrument getInstrumentBy(InstrumentId instrumentId) {
        return exchanges.values()
            .stream()
            .map(Datasource::getInstruments)
            .flatMap(Collection::stream).filter(row -> row.getId().equals(instrumentId))
            .findFirst()
            .orElseThrow();
    }

    public Optional<Instrument> findInstrumentBy(InstrumentId instrumentId) {
        return exchanges.values()
            .stream()
            .map(Datasource::getInstruments)
            .flatMap(Collection::stream).filter(row -> row.getId().equals(instrumentId))
            .findFirst();
    }
}
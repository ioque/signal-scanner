package ru.ioque.investfund.domain.datasource.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.core.Domain;
import ru.ioque.investfund.domain.datasource.command.CreateDatasourceCommand;
import ru.ioque.investfund.domain.datasource.command.UpdateDatasourceCommand;
import ru.ioque.investfund.domain.datasource.entity.identity.DatasourceId;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Datasource extends Domain<DatasourceId> {
    String name;
    String url;
    String description;
    final List<Instrument> instruments;

    @Builder
    public Datasource(
        DatasourceId id,
        String name,
        String url,
        String description,
        List<Instrument> instruments
    ) {
        super(id);
        this.name = name;
        this.url = url;
        this.description = description;
        this.instruments = instruments;
    }

    public static Datasource of(DatasourceId id, CreateDatasourceCommand command) {
        return Datasource
            .builder()
            .id(id)
            .name(command.getName())
            .url(command.getUrl())
            .description(command.getDescription())
            .instruments(new ArrayList<>())
            .build();
    }

    public void update(UpdateDatasourceCommand command) {
        this.name = command.getName();
        this.url = command.getUrl();
        this.description = command.getDescription();
    }

    public void addInstrument(Instrument instrument) {
        if (findBy(instrument.getId()).isEmpty()) {
            instruments.add(instrument);
        }
    }

    public void checkExistsInstrument(List<InstrumentId> instrumentIds) {
        List<InstrumentId> notExistedInstrument = instrumentIds
            .stream()
            .filter(instrumentId -> !getInstrumentIds().contains(instrumentId))
            .toList();
        if (!notExistedInstrument.isEmpty()) {
            throw new IllegalArgumentException(
                String
                    .format(
                        "В выбранном источнике данных не существует инструментов с идентификаторам %s.",
                        notExistedInstrument
                    )
            );
        }
    }

    public List<Instrument> getUpdatableInstruments() {
        return instruments.stream().filter(Instrument::isUpdatable).toList();
    }

    public void enableUpdate(List<InstrumentId> instrumentIds) {
        instrumentIds.forEach(id -> findBy(id).ifPresent(Instrument::enableUpdate));
    }

    private Optional<Instrument> findBy(InstrumentId instrumentId) {
        return instruments.stream().filter(row -> row.getId().equals(instrumentId)).findFirst();
    }

    public void disableUpdate(List<InstrumentId> instrumentIds) {
        instrumentIds.forEach(id -> findBy(id).ifPresent(Instrument::disableUpdate));
    }

    public List<InstrumentId> getInstrumentIds() {
        return getInstruments().stream().map(Instrument::getId).toList();
    }

    public List<Instrument> getInstruments() {
        return Collections.unmodifiableList(instruments);
    }
}

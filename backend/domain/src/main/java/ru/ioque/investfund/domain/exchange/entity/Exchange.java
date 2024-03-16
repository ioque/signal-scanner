package ru.ioque.investfund.domain.exchange.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.core.Domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

//урлы для обновления данных, клиенты к биржам выносятся как отдельные сервисы-адаптеры для получения данных.
//добавил клиент (хоть на питоне, похер, главное чтоб контракт выполнялся), указал название биржи, описание и погнали.
//дальше система сама начинает работать с биржей, настраиваются сканеры
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Exchange extends Domain {
    String name;
    String url;
    String description;
    Map<UUID, Instrument> instruments;

    @Builder
    public Exchange(
        UUID id,
        String name,
        String url,
        String description,
        List<Instrument> instruments
    ) {
        super(id);
        this.name = name;
        this.url = url;
        this.description = description;
        this.instruments = new HashMap<>();
        if (instruments != null) {
            instruments.forEach(instrument -> this.instruments.put(instrument.getId(), instrument));
        }
    }

    public List<Instrument> getInstruments() {
        return instruments
            .values()
            .stream()
            .toList();
    }

    public void saveInstrument(Instrument instrument) {
        if (!instrumentExists(instrument)) {
            instruments.put(instrument.getId(), instrument);
        }
    }

    public List<Instrument> getUpdatableInstruments() {
        return instruments.values().stream().filter(Instrument::isUpdatable).toList();
    }

    public void enableUpdate(List<UUID> ids) {
        ids.forEach(id -> {
            if (instruments.get(id) != null) {
                instruments.get(id).enableUpdate();
            }
        });
    }

    public void disableUpdate(List<UUID> ids) {
        ids.forEach(id -> {
            if (instruments.get(id) != null) {
                instruments.get(id).disableUpdate();
            }
        });
    }

    private boolean instrumentExists(Instrument instrument) {
        return instruments.values().stream().anyMatch(row -> row.getTicker().equals(instrument.getTicker()));
    }
}

package ru.ioque.investfund.domain.exchange.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.Domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Exchange extends Domain {
    String name;
    String url;
    String description;
    Map<UUID, Instrument> instruments;
    Map<String, UUID> tickerToIds;

    @Builder
    public Exchange(
        UUID id,
        String name,
        String url,
        String description,
        Set<UUID> updatable,
        List<Instrument> instruments
    ) {
        super(id);
        this.name = name;
        this.url = url;
        this.description = description;
        this.instruments = new HashMap<>();
        this.tickerToIds = new HashMap<>();
        if (instruments != null) {
            instruments.forEach(instrument -> {
                this.instruments.put(instrument.getId(), instrument);
                this.tickerToIds.put(instrument.getTicker(), instrument.getId());
            });
        }
    }

    public List<Instrument> getInstruments() {
        return instruments
            .values()
            .stream()
            .toList();
    }

    public void saveInstrument(Instrument instrument) {
        if (!tickerToIds.containsKey(instrument.getTicker())) {
            instruments.put(instrument.getId(), instrument);
            tickerToIds.put(instrument.getTicker(), instrument.getId());
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
}

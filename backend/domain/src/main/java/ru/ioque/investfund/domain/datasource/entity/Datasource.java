package ru.ioque.investfund.domain.datasource.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.core.Domain;
import ru.ioque.investfund.domain.datasource.command.AddDatasourceCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Datasource extends Domain {
    String name;
    String url;
    String description;
    @Getter
    List<Instrument> instruments;

    @Builder
    public Datasource(
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
        this.instruments = instruments != null ? new ArrayList<>(instruments) : new ArrayList<>();
    }

    public static Datasource from(UUID id, AddDatasourceCommand command) {
        return Datasource
            .builder()
            .id(id)
            .name(command.getName())
            .url(command.getUrl())
            .description(command.getDescription())
            .instruments(new ArrayList<>())
            .build();
    }

    public void addInstrument(Instrument instrument) {
        if (findBy(instrument.getTicker()).isEmpty()) {
            instruments.add(instrument);
        }
    }

    public List<Instrument> getUpdatableInstruments() {
        return instruments.stream().filter(Instrument::isUpdatable).toList();
    }

    public void enableUpdate(List<String> tickers) {
        tickers.forEach(ticker -> findBy(ticker).ifPresent(Instrument::enableUpdate));
    }

    private Optional<Instrument> findBy(String ticker) {
        return instruments.stream().filter(row -> row.getTicker().equals(ticker)).findFirst();
    }

    public void disableUpdate(List<String> tickers) {
        tickers.forEach(ticker -> findBy(ticker).ifPresent(Instrument::disableUpdate));
    }

    public List<String> getTickers() {
        return getInstruments().stream().map(Instrument::getTicker).toList();
    }
}

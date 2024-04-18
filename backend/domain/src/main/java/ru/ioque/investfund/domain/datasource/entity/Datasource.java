package ru.ioque.investfund.domain.datasource.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.datasource.command.CreateDatasourceCommand;
import ru.ioque.investfund.domain.datasource.command.UpdateDatasourceCommand;
import ru.ioque.investfund.domain.datasource.entity.indetity.DatasourceId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Datasource {
    DatasourceId id;
    String name;
    String url;
    String description;
    final List<Instrument> instruments;

    public static Datasource of(DatasourceId id, CreateDatasourceCommand command) {
        return Datasource
            .builder()
            .id(id)
            .url(command.getUrl())
            .name(command.getName())
            .instruments(new ArrayList<>())
            .description(command.getDescription())
            .build();
    }

    public void update(UpdateDatasourceCommand command) {
        this.name = command.getName();
        this.url = command.getUrl();
        this.description = command.getDescription();
    }

    public void addInstrument(Instrument instrument) {
        if (findBy(instrument.getTicker()).isEmpty()) {
            instruments.add(instrument);
        }
    }

    public void checkExistsTickers(List<String> tickers) {
        List<String> notExistedTickers = tickers
            .stream()
            .filter(ticker -> !getTickers().contains(ticker))
            .toList();
        if (!notExistedTickers.isEmpty()) {
            throw new IllegalArgumentException(
                String
                    .format(
                        "В выбранном источнике данных не существует инструментов с тикерами %s.",
                        notExistedTickers
                    )
            );
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

    public List<Instrument> getInstruments() {
        return Collections.unmodifiableList(instruments);
    }
}

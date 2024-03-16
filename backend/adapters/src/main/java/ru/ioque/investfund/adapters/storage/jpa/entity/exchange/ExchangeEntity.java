package ru.ioque.investfund.adapters.storage.jpa.entity.exchange;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.adapters.storage.jpa.entity.AbstractEntity;
import ru.ioque.investfund.adapters.storage.jpa.entity.exchange.instrument.InstrumentEntity;
import ru.ioque.investfund.domain.exchange.entity.Exchange;
import ru.ioque.investfund.domain.exchange.entity.Instrument;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@Table(name = "exchange")
@Entity(name = "Exchange")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExchangeEntity extends AbstractEntity {
    String name;
    String url;
    String description;
    @ElementCollection(fetch = FetchType.EAGER)
    Set<UUID> updatable;

    @Builder
    public ExchangeEntity(
        UUID id,
        String name,
        String url,
        String description,
        Set<UUID> updatable,
        List<InstrumentEntity> instruments
    ) {
        super(id);
        this.name = name;
        this.url = url;
        this.description = description;
        this.updatable = updatable;
    }

    public Exchange toDomain() {
        return Exchange.builder()
            .id(getId())
            .name(name)
            .url(url)
            .description(description)
            .build();
    }

    public Exchange toDomain(List<Instrument> instruments) {
        return Exchange.builder()
            .id(getId())
            .name(name)
            .url(url)
            .description(description)
            .instruments(instruments)
            .build();
    }

    public static ExchangeEntity fromDomain(Exchange exchange) {
        return ExchangeEntity.builder()
            .id(exchange.getId())
            .name(exchange.getName())
            .url(exchange.getUrl())
            .description(exchange.getDescription())
            .updatable(exchange.getUpdatableInstruments().stream().map(Instrument::getId).collect(Collectors.toSet()))
            .build();
    }
}

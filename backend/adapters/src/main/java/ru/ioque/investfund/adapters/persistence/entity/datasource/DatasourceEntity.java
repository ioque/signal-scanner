package ru.ioque.investfund.adapters.persistence.entity.datasource;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.adapters.persistence.entity.AbstractEntity;
import ru.ioque.investfund.adapters.persistence.entity.datasource.instrument.InstrumentEntity;
import ru.ioque.investfund.domain.datasource.entity.Datasource;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@Table(name = "datasource")
@Entity(name = "Datasource")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DatasourceEntity extends AbstractEntity {
    String name;
    String url;
    String description;
    @ToString.Exclude
    @JoinColumn(name = "datasource_id")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    Set<InstrumentEntity> instruments;

    @Builder
    public DatasourceEntity(
        UUID id,
        String name,
        String url,
        String description,
        Set<InstrumentEntity> instruments
    ) {
        super(id);
        this.name = name;
        this.url = url;
        this.description = description;
        this.instruments = instruments;
    }

    public Datasource toDomain() {
        return Datasource.builder()
            .id(getId())
            .name(name)
            .url(url)
            .description(description)
            .instruments(instruments
                .stream()
                .map(InstrumentEntity::toDomain)
                .toList()
            )
            .build();
    }

    public static DatasourceEntity fromDomain(Datasource datasource) {
        return DatasourceEntity.builder()
            .id(datasource.getId())
            .name(datasource.getName())
            .url(datasource.getUrl())
            .description(datasource.getDescription())
            .instruments(datasource
                .getInstruments()
                .stream()
                .map(InstrumentEntity::from)
                .collect(Collectors.toSet())
            )
            .build();
    }
}

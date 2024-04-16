package ru.ioque.investfund.adapters.persistence.entity.datasource;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
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

import java.util.ArrayList;
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
    UUID datasourceId;
    String name;
    String url;
    String description;
    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "datasource")
    Set<InstrumentEntity> instruments;

    @Builder
    public DatasourceEntity(
        UUID id,
        UUID datasourceId,
        String name,
        String url,
        String description,
        Set<InstrumentEntity> instruments
    ) {
        super(id);
        this.datasourceId = datasourceId;
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
            .instruments(
                instruments
                    .stream()
                    .map(InstrumentEntity::toDomain)
                    .collect(Collectors.toCollection(ArrayList::new))
            )
            .build();
    }

    public static DatasourceEntity from(Datasource datasource) {
        final DatasourceEntity datasourceEntity = DatasourceEntity.builder()
            .id(datasource.getId())
            .name(datasource.getName())
            .url(datasource.getUrl())
            .description(datasource.getDescription())
            .build();
        final Set<InstrumentEntity> instruments = datasource
            .getInstruments()
            .stream()
            .map(InstrumentEntity::from)
            .peek(row -> row.setDatasource(datasourceEntity))
            .collect(Collectors.toSet());
        datasourceEntity.setInstruments(instruments);
        return datasourceEntity;
    }
}

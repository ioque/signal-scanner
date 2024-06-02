package ru.ioque.investfund.adapters.psql.entity.datasource.instrument;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.adapters.psql.entity.UuidIdentity;
import ru.ioque.investfund.adapters.psql.entity.datasource.DatasourceEntity;
import ru.ioque.investfund.adapters.psql.entity.datasource.instrument.details.InstrumentDetailsEntity;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.datasource.value.types.InstrumentType;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@Table(name = "instrument")
@Entity(name = "Instrument")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InstrumentEntity extends UuidIdentity {
    @ManyToOne
    @JoinColumn(name = "datasource_id")
    DatasourceEntity datasource;

    @OneToOne(mappedBy = "instrument", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    InstrumentDetailsEntity details;

    Boolean updatable;

    @Enumerated(EnumType.STRING)
    InstrumentType type;

    @Builder
    public InstrumentEntity(
        UUID id,
        Boolean updatable,
        InstrumentType type,
        DatasourceEntity datasource,
        InstrumentDetailsEntity details
    ) {
        super(id);
        this.type = type;
        this.updatable = updatable;
        this.datasource = datasource;
        this.details = details;
    }

    public String getTicker() {
        return details.getTicker();
    }

    public Instrument toDomain() {
        return Instrument.builder()
            .id(InstrumentId.from(getId()))
            .updatable(getUpdatable())
            .detail(getDetails().toDomain())
            .build();
    }

    public static InstrumentEntity fromDomain(Instrument domain) {
        InstrumentEntity instrumentEntity = InstrumentEntity.builder()
            .id(domain.getId().getUuid())
            .updatable(domain.getUpdatable())
            .type(domain.getType())
            .build();
        instrumentEntity
            .setDetails(
                InstrumentDetailsEntity.of(instrumentEntity, domain.getDetail())
            );
        return instrumentEntity;
    }
}

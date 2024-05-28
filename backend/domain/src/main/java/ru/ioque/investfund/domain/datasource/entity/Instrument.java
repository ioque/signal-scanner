package ru.ioque.investfund.domain.datasource.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.core.Domain;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.datasource.value.details.InstrumentDetail;
import ru.ioque.investfund.domain.datasource.value.types.InstrumentType;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Instrument extends Domain<InstrumentId> {
    InstrumentDetail detail;
    Boolean updatable;

    @Builder
    public Instrument(
        InstrumentId id,
        Boolean updatable,
        InstrumentDetail detail
    ) {
        super(id);
        this.updatable = updatable;
        this.detail = detail;
    }

    public Ticker getTicker() {
        return detail.getTicker();
    }

    public InstrumentType getType() {
        return detail.getType();
    }

    public void updateDetails(InstrumentDetail details) {
        this.detail = details;
    }

    public void enableUpdate() {
        updatable = true;
    }

    public void disableUpdate() {
        updatable = false;
    }

    public boolean isUpdatable() {
        return Boolean.TRUE.equals(updatable);
    }
}

package ru.ioque.investfund.adapters.persistence.entity.datasource.instrument.details;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.ioque.investfund.adapters.persistence.entity.datasource.instrument.InstrumentEntity;
import ru.ioque.investfund.domain.datasource.value.details.CurrencyPairDetails;
import ru.ioque.investfund.domain.datasource.value.details.FuturesDetails;
import ru.ioque.investfund.domain.datasource.value.details.IndexDetails;
import ru.ioque.investfund.domain.datasource.value.details.InstrumentDetails;
import ru.ioque.investfund.domain.datasource.value.details.StockDetails;
import ru.ioque.investfund.domain.datasource.value.types.InstrumentType;

import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@Table(name = "instrument_details")
@Entity(name = "InstrumentDetails")
public abstract class InstrumentDetailsEntity {
    @Id
    @Column(name = "instrument_id")
    private UUID id;

    @MapsId
    @OneToOne
    @JoinColumn(name = "instrument_id")
    InstrumentEntity instrument;

    @Column(nullable = false)
    String shortName;

    @Column(nullable = false)
    String name;

    public InstrumentDetailsEntity(InstrumentEntity instrument, String shortName, String name) {
        this.id = instrument.getId();
        this.instrument = instrument;
        this.shortName = shortName;
        this.name = name;
    }

    public abstract InstrumentDetails toDomain();

    public static InstrumentDetailsEntity of(InstrumentEntity instrument, InstrumentDetails details) {
        return instrumentMappers.get(instrument.getType()).apply(instrument, details);
    }

    private static Map<InstrumentType, BiFunction<InstrumentEntity, InstrumentDetails, InstrumentDetailsEntity>> instrumentMappers = Map.of(
        InstrumentType.FUTURES, InstrumentDetailsEntity::toFuturesDetails,
        InstrumentType.STOCK, InstrumentDetailsEntity::toStockDetails,
        InstrumentType.CURRENCY_PAIR, InstrumentDetailsEntity::toCurrencyPairDetails,
        InstrumentType.INDEX, InstrumentDetailsEntity::toIndexDetails
    );

    private static InstrumentDetailsEntity toIndexDetails(InstrumentEntity instrument, InstrumentDetails instrumentDetails) {
        IndexDetails details = (IndexDetails) instrumentDetails;
        return IndexDetailsEntity.builder()
            .instrument(instrument)
            .name(details.getName())
            .annualLow(details.getAnnualLow())
            .shortName(details.getShortName())
            .annualHigh(details.getAnnualHigh())
            .build();
    }

    private static InstrumentDetailsEntity toCurrencyPairDetails(InstrumentEntity instrument, InstrumentDetails instrumentDetails) {
        CurrencyPairDetails details = (CurrencyPairDetails) instrumentDetails;
        return CurrencyPairDetailsEntity.builder()
            .instrument(instrument)
            .name(details.getName())
            .lotSize(details.getLotSize())
            .faceUnit(details.getFaceUnit())
            .shortName(details.getShortName())
            .build();
    }

    private static InstrumentDetailsEntity toStockDetails(InstrumentEntity instrument, InstrumentDetails instrumentDetails) {
        StockDetails details = (StockDetails) instrumentDetails;
        return StockDetailsEntity.builder()
            .instrument(instrument)
            .name(details.getName())
            .lotSize(details.getLotSize())
            .listLevel(details.getListLevel())
            .regNumber(details.getRegNumber())
            .shortName(details.getShortName())
            .isin(details.getIsin().getValue())
            .build();
    }

    private static InstrumentDetailsEntity toFuturesDetails(InstrumentEntity instrument, InstrumentDetails instrumentDetails) {
        FuturesDetails details = (FuturesDetails) instrumentDetails;
        return FuturesDetailsEntity.builder()
            .instrument(instrument)
            .name(details.getName())
            .lowLimit(details.getLowLimit())
            .assetCode(details.getAssetCode())
            .lotVolume(details.getLotVolume())
            .highLimit(details.getHighLimit())
            .shortName(details.getShortName())
            .initialMargin(details.getInitialMargin())
            .build();
    }
}

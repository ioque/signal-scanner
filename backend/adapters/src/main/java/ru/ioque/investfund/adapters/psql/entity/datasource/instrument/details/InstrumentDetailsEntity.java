package ru.ioque.investfund.adapters.psql.entity.datasource.instrument.details;

import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.adapters.psql.entity.datasource.instrument.InstrumentEntity;
import ru.ioque.investfund.domain.datasource.value.details.CurrencyPairDetail;
import ru.ioque.investfund.domain.datasource.value.details.FuturesDetail;
import ru.ioque.investfund.domain.datasource.value.details.IndexDetail;
import ru.ioque.investfund.domain.datasource.value.details.InstrumentDetail;
import ru.ioque.investfund.domain.datasource.value.details.StockDetail;
import ru.ioque.investfund.domain.datasource.value.types.InstrumentType;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@Table(name = "instrument_details")
@Entity(name = "InstrumentDetails")
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class InstrumentDetailsEntity {

    @Id
    @Column(name = "instrument_id")
    UUID id;

    @MapsId
    @OneToOne
    @JoinColumn(name = "instrument_id")
    InstrumentEntity instrument;

    @Column(nullable = false)
    String ticker;

    @Column(nullable = false)
    String shortName;

    @Column(nullable = false)
    String name;

    public InstrumentDetailsEntity(InstrumentEntity instrument, String ticker, String shortName, String name) {
        this.id = instrument.getId();
        this.instrument = instrument;
        this.ticker = ticker;
        this.shortName = shortName;
        this.name = name;
    }

    public abstract InstrumentDetail toDomain();

    public static InstrumentDetailsEntity of(InstrumentEntity instrument, InstrumentDetail details) {
        return instrumentMappers.get(instrument.getType()).apply(instrument, details);
    }

    private static Map<InstrumentType, BiFunction<InstrumentEntity, InstrumentDetail, InstrumentDetailsEntity>> instrumentMappers = Map.of(
        InstrumentType.FUTURES, InstrumentDetailsEntity::toFuturesDetails,
        InstrumentType.STOCK, InstrumentDetailsEntity::toStockDetails,
        InstrumentType.CURRENCY_PAIR, InstrumentDetailsEntity::toCurrencyPairDetails,
        InstrumentType.INDEX, InstrumentDetailsEntity::toIndexDetails
    );

    private static InstrumentDetailsEntity toIndexDetails(
        InstrumentEntity instrument,
        InstrumentDetail instrumentDetail) {
        final IndexDetail details = (IndexDetail) instrumentDetail;
        return IndexDetailsEntity.builder()
            .instrument(instrument)
            .ticker(details.getTicker().getValue())
            .name(details.getName())
            .annualLow(details.getAnnualLow())
            .shortName(details.getShortName())
            .annualHigh(details.getAnnualHigh())
            .build();
    }

    private static InstrumentDetailsEntity toCurrencyPairDetails(
        InstrumentEntity instrument,
        InstrumentDetail instrumentDetail) {
        final CurrencyPairDetail details = (CurrencyPairDetail) instrumentDetail;
        return CurrencyPairDetailsEntity.builder()
            .instrument(instrument)
            .ticker(details.getTicker().getValue())
            .name(details.getName())
            .lotSize(details.getLotSize())
            .faceUnit(details.getFaceUnit())
            .shortName(details.getShortName())
            .build();
    }

    private static InstrumentDetailsEntity toStockDetails(
        InstrumentEntity instrument,
        InstrumentDetail instrumentDetail) {
        final StockDetail details = (StockDetail) instrumentDetail;
        return StockDetailsEntity.builder()
            .instrument(instrument)
            .ticker(details.getTicker().getValue())
            .name(details.getName())
            .lotSize(details.getLotSize())
            .listLevel(details.getListLevel())
            .regNumber(details.getRegNumber())
            .shortName(details.getShortName())
            .isin(details.getIsin().getValue())
            .build();
    }

    private static InstrumentDetailsEntity toFuturesDetails(
        InstrumentEntity instrument,
        InstrumentDetail instrumentDetail) {
        final FuturesDetail details = (FuturesDetail) instrumentDetail;
        return FuturesDetailsEntity.builder()
            .instrument(instrument)
            .ticker(details.getTicker().getValue())
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

package ru.ioque.investfund.adapters.persistence.entity.datasource.instrument.tradingstate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.adapters.persistence.entity.datasource.instrument.InstrumentEntity;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@ToString
@NoArgsConstructor
@Table(name = "trading_state")
@Entity(name = "TradingState")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TradingStateEntity {
    @Id
    @Column(name = "instrument_id")
    private UUID id;

    @MapsId
    @OneToOne
    @JoinColumn(name = "instrument_id")
    InstrumentEntity instrument;

    LocalDateTime dateTime;

    Double todayValue;

    Double todayLastPrice;

    Double todayFirstPrice;

    Long lastIntradayNumber;

    @Builder
    public TradingStateEntity(
        InstrumentEntity instrument,
        LocalDateTime dateTime,
        Double todayValue,
        Double todayLastPrice,
        Double todayFirstPrice,
        Long lastIntradayNumber
    ) {
        this.id = instrument.getId();
        this.instrument = instrument;
        this.dateTime = dateTime;
        this.todayValue = todayValue;
        this.todayLastPrice = todayLastPrice;
        this.todayFirstPrice = todayFirstPrice;
        this.lastIntradayNumber = lastIntradayNumber;
    }

    public static TradingStateEntity of(InstrumentEntity instrument, TradingState tradingState) {
        return TradingStateEntity.builder()
            .instrument(instrument)
            .dateTime(tradingState.getDate().atTime(tradingState.getTime()))
            .todayValue(tradingState.getTodayValue())
            .todayLastPrice(tradingState.getTodayLastPrice())
            .todayFirstPrice(tradingState.getTodayFirstPrice())
            .lastIntradayNumber(tradingState.getLastIntradayNumber())
            .build();
    }

    public TradingState toTradingState() {
        return new TradingState(
            getDateTime().toLocalDate(),
            getDateTime().toLocalTime(),
            getTodayValue(),
            getTodayLastPrice(),
            getTodayFirstPrice(),
            getLastIntradayNumber()
        );
    }
}

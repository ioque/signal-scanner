package ru.ioque.investfund.domain.datasource.value;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.core.DomainException;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Builder
@ToString
@EqualsAndHashCode
@Getter(AccessLevel.PUBLIC)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HistoryValue implements Comparable<HistoryValue>, Serializable {
    UUID datasourceId;
    LocalDate tradeDate;
    String ticker;
    Double openPrice;
    Double closePrice;
    Double lowPrice;
    Double highPrice;
    Double waPrice;
    Double value;

    public HistoryValue(
        UUID datasourceId,
        LocalDate tradeDate,
        String ticker,
        Double openPrice,
        Double closePrice,
        Double lowPrice,
        Double highPrice,
        Double waPrice,
        Double value
    ) {
        setDatasourceId(datasourceId);
        setTradeDate(tradeDate);
        setTicker(ticker);
        setOpenPrice(openPrice);
        setClosePrice(closePrice);
        setLowPrice(lowPrice);
        setHighPrice(highPrice);
        setWaPrice(waPrice);
        setValue(value);
    }

    @Override
    public int compareTo(HistoryValue historyValue) {
        return Objects.compare(getTradeDate(), historyValue.getTradeDate(), LocalDate::compareTo);
    }

    private void setDatasourceId(UUID datasourceId) {
        if(datasourceId == null) {
            throw new DomainException("Не заполнен идентификатор источника данных.");
        }
        this.datasourceId = datasourceId;
    }

    private void setTradeDate(LocalDate tradeDate) {
        if(tradeDate == null) {
            throw new DomainException("Не заполнена дата.");
        }
        this.tradeDate = tradeDate;
    }

    private void setTicker(String ticker) {
        if(ticker == null || ticker.isEmpty()) {
            throw new DomainException("Не заполнен тикер.");
        }
        this.ticker = ticker;
    }

    private void setOpenPrice(Double openPrice) {
        if(openPrice == null) {
            throw new DomainException("Не заполнена цена открытия.");
        }
        if(openPrice <= 0) {
            throw new DomainException("Цена открытия должна быть больше 0.");
        }
        this.openPrice = openPrice;
    }

    private void setClosePrice(Double closePrice) {
        if(closePrice == null) {
            throw new DomainException("Не заполнена цена закрытия.");
        }
        if(closePrice <= 0) {
            throw new DomainException("Цена закрытия должна быть больше 0.");
        }
        this.closePrice = closePrice;
    }

    private void setLowPrice(Double lowPrice) {
        if(lowPrice == null) {
            throw new DomainException("Не заполнена минимальная цена.");
        }
        if(lowPrice <= 0) {
            throw new DomainException("Минимальная цена должна быть больше 0.");
        }
        this.lowPrice = lowPrice;
    }

    private void setHighPrice(Double highPrice) {
        if(highPrice == null) {
            throw new DomainException("Не заполнена максимальная цена.");
        }
        if(highPrice <= 0) {
            throw new DomainException("Максимальная цена должна быть больше 0.");
        }
        this.highPrice = highPrice;
    }

    private void setWaPrice(Double waPrice) {
        if(waPrice != null && waPrice <= 0) {
            throw new DomainException("Средневзвешенная цена должна быть больше 0.");
        }
        this.waPrice = waPrice;
    }

    private void setValue(Double value) {
        this.value = value;
    }
}

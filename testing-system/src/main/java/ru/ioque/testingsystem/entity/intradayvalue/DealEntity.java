package ru.ioque.testingsystem.entity.intradayvalue;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@DiscriminatorValue("DealEntity")
public class DealEntity extends IntradayValueEntity {
    Boolean isBuy;
    Integer qnt;
    Double value;

    @Builder
    public DealEntity(
        Long number,
        LocalDateTime dateTime,
        String ticker,
        Double price,
        Boolean isBuy,
        Integer qnt,
        Double value
    ) {
        super(number, dateTime, ticker, price);
        this.isBuy = isBuy;
        this.qnt = qnt;
        this.value = value;
    }
}

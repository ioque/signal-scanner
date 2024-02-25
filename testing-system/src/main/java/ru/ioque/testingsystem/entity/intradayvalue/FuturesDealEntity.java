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
@DiscriminatorValue("FuturesDealEntity")
public class FuturesDealEntity extends IntradayValueEntity {
    Integer qnt;

    @Builder
    public FuturesDealEntity(
        Long number,
        LocalDateTime dateTime,
        String ticker,
        Double price,
        Integer qnt
    ) {
        super(number, dateTime, ticker, price);
        this.qnt = qnt;
    }
}

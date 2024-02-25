package ru.ioque.testingsystem.entity.intradayvalue;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@Table(name = "intraday_value")
@Entity(name = "IntradayValue")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="INTRADAY_VALUE_TYPE", discriminatorType= DiscriminatorType.STRING, columnDefinition = "varchar(255)")
public abstract class IntradayValueEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(nullable = false)
    LocalDateTime dateTime;
    @Column(nullable = false)
    Long number;
    @Column(nullable = false)
    String ticker;
    @Column(nullable = false)
    Double price;

    public IntradayValueEntity(
        Long number,
        LocalDateTime dateTime,
        String ticker,
        Double price
    ) {
        this.dateTime = dateTime;
        this.ticker = ticker;
        this.price = price;
        this.number = number;
    }
}


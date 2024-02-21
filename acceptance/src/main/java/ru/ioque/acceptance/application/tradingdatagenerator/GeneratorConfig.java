package ru.ioque.acceptance.application.tradingdatagenerator;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public abstract class GeneratorConfig {
    String ticker;
    LocalDate startDate;
    int days;
}

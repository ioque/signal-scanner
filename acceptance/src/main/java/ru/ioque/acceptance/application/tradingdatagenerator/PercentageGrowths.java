package ru.ioque.acceptance.application.tradingdatagenerator;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class PercentageGrowths {
    Double value;
    Double weight;
}

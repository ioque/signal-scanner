package ru.ioque.acceptance.application.tradingdatagenerator.core;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public abstract class GeneratorConfig {
    String ticker;
}

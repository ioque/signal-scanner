package ru.ioque.core.tradingdatagenerator.core;

import java.time.DayOfWeek;
import java.time.LocalDate;

public abstract class AbstractGenerator {
    // Генерировать целиком массив double, тогда можно будет постепенно перейти к использованию статистик.
    // Задавать растущий тренд, а значения генерировать более менее случайно в определенном разбросе
    // Определять покупка или продажа как? растущий тренд - больше покупок, чем продаж.
    // Падающий тренд - больше продаж, чем покупок. Как определять тренд? Строго параметром задавать на кусок данных?
    protected double linearGrowthFinalResult(Double deltaPercentage, Double startOpen) {
        return startOpen + deltaPercentage * startOpen / 100;
    }

    protected boolean isWeekend(LocalDate date) {
        return date.getDayOfWeek().equals(DayOfWeek.SUNDAY) || date.getDayOfWeek().equals(DayOfWeek.SATURDAY);
    }

    protected double getDeltaByMean(Double startValue, Double finalValue, long numbers) {
        return (finalValue - startValue) / (numbers - 1);
    }
}

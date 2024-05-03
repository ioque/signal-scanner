package ru.ioque.investfund.adapters.rest;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.function.Function;

@Getter
@AllArgsConstructor
public class Pagination<ELEMENT_TYPE> {
    int page;
    int totalPages;
    long totalElements;
    List<ELEMENT_TYPE> elements;

    public <NEW_ELEMENT_TYPE> Pagination<NEW_ELEMENT_TYPE> to(Function<ELEMENT_TYPE, NEW_ELEMENT_TYPE> mapper) {
        return new Pagination<>(
            page,
            totalPages,
            totalElements,
            elements.stream().map(mapper).toList()
        );
    }
}

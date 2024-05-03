package ru.ioque.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Pagination<ELEMENT_TYPE> {
    int page;
    int totalPages;
    long totalElements;
    List<ELEMENT_TYPE> elements;
}

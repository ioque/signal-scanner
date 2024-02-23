package ru.ioque.acceptance.domain.exchange;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Exchange {
    String name;
    String url;
    String description;
}

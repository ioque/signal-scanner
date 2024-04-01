package ru.ioque.investfund.application.modules.exchange;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class AddDatasourceCommand {
    String name;
    String url;
    String description;
}

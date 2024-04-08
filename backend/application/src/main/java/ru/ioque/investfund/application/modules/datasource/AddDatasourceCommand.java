package ru.ioque.investfund.application.modules.datasource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import ru.ioque.investfund.domain.datasource.entity.Datasource;

import java.util.ArrayList;
import java.util.UUID;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class AddDatasourceCommand {
    String name;
    String url;
    String description;

    public Datasource factory(UUID id) {
        return Datasource
            .builder()
            .id(id)
            .name(name)
            .url(url)
            .description(description)
            .instruments(new ArrayList<>())
            .build();
    }
}

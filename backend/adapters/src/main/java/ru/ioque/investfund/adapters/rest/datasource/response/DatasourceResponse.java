package ru.ioque.investfund.adapters.rest.datasource.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.adapters.psql.entity.datasource.DatasourceEntity;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DatasourceResponse implements Serializable {
    UUID id;
    String name;
    String url;
    String description;

    public static DatasourceResponse from(DatasourceEntity exchange) {
        return DatasourceResponse.builder()
            .id(exchange.getId())
            .name(exchange.getName())
            .url(exchange.getUrl())
            .description(exchange.getDescription())
            .build();
    }
}

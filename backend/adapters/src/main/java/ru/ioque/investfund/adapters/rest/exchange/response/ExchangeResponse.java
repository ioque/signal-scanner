package ru.ioque.investfund.adapters.rest.exchange.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.adapters.storage.jpa.entity.exchange.ExchangeEntity;

import java.io.Serializable;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ExchangeResponse implements Serializable {
    String name;
    String url;
    String description;

    public static ExchangeResponse fromEntity(ExchangeEntity exchange) {
        return ExchangeResponse.builder()
            .name(exchange.getName())
            .url(exchange.getUrl())
            .description(exchange.getDescription())
            .build();
    }
}

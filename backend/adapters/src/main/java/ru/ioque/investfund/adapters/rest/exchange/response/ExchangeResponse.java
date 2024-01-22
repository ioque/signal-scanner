package ru.ioque.investfund.adapters.rest.exchange.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.exchange.entity.Exchange;

import java.io.Serializable;
import java.util.List;

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
    List<InstrumentInListResponse> instruments;

    public static ExchangeResponse fromDomain(Exchange exchange) {
        return ExchangeResponse.builder()
            .name(exchange.getName())
            .url(exchange.getUrl())
            .description(exchange.getDescription())
            .instruments(exchange.getInstruments().stream().map(InstrumentInListResponse::fromDomain).toList())
            .build();
    }
}

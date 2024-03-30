package ru.ioque.moexdatasource.adapters.providers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.ioque.moexdatasource.application.adapters.MoexProvider;
import ru.ioque.moexdatasource.domain.instrument.Instrument;

@Component
public class MoexProviderImpl implements MoexProvider {
    QueryBuilder queryBuilder;
    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    RestClient defaultClient = RestClient.create();

    @SneakyThrows
    public JsonNode fetchInstruments(Class<? extends Instrument> type) {
        return objectMapper
            .readTree(fetch(queryBuilder.getSecurityListQuery(type)))
            .get("securities");
    }

    @Override
    public JsonNode fetchHistory(Instrument instrument) {
        return null;
    }

    @Override
    public JsonNode fetchIntradayValues(Instrument instrument) {
        return null;
    }

    private String fetch(String path) {
        try {
            return defaultClient.get()
                .uri(path)
                .retrieve()
                .body(String.class);
        } catch (Exception ex) {
            throw new RestClientException(ex.getMessage(), ex.getCause());
        }
    }
}

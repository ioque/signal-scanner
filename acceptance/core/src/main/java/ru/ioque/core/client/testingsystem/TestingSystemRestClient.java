package ru.ioque.core.client.testingsystem;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.SneakyThrows;
import ru.ioque.core.client.BaseClient;
import ru.ioque.core.dto.exchange.response.HistoryValueResponse;
import ru.ioque.core.dto.exchange.response.IntradayValueResponse;

import java.util.List;

public class TestingSystemRestClient extends BaseClient {
    public TestingSystemRestClient(String apiHost) {
        super(apiHost);
    }

    @SneakyThrows
    public List<HistoryValueResponse> getHistoryValues(int pageNumber, int pageSize) {
        String path = "/api/history-values?pageNumber=" + pageNumber + "&pageSize=" + pageSize;
        return objectMapper.readValue(get(path), new TypeReference<>(){});
    }

    @SneakyThrows
    public List<IntradayValueResponse> getIntradayValues(int pageNumber, int pageSize) {
        String path = "/api/intraday-values?pageNumber=" + pageNumber + "&pageSize=" + pageSize;
        return objectMapper.readValue(get(path), new TypeReference<>(){});
    }
}

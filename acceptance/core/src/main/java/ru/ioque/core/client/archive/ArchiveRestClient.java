package ru.ioque.core.client.archive;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.SneakyThrows;
import ru.ioque.core.client.BaseClient;
import ru.ioque.core.dto.exchange.response.IntradayValueResponse;

import java.util.List;

public class ArchiveRestClient extends BaseClient {
    public ArchiveRestClient(String apiUrl) {
        super(apiUrl);
    }

    @SneakyThrows
    public List<IntradayValueResponse> getIntradayValues(int pageNumber, int pageSize) {
        String path = "/api/intraday-values?pageNumber=" + pageNumber + "&pageSize=" + pageSize;
        return objectMapper.readValue(get(path), new TypeReference<>(){});
    }
}

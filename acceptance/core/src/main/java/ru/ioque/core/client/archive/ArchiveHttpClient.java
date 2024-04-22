package ru.ioque.core.client.archive;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.SneakyThrows;
import ru.ioque.core.client.JsonHttpClient;
import ru.ioque.core.dto.datasource.response.IntradayResponse;

import java.util.List;

public class ArchiveHttpClient extends JsonHttpClient {
    public ArchiveHttpClient(String apiUrl) {
        super(apiUrl);
    }

    @SneakyThrows
    public List<IntradayResponse> getIntradayValues(int pageNumber, int pageSize) {
        String path = "/api/archive/intraday?pageNumber=" + pageNumber + "&pageSize=" + pageSize;
        return objectMapper.readValue(get(path), new TypeReference<>(){});
    }
}

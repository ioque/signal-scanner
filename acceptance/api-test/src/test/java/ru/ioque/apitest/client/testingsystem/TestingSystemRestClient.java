package ru.ioque.apitest.client.testingsystem;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import ru.ioque.apitest.client.RestTemplateFacade;
import ru.ioque.apitest.client.signalscanner.response.IntradayValueResponse;
import ru.ioque.apitest.client.testingsystem.response.DailyValueResponse;

import java.util.List;

@Component
@AllArgsConstructor
public class TestingSystemRestClient {
    RestTemplateFacade restTemplateFacade;
    ObjectMapper objectMapper;

    @SneakyThrows
    public List<DailyValueResponse> getDailyValues(int pageNumber, int pageSize) {
        String path = "/api/daily-values?pageNumber=" + pageNumber + "&pageSize=" + pageSize;
        return objectMapper.readValue(restTemplateFacade.get(path, String.class), new TypeReference<>(){});
    }

    @SneakyThrows
    public List<IntradayValueResponse> getIntradayValues(int pageNumber, int pageSize) {
        String path = "/api/intraday-values?pageNumber=" + pageNumber + "&pageSize=" + pageSize;
        return objectMapper.readValue(restTemplateFacade.get(path, String.class), new TypeReference<>(){});
    }
}

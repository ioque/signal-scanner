package ru.ioque.investfund.adapters.exchagne.moex.client;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

@Builder
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PageReader {
    int pageSize;
    String url;
    Function<String, JsonNode> fetch;

    public List<JsonNode> readAllPage() {
        final List<JsonNode> batches = new ArrayList<>();
        int start = 0;
        var page = fetch.apply(currentUrl(start));
        while (isNotEnd(page)) {
            batches.add(page);
            start += pageSize;
            page = fetch.apply(currentUrl(start));
        }
        return batches;
    }

    private String currentUrl(int start) {
        return String.format("%slimit=%s&start=%s", url, pageSize, start);
    }

    private boolean isNotEnd(JsonNode page) {
        return Objects.nonNull(page) && Objects.nonNull(page.get("data")) && !page.get("data").isEmpty();
    }
}

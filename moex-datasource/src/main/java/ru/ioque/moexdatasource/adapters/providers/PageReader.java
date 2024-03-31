package ru.ioque.moexdatasource.adapters.providers;

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
    long start;
    int pageSize;
    String url;
    Function<String, JsonNode> fetch;

    public List<JsonNode> readAllPage() {
        final List<JsonNode> batches = new ArrayList<>();
        long _start = start;
        var page = fetch.apply(currentUrl(_start));
        while (isNotEnd(page)) {
            batches.add(page);
            _start += pageSize;
            page = fetch.apply(currentUrl(_start));
        }
        return batches;
    }

    private String currentUrl(long start) {
        return String.format("%slimit=%s&start=%s", url, pageSize, start);
    }

    private boolean isNotEnd(JsonNode page) {
        return Objects.nonNull(page) && Objects.nonNull(page.get("data")) && !page.get("data").isEmpty();
    }
}

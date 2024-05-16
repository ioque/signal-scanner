package ru.ioque.datasource;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import ru.ioque.core.datagenerator.intraday.IntradayValue;

import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

@Slf4j
@RestController
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DatasourceStreamController {

    @GetMapping(produces = TEXT_EVENT_STREAM_VALUE)
    public Flux<IntradayValue> sse() {
        return Flux.empty();
    }
}

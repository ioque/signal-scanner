package ru.ioque.moexdatasource.application.fakes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;
import ru.ioque.moexdatasource.application.adapters.MoexProvider;
import ru.ioque.moexdatasource.domain.instrument.CurrencyPair;
import ru.ioque.moexdatasource.domain.instrument.Futures;
import ru.ioque.moexdatasource.domain.instrument.Index;
import ru.ioque.moexdatasource.domain.instrument.Instrument;
import ru.ioque.moexdatasource.domain.instrument.Stock;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class FakeMoexProvider implements MoexProvider {

    @Override
    @SneakyThrows
    public List<JsonNode> fetchInstruments(Class<? extends Instrument> type) {
        Resource resource = new ClassPathResource("instruments_" + typeToFileMap.get(type) + ".json");
        byte[] fileData = FileCopyUtils.copyToByteArray(resource.getInputStream());
        return List.of(new ObjectMapper().readTree(new String(fileData)).get("securities"));
    }

    @Override
    @SneakyThrows
    public List<JsonNode> fetchHistory(Instrument instrument, LocalDate from, LocalDate to) {
        Resource resource = new ClassPathResource("trading_results_" + instrument.getTicker() + ".json");
        byte[] fileData = FileCopyUtils.copyToByteArray(resource.getInputStream());
        return List.of(new ObjectMapper().readTree(new String(fileData)).get("history"));
    }

    @Override
    @SneakyThrows
    public List<JsonNode> fetchIntradayValues(Instrument instrument, Long start) {
        Resource resource = new ClassPathResource("intraday_value_" + instrument.getTicker() + ".json");
        byte[] fileData = FileCopyUtils.copyToByteArray(resource.getInputStream());
        return List.of(new ObjectMapper().readTree(new String(fileData)).get("trades"));
    }

    Map<Class<? extends Instrument>, String> typeToFileMap = Map.of(
        Stock.class, "STOCK",
        Futures.class, "FUTURES",
        CurrencyPair.class, "CURRENCY",
        Index.class, "INDEX"
    );
}

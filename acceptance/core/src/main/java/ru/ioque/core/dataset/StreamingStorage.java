package ru.ioque.core.dataset;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.ioque.core.datagenerator.history.HistoryValue;
import ru.ioque.core.datagenerator.instrument.Instrument;
import ru.ioque.core.datagenerator.intraday.IntradayValue;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class StreamingStorage implements DatasetStorage {
    private final List<Instrument> instruments = new ArrayList<>();
    private final TreeSet<IntradayValue> intradayValues = new TreeSet<>();
    private final Map<String, PriorityQueue<IntradayValue>> awaitIntradayValues = new HashMap<>();
    private final TreeSet<HistoryValue> historyValues = new TreeSet<>();
    @Getter
    private final StreamingThread streamingThread;

    public StreamingStorage(Dataset dataset) {
        instruments.addAll(dataset.getInstruments());
        historyValues.addAll(dataset.getHistoryValues());
        dataset.getIntradayValues()
                .stream()
                .collect(Collectors.groupingBy(IntradayValue::getTicker))
                .forEach((ticker, intradayValues) -> {
                    awaitIntradayValues.put(ticker, new PriorityQueue<>(intradayValues));
                });
        streamingThread = new StreamingThread(intradayValues, awaitIntradayValues);
    }

    @Override
    public List<Instrument> getInstruments() {
        return List.copyOf(instruments);
    }

    @Override
    public List<IntradayValue> getIntradayValuesBy(String ticker, Integer start) {
        return intradayValues
                .stream()
                .filter(row -> row.getTicker().equals(ticker))
                .skip(start)
                .toList();
    }

    @Override
    public List<HistoryValue> getHistoryValuesBy(String ticker, LocalDate from, LocalDate till) {
        return historyValues
                .stream()
                .filter(row -> row.getTicker().equals(ticker) && row.isBetween(from, till))
                .toList();
    }

    @AllArgsConstructor
    public static class StreamingThread extends Thread {
        private final TreeSet<IntradayValue> intradayValues;
        private final Map<String, PriorityQueue<IntradayValue>> awaitIntradayValues;

        @Override
        public void run() {
            System.out.println("run");
            ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
            service.scheduleAtFixedRate(() -> {
                awaitIntradayValues.values().forEach(queue -> {
                    if (queue.isEmpty()) {
                        service.shutdownNow();
                    }
                    final var data = queue.poll();
                    intradayValues.add(data);
                });
            }, 0, 10, TimeUnit.MILLISECONDS);
            try {
                service.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

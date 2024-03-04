package ru.ioque.acceptance.domain.scanner;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.acceptance.adapters.client.signalscanner.response.Report;
import ru.ioque.acceptance.adapters.client.signalscanner.response.Signal;
import ru.ioque.acceptance.domain.exchange.InstrumentInList;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SignalScanner {
    UUID id;
    String description;
    List<InstrumentInList> instruments;
    List<Report> reports;
    List<Signal> signals;
}

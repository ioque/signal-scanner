package ru.ioque.investfund.domain.scanner.financial.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class ScannerLog {
    String message;
    Instant time;
}

package ru.ioque.investfund.domain.scanner.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class ScannerLog {
    String message;
    LocalDateTime dateTime;
}

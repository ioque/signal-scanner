package ru.ioque.investfund.domain.core;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ErrorLog extends ApplicationLog {
    Throwable error;

    public ErrorLog(LocalDateTime timestamp, String msg, Throwable error) {
        super(timestamp, msg);
        this.error = error;
    }
}

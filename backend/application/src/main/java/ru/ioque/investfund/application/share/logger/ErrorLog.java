package ru.ioque.investfund.application.share.logger;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ErrorLog extends ApplicationLog {
    Throwable error;

    public ErrorLog(String msg, Throwable error) {
        super(msg);
        this.error = error;
    }
}

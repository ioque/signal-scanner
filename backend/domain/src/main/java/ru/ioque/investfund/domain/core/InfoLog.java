package ru.ioque.investfund.domain.core;

import java.time.LocalDateTime;

public class InfoLog extends ApplicationLog {
    public InfoLog(LocalDateTime timestamp, String msg) {
        super(timestamp, msg);
    }
}

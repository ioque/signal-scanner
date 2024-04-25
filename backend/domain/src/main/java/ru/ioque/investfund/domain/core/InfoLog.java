package ru.ioque.investfund.domain.core;

import java.time.LocalDateTime;
import java.util.UUID;

public class InfoLog extends ApplicationLog {
    public InfoLog(LocalDateTime timestamp, String msg) {
        super(timestamp, msg, null);
    }

    public InfoLog(LocalDateTime timestamp, String msg, UUID track) {
        super(timestamp, msg, track);
    }
}

package ru.ioque.investfund.domain.core;

import java.time.LocalDateTime;
import java.util.UUID;

public class WarningLog extends ApplicationLog {
    public WarningLog(LocalDateTime timestamp, String msg) {
        super(timestamp, msg, null);
    }

    public WarningLog(LocalDateTime timestamp, String msg, UUID track) {
        super(timestamp, msg, track);
    }
}

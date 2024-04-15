package ru.ioque.investfund.application.share.logger;

import java.time.LocalDateTime;

public class WarningLog extends ApplicationLog {
    public WarningLog(LocalDateTime timestamp, String msg) {
        super(timestamp, msg);
    }
}

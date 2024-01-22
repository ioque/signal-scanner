package ru.ioque.investfund.application.modules.exchange;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import ru.ioque.investfund.application.share.exception.ApplicationException;

import java.util.List;
import java.util.UUID;

@Getter
@ToString
@EqualsAndHashCode
public class TradingDataIntegrateCommand {
    List<UUID> instrumentIds;

    @Builder
    public TradingDataIntegrateCommand(List<UUID> instrumentIds) {
        this.instrumentIds = instrumentIds;
        if (instrumentIds == null || instrumentIds.isEmpty()) {
            throw new ApplicationException("Не переданы идентификаторы для интеграции торговых данных.");
        }
    }
}

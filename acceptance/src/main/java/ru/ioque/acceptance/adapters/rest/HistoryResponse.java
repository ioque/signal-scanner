package ru.ioque.acceptance.adapters.rest;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import ru.ioque.acceptance.domain.dataemulator.core.DailyResultValue;

import java.util.List;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class HistoryResponse {
    ObjectsWrapper history;

    public static HistoryResponse fromBy(List<? extends DailyResultValue> historyValues) {
        return new HistoryResponse(ObjectsWrapper.fromDataset(historyValues));
    }
}

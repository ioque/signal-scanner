package ru.ioque.apitest.rest;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import ru.ioque.core.dataemulator.core.DailyResultValue;

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

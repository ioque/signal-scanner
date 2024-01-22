package ru.ioque.acceptance.adapters.rest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import ru.ioque.acceptance.domain.dataemulator.core.DatasetObject;
import ru.ioque.acceptance.domain.dataemulator.core.DatasetValue;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class ObjectsWrapper {
    List<String> columns;
    List<List<Object>> data;

    public static ObjectsWrapper fromDataset(List<? extends DatasetObject> moexValueList) {
        if (moexValueList.isEmpty()) return new ObjectsWrapper(List.of(), List.of());
        List<String> columns = moexValueList
            .get(0).getRow().stream().sorted(DatasetValue::compareTo).map(DatasetValue::getColumnName).toList();
        List<List<Object>> data = new ArrayList<>();
        moexValueList.forEach(row -> data.add(row.getRow().stream().sorted(DatasetValue::compareTo).map(DatasetValue::getValue).toList()));
        return new ObjectsWrapper(columns, data);
    }
}

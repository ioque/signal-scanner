package ru.ioque.core.dataemulator.core;

import java.util.List;

public interface DatasetObject {
    List<? extends DatasetValue> getRow();
}

package ru.ioque.acceptance.domain.dataemulator.core;

import java.util.List;

public interface DatasetObject {
    List<? extends DatasetValue> getRow();
}

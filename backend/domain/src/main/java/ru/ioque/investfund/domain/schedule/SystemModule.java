package ru.ioque.investfund.domain.schedule;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public enum SystemModule {
    EXCHANGE("Биржа"),
    SIGNAL_SCANNER("Сканер сигналов");

    String moduleName;
}

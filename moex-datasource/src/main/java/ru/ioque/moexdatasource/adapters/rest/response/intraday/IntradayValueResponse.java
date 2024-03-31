package ru.ioque.moexdatasource.adapters.rest.response.intraday;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.moexdatasource.domain.intraday.Contract;
import ru.ioque.moexdatasource.domain.intraday.Deal;
import ru.ioque.moexdatasource.domain.intraday.Delta;
import ru.ioque.moexdatasource.domain.intraday.IntradayValue;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.Function;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public abstract class IntradayValueResponse implements Serializable {
    LocalDateTime dateTime;
    String ticker;
    Double value;
    Double price;

    public static IntradayValueResponse from(IntradayValue intradayValue) {
        return factoryMethods.get(intradayValue.getClass()).apply(intradayValue);
    }

    static Map<Class<? extends IntradayValue>, Function<IntradayValue, IntradayValueResponse>> factoryMethods = Map.of(
        Deal.class, intradayValue -> DealResponse.from((Deal) intradayValue),
        Contract.class, intradayValue -> ContractResponse.from((Contract) intradayValue),
        Delta.class, intradayValue -> DeltaResponse.from((Delta) intradayValue)
    );
}

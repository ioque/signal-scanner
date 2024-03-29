package ru.ioque.apitest.client.signalscanner.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FinancialInstrument implements Serializable {
    UUID id;
    String name;
    String ticker;
    FinancialInstrumentType type;
    Double historyWaVolume;
    Double todayVolume;
    Double firstDealPrice;
    Double lastDealPrice;
    Double prevDayClose;
    List<DailyTradingResultResponse> dailyTradingResults;
    List<IntradayValueResponse> intradayValues;
}

package ru.ioque.investfund.fixture;

import ru.ioque.investfund.domain.datasource.value.details.CurrencyPairDetail;
import ru.ioque.investfund.domain.datasource.value.details.FuturesDetail;
import ru.ioque.investfund.domain.datasource.value.details.IndexDetail;
import ru.ioque.investfund.domain.datasource.value.details.StockDetail;
import ru.ioque.investfund.domain.datasource.value.types.Isin;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

public class InstrumentDetailsFixture {
    public static final String TGKN = "TGKN";
    public static final String TGKB = "TGKB";
    public static final String ROSN = "ROSN";
    public static final String TATN = "TATN";
    public static final String LKOH = "LKOH";
    public static final String SBER = "SBER";
    public static final String SBERP = "SBERP";
    public static final String SIBN = "SIBN";
    public static final String AFKS = "AFKS";
    public static final String USD000UTSTOM = "USD000UTSTOM";
    public static final String BRF4 = "BRF4";
    public static final String IMOEX = "IMOEX";

    public IndexDetail imoexDetails() {
        return IndexDetail
            .builder()
            .ticker(Ticker.from(IMOEX))
            .name("Индекс МосБиржи")
            .shortName("Индекс МосБиржи")
            .annualLow(100D)
            .annualHigh(100D)
            .build();
    }

    public StockDetail afks() {
        return StockDetail
            .builder()
            .ticker(Ticker.from(AFKS))
            .shortName("ао Система")
            .name("АФК Система")
            .lotSize(10000)
            .regNumber("1-05-01669-A")
            .isin(Isin.from("RU000A0DQZE3"))
            .listLevel(1)
            .build();
    }

    public StockDetail sberp() {
        return StockDetail
            .builder()
            .ticker(Ticker.from(SBERP))
            .shortName("Сбер п")
            .name("Сбербанк П")
            .lotSize(100)
            .regNumber("20301481B")
            .isin(Isin.from("RU0009029557"))
            .listLevel(1)
            .build();
    }

    public StockDetail sber() {
        return StockDetail
            .builder()
            .ticker(Ticker.from(SBER))
            .shortName("Сбер")
            .name("Сбербанк")
            .lotSize(100)
            .regNumber("10301481B")
            .isin(Isin.from("RU0009029540"))
            .listLevel(1)
            .build();
    }

    public StockDetail sibn() {
        return StockDetail
            .builder()
            .ticker(Ticker.from(SIBN))
            .shortName("Газпромнефть")
            .name("Газпромнефть")
            .lotSize(100)
            .regNumber("1-01-00146-A")
            .isin(Isin.from("RU0009062467"))
            .listLevel(1)
            .build();
    }

    public FuturesDetail brf4() {
        return FuturesDetail
            .builder()
            .ticker(Ticker.from(BRF4))
            .name("Фьючерсный контракт BR-1.24")
            .shortName("BR-1.24")
            .assetCode("BR")
            .lowLimit(100D)
            .highLimit(100D)
            .initialMargin(100D)
            .lotVolume(1000)
            .build();
    }

    public StockDetail lkohDetails() {
        return StockDetail
            .builder()
            .ticker(Ticker.from(LKOH))
            .shortName("Лукойл")
            .name("Лукойл")
            .lotSize(100)
            .regNumber("1-01-00077-A")
            .isin(Isin.from("RU0009024277"))
            .listLevel(1)
            .build();
    }

    public StockDetail tatnDetails() {
        return StockDetail
            .builder()
            .ticker(Ticker.from(TATN))
            .shortName("Татнефть")
            .name("Татнефть")
            .isin(Isin.from("RU0009033591"))
            .regNumber("1-03-00161-A")
            .lotSize(100)
            .listLevel(1)
            .build();
    }

    public StockDetail rosnDetails() {
        return StockDetail
            .builder()
            .ticker(Ticker.from(ROSN))
            .shortName("Роснефть")
            .name("Роснефть")
            .isin(Isin.from("RU000A0J2Q06"))
            .regNumber("1-02-00122-A")
            .lotSize(100)
            .listLevel(1)
            .build();
    }

    public CurrencyPairDetail usdRubDetails() {
        return CurrencyPairDetail
            .builder()
            .ticker(Ticker.from(USD000UTSTOM))
            .shortName("USDRUB_TOM")
            .name("USDRUB_TOM - USD/РУБ")
            .faceUnit("RUB")
            .lotSize(1000)
            .build();
    }

    public StockDetail tgkbDetails() {
        return StockDetail
            .builder()
            .ticker(Ticker.from(TGKB))
            .name("TGKB")
            .shortName("TGKB")
            .isin(Isin.from("RU000A0JNGS7"))
            .regNumber("1-01-10420-A")
            .listLevel(3)
            .lotSize(100)
            .build();
    }

    public StockDetail tgknDetails() {
        return StockDetail
            .builder()
            .ticker(Ticker.from(TGKN))
            .name("TGKN")
            .shortName("TGKN")
            .lotSize(100)
            .isin(Isin.from("RU000A0H1ES3"))
            .regNumber("1-01-22451-F")
            .listLevel(2)
            .build();
    }
}

export type Exchange = {
    id: string;
    name: string;
    url: string;
    description: string;
}

export type Instrument = {
    id: string;
    ticker: string;
    shortName: string;
    dailyValues: Array<DailyValue>;
    intradayValues: Array<IntradayValue>;
}

export type InstrumentInList = {
    id: string;
    shortName: string;
    ticker: string;
}

export type DailyValue = {
    tradeDate: string;
    ticker: string;
    value: number;
    openPrice: number;
    closePrice: number;
}

export type IntradayValue = {
    tradeNumber: number;
    dateTime: string;
    price: number;
}
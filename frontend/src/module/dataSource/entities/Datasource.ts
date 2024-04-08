export type Datasource = {
    id: string;
    name: string;
    url: string;
    description: string;
}

export type Instrument = {
    id: string;
    ticker: string;
    shortName: string;
    historyValues: Array<HistoryValue>;
    intradayValues: Array<IntradayValue>;
}

export type InstrumentInList = {
    id: string;
    shortName: string;
    ticker: string;
}

export type HistoryValue = {
    tradeDate: string;
    ticker: string;
    value: number;
    openPrice: number;
    closePrice: number;
}

export type IntradayValue = {
    number: number;
    dateTime: string;
    price: number;
}
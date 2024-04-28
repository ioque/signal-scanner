export type Datasource = {
    id: string;
    name: string;
    url: string;
    description: string;
}

export type Instrument = {
    id: string;
    ticker: string;
    lastUpdate: Date;
    shortName: string;
    updatable: boolean;
    todayValue: number;
    todayLastPrice: number;
    todayFirstPrice: number;
    lastIntradayNumber: number;
    historyValues: Array<HistoryValue>;
}

export type InstrumentInList = {
    id: string;
    shortName: string;
    ticker: string;
    todayValue: number;
    todayLastPrice: number;
}

export type HistoryValue = {
    tradeDate: Date;
    ticker: string;
    value: number;
    openPrice: number;
    closePrice: number;
}
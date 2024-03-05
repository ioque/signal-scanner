import {InstrumentInList} from "../../dataSource/entities/Exchange";

export type Scanner = {
    id: string;
    description: string;
    config: AnomalyVolumeScannerConfig | CorrelationSectoralConfig | PrefSimpleConfig | SectoralRetardScannerConfig;
    instruments: Array<InstrumentInList>;
    logs: Array<ScannerLog>
    signals: Array<Signal>
}

export type ScannerInList = {
    id: string;
    description: string;
}

export type Signal = {
    ticker: string;
    dateTime: Date;
    isBuy: boolean;
}

export type ScannerLog = {
    dateTime: Date;
    message: string;
}

export type AnomalyVolumeScannerConfig = {
    scaleCoefficient: number;
    historyPeriod: number;
    indexTicker: string;
}

export type CorrelationSectoralConfig = {
    futuresOvernightScale: number;
    stockOvernightScale: number;
    futuresTicker: string;
}

export type PrefSimpleConfig = {
    spreadParam: number;
}

export type SectoralRetardScannerConfig = {
    historyScale: number;
    intradayScale: number;
}
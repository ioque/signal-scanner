import {InstrumentInList} from "../../dataSource/entities/Datasource";

export type Scanner = {
    id: string;
    status: string;
    description: string;
    workPeriodInMinutes: number;
    lastExecutionDateTime: Date;
    config: AnomalyVolumeAlgorithmConfig | CorrelationSectoralAlgorithmConfig | PrefSimpleAlgorithmConfig | SectoralRetardAlgorithmConfig;
    instruments: Array<InstrumentInList>;
    signals: Array<Signal>
}

export type ScannerInList = {
    id: string;
    status: string;
    description: string;
    workPeriodInMinutes: number;
    signalCounts: number;
    lastExecutionDateTime: Date;
}

export type Signal = {
    price: number;
    ticker: string;
    summary: string;
    dateTime: Date;
    isBuy: boolean;
}

export type ScannerLog = {
    dateTime: Date;
    message: string;
}

export interface AnomalyVolumeAlgorithmConfig {
    scaleCoefficient: number;
    historyPeriod: number;
    indexTicker: string;
}

export interface CorrelationSectoralAlgorithmConfig {
    futuresOvernightScale: number;
    stockOvernightScale: number;
    futuresTicker: string;
}

export interface PrefSimpleAlgorithmConfig {
    spreadParam: number;
}

export interface SectoralRetardAlgorithmConfig {
    historyScale: number;
    intradayScale: number;
}

export function isAnomalyVolumeAlgorithmConfig(item: any): item is AnomalyVolumeAlgorithmConfig {
    return 'scaleCoefficient' in item;
}

export function isCorrelationSectoralAlgorithmConfig(item: any): item is CorrelationSectoralAlgorithmConfig {
    return 'futuresOvernightScale' in item;
}

export function isPrefSimpleConfig(item: any): item is PrefSimpleAlgorithmConfig {
    return 'spreadParam' in item;
}

export function isSectoralRetardScannerConfig(item: any): item is SectoralRetardAlgorithmConfig {
    return 'historyScale' in item;
}
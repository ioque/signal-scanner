export type EmulatedPosition = {
    scannerId: string;
    ticker: string;
    openPrice: number;
    lastPrice: number;
    closePrice: number;
    profit: number;
    isOpen: boolean;
}
import {Exchange, Instrument, InstrumentInList} from "../../module/dataSource/entities/Exchange";
import {Statistic} from "../../module/statistic/entities/Statistic";
import {Scanner, ScannerInList} from "../../module/scanner/entities/Scanner";

const baseUrl: string = "http://localhost:8080/api/v1";

export const fetchExchange = async (): Promise<Exchange> => {
    const response = await fetch(baseUrl + "/exchange", headersForGetRequest());
    return await response.json();
}

export const fetchInstruments = async (): Promise<Array<InstrumentInList>> => {
    const response = await fetch(baseUrl + "/instruments", headersForGetRequest());
    return await response.json();
}

function headersForGetRequest(): RequestInit {
    return {
        headers: {'Content-Type': 'application/json'},
        mode: 'cors',
        method: "GET"
    };
}

export const fetchInstrumentDetails = async (ticker: string): Promise<Instrument> => {
    const response = await fetch(baseUrl + "/instruments/" + ticker, headersForGetRequest());
    return await response.json();
}

export const fetchInstrumentStatistic = async (ticker: string): Promise<Statistic> => {
    const response = await fetch(baseUrl + "/instruments/" + ticker + "/statistic", headersForGetRequest());
    return await response.json();
}

export const fetchScanners = async (): Promise<Array<ScannerInList>> => {
    const response = await fetch(baseUrl + "/signal-scanner");
    return await response.json();
}

export const fetchScanner = async (id: string): Promise<Scanner> => {
    const response = await fetch(baseUrl + "/signal-scanner/" + id);
    return await response.json();
}
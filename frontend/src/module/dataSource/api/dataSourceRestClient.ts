import {Exchange, Instrument, InstrumentInList} from "../entities/Exchange";
import {GetRequest} from "../../../share/api/restClient";

export const fetchExchange = async (): Promise<Exchange> => {
    const response = await GetRequest("/exchange", []);
    return await response.json();
}

export const fetchInstruments = async (): Promise<Array<InstrumentInList>> => {
    const response = await GetRequest("/instruments", []);
    return await response.json();
}

export const fetchInstrumentDetails = async (ticker: string): Promise<Instrument> => {
    const response = await GetRequest("/instruments/" + ticker, []);
    return await response.json();
}
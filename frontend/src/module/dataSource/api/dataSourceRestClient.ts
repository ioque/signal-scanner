import {Datasource, Instrument, InstrumentInList} from "../entities/Datasource";
import {GetRequest} from "../../../share/api/restClient";

export const fetchDatasourceList = async (): Promise<Array<Datasource>> => {
    const response = await GetRequest("datasource", []);
    return await response.json();
}

export const fetchDatasource = async (datasourceId: string): Promise<Datasource> => {
    const response = await GetRequest("datasource/" + datasourceId, []);
    return await response.json();
}

export const fetchInstrumentList = async (datasourceId: string): Promise<Array<InstrumentInList>> => {
    const response = await GetRequest("datasource/" + datasourceId + "/instrument", []);
    return await response.json();
}

export const fetchInstrumentDetails = async (datasourceId: string, ticker: string): Promise<Instrument> => {
    const response = await GetRequest("datasource/" + datasourceId + "/instrument/" + ticker, []);
    return await response.json();
}
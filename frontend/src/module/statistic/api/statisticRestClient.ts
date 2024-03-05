import {Statistic} from "../entities/Statistic";
import {GetRequest} from "../../../share/api/restClient";

export const fetchInstrumentStatistic = async (ticker: string): Promise<Statistic> => {
    const response = await GetRequest("/instruments/" + ticker + "/statistic", []);
    return await response.json();
}
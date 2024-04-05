import {Scanner, ScannerInList} from "../entities/Scanner";
import {GetRequest} from "../../../share/api/restClient";

export const fetchScanners = async (): Promise<Array<ScannerInList>> => {
    const response = await GetRequest("/signal-scanner", []);
    return await response.json();
}

export const fetchScanner = async (id: string): Promise<Scanner> => {
    const response = await GetRequest("/signal-scanner/" + id, []);
    console.log(response)
    return await response.json();
}
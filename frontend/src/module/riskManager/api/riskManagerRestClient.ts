import {GetRequest} from "../../../share/api/restClient";
import {EmulatedPosition} from "../entities/EmulatedPosition";

export const fetchEmulatedPositions = async (): Promise<Array<EmulatedPosition>> => {
    const response = await GetRequest("emulated-position", []);
    return await response.json();
}
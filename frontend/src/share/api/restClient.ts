import {Instrument} from "../../module/dataSource/entities/Instrument";

const baseUrl: string = "http://localhost:8080/api/v1";

export const fetchInstruments = async (): Promise<Array<Instrument>> => {
    const response = await fetch(baseUrl + "/instruments", {
        headers: {'Content-Type':'application/json'},
        mode: 'cors',
        method: "GET"
    });
    return await response.json();
}
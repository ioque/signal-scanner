const apiHost = process.env.API_HOST || 'localhost';
const baseUrl: string = "http://" + apiHost + ":8080/api/v1";

export type QueryParam = {
    name: string;
    value: string;
}

const paramsToString = (params: Array<QueryParam>) => {
    if (params.length === 0) return "";
    let str = "?";
    params.forEach(row => {
        str = str + row.name + "=" + row.value + "&"
    })
    return str;
}

const GetRequestInit = (): RequestInit => {
    return {
        headers: {'Content-Type': 'application/json'},
        mode: 'cors',
        method: "GET"
    };
}

const url = (path: string, params: Array<QueryParam>) => {
    return baseUrl + path + paramsToString(params)
}

export const GetRequest = async (path: string, params: Array<QueryParam>) => {
    return await fetch(url(path, params), GetRequestInit());
}
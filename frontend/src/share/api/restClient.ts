export type QueryParam = {
    name: string;
    value: string | number;
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
    if (!process.env.NODE_ENV || process.env.NODE_ENV === 'development') {
        return "http://localhost:8080/api/" + path + paramsToString(params)
    }
    return "/api/" + path + paramsToString(params)
}

export const GetRequest = async (path: string, params: Array<QueryParam>) => {
    return await fetch(url(path, params), GetRequestInit());
}
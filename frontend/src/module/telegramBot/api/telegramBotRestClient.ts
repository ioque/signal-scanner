import {GetRequest} from "../../../share/api/restClient";
import {TelegramChat} from "../entities/TelegramChat";

export const fetchSubscribers = async (): Promise<Array<TelegramChat>> => {
    const response = await GetRequest("telegram-chat", []);
    return await response.json();
}
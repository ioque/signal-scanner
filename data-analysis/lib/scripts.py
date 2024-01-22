import requests
import pandas as pd


def get_dataframe(board, ticker, left_date, right_date, limit):
    url = "https://iss.moex.com/iss/history/engines/stock/markets/shares/boards/" + board + "/securities/" + ticker + ".json?from=" + left_date + "&till=" + right_date + "&limit=" + limit
    resp = requests.get(url)
    resp_dict = resp.json()

    columns = resp_dict['history']['columns']
    rows = resp_dict['history']['data']

    return pd.DataFrame(rows, columns=columns)
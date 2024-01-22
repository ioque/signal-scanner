from lib.scripts import get_dataframe
from strategy.volume_strategy import run_volume_strategy


def run():
    tickers = ["TGKB","VRSB", "KROT", "KUBE", "MSTT", "KBSB", "TNSE", "GAZA", "KUZB"]
    run_volume_strategy()


if __name__ == '__main__':
    run()

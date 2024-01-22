ИНДИКАТОРЫ РЫНКА

STOCK https://iss.moex.com/iss/engines/stock/markets/shares/boards/TQBR/securities.json
1) SECID [0]      ex. "ABIO"
2) SHORTNAME [2]  ex. "iАРТГЕН ао"
3) LOTSIZE [4]    ex. 10
4) SECNAME [9]    ex. 'ПАО "Артген"'
5) ISIN [19]      ex. "RU000A0JNAB6"
6) REGNUMBER [21] ex. "1-01-08902-A"
7) ISSUESIZE - объем выпуска, FACEUNIT номинальная стоимость
8) LISTLEVEL - котировальный лист
9) CURRENCYID - котировальный лист

INDEX https://iss.moex.com/iss/engines/stock/markets/index/boards/SNDX/securities.json
1) SECID [0]        ex. "IMOEX"
2) NAME [2] 	    ex. "Индекс МосБиржи"
3) SHORTNAME [4] 	ex. "Индекс МосБиржи"
4) ANNUALHIGH [5] 	ex. 3287.34
5) ANNUALLOW [6] 	ex. 2102.24
6) CURRENCYID

FUTURES https://iss.moex.com/iss/engines/futures/markets/forts/boards/RFUD/securities.json
1) SECID [0]      ex. "AEH4"
2) SHORTNAME [2]  ex. "AED-3.24"
3) SECNAME [3]    ex. "Фьючерсный контракт AED-3.24"
4) LOTVOLUME [13] ex. 1000
5) LASTDELDATE [13] ex. дата исполнения контракта
6) LASTTRADEDATE [13] ex. дата последних торгов контракта
7) SECTYPE [13] ex. BR (брент фьючерс 4 квартал = BRF4)
8) ASSETCODE [13] ex. BR (брент фьючерс 4 квартал = BRF4)
9) INITIALMARGIN первоначальная маржа


https://bcs-express.ru/novosti-i-analitika/torgovlia-f-iuchersami-na-rossiiskom-rynke


CURRENCY https://iss.moex.com/iss/engines/currency/markets/selt/boards/CETS/securities.json
1) SECID [0]        ex. "AEDRUBTODTOM"
2) SHORTNAME [2]    ex. "AED_TODTOM"
3) LOTSIZE [3]      ex. 100000
4) SECNAME [10] 	ex. "AED_TODTOM - СВОП AED/РУБ"
5) MINSTEP - минимальный шаг с которым торгуется валютная пара
6) FACEUNIT - покупаемая валюта в валютной паре

HISTORY CURRENCY https://iss.moex.com/iss/history/engines/currency/markets/selt/boards/CETS/securities/USD000UTSTOM.json?from=2023-01-01&till=2023-12-31
1) TRADEDATE [1]    ex. "2023-05-29"
2) SHORTNAME [2]    ex. "USDRUB_TOM"
3) SECID [3] 	    ex. "USD000UTSTOM"
4) OPEN [4] 	    ex. 79.915
5) LOW [5]          ex. 79.9
6) HIGH [6] 	    ex. 80.98
7) CLOSE [7] 	    ex. 80.83
8) NUMTRADES [8]    ex. 23736 Количество сделок
9) VOLRUR [9]       ex. 84 283 873 497.5 Объем сделок в рублях
10) WAPRICE [10] 	ex. 80.2195 Средневзвешенный курс

HISTORY FUTURES https://iss.moex.com/iss/history/engines/futures/markets/forts/boards/RFUD/securities/BRF4.json?from=2023-01-01&till=2023-12-31
1) TRADEDATE [1] 	      ex. "2023-12-01"
2) SECID [2] 	          ex. "BRF4"
3) OPEN [3]               ex. 84.16 мб null
4) LOW [4] 	              ex. 81.27 мб null
5) HIGH [5]               ex. 84.16 мб null
6) CLOSE [6] 	          ex. 82.06 мб null
7) OPENPOSITIONVALUE [7]  ex. 38990109493.72
8) VALUE [8]              ex. 44689997496.1 оборот
9) VOLUME [9]             ex. 601133 кол-во контрактов

HISTORY STOCK https://iss.moex.com/iss/history/engines/stock/markets/shares/boards/TQBR/securities/AFKS.json?from=2023-01-01&till=2023-12-31
1) TRADEDATE [1] ex. "2023-01-03"
2) SHORTNAME [2] ex. "Система ао"
3) SECID [3]     ex. "AFKS"
4) NUMTRADES [4] ex. 5116
5) VALUE [5]     ex. 75 900 025.2 оборот
6) OPEN [6]      ex. 11.937
7) LOW [7]       ex. 11.937
8) HIGH [8]      ex. 12.19
9) WARPRICE [10] ex. 12.075
10) CLOSE [11]   ex. 12.169
11) VOLUME[12]   ex. 6 285 900 кол-во проданных акций

HISTORY STOCK INDEX https://iss.moex.com/iss/history/engines/stock/markets/index/boards/SNDX/securities/IMOEX.json?from=2023-01-01&till=2023-12-31
1) SECID [1] 	         ex. "IMOEX"
2) TRADEDATE [2] 	     ex. "2023-12-01"
3) SHORTNAME [3] 	     ex. "Индекс МосБиржи"
4) NAME [4] 	         ex. "Индекс МосБиржи"
5) CLOSE [5] 	         ex. 3142.29
6) OPEN [6] 	         ex. 3155.87
7) HIGH [7]	             ex. 3162.97
8) LOW [8] 	             ex. 3136.56
9) VALUE [9]             ex. 40584354460.75 оборот
10) CAPITALIZATION [13]  ex. 5486310253735.887

DAILY CURRENCY DEAL https://iss.moex.com/iss/engines/currency/markets/selt/boards/CETS/securities/USD000UTSTOM/trades.json?limit=100&start=0
1) TRADETIME [1] ex. "06:59:33"
2) SECID [3]     ex. "USD000UTSTOM"
3) PRICE [4]     ex. 91.135
4) QUANTITY [5]  ex. 1
5) VALUE [6]     ex. 91135
6) SYSTIME [9]   ex. "2023-12-21 06:59:34"
7) BUYSELL [10]  ex. "S"

DAILY FUTURES DEAL https://iss.moex.com/iss/engines/futures/markets/forts/boards/RFUD/securities/BBRF4/trades.json?limit=100&start=0
1) SECID [2]     ex. "BRF4"
2) TRADEDATE [3] ex. "2023-12-21"
3) TRADETIME [4] ex. "19:05:00"
4) PRICE [5] 	 ex. 79.24
5) QUANTITY [6]  ex. 1
6) SYSTIME [7] 	 ex. "2023-12-21 19:05:01"

DAILY STOCK DEAL https://iss.moex.com/iss/engines/stock/markets/shares/boards/TQBR/securities/AFKS/trades.json?limit=100&start=0
1) TRADETIME [1] ex. "09:59:52"
2) SECID [3]     ex. "SBER"
3) PRICE [4] 	 ex. 266.4
4) QUANTITY [5]  ex.  1
5) VALUE [6]     ex. 2664
6) SYSTIME [9]   ex. "2023-12-21 09:59:52"
7) BUYSELL [10]  ex. "S"

DAILY INDEX VALUE https://iss.moex.com/iss/engines/stock/markets/index/boards/SNDX/securities/IMOEX/trades.json?limit=100&start=0
1) SECID [2]     ex. "IMOEX"
2) TRADEDATE [3] ex. "2023-12-21"
3) TRADETIME [4] ex. "10:00:01"
4) PRICE [5]     ex. 3093.46
5) VALUE [6]     ex. 68292599
6) SYSTIME [7] 	 ex. "2023-12-21 10:00:01"
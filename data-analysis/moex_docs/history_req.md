## Историческая информация

### Сделки по инструменту
        GET /engines/[engine]/markets/[datasource]/securities/[instrumentInList]/trades.[format]
        Например https://iss.moex.com/iss/engines/stock/markets/shares/securities/AFLT/trades.xml
        Параметры
        - tradeno
            Номер сделки, с которого следует начать возвращать данные.
            В данных возвращаются сделки начиная с номера переданной сделки переданого в параметре tradeno.
            Если необходимо начать со следующей сделки - используйте параметр "next_trade=1".
            Сделки возвращаются в порядке их заключения. На срочном рынке номер более поздней сделки может быть менее номера предыдущей.
        - securities
            Фильтр-список инструментов. Получение данных производится только по инстурментам из списка securities. Например: securities=GAZP,AFLT,LKOH
        - limit - Количество строк в данных. Доступные значения: 1, 10, 100, 1000, 5000
        - reversed - обратный порядок сортировки
        - previous_session
            Сделки предыдущей сессии для forts, options рынков и для индексов.
        - recno
            Только для срочного рынка (FORTS + OPTIONS).
            Фильтрация по полю RECNO (в порядке заключения сделок).
            Заменяет собой фильтр tradeno.
            Если необходимо начать со следующей сделки - используйте параметр "next_trade=1"
        - next_trade - Не включать текущую сделку в выдачу.
        - start
            Номер строки (отсчет с нуля), с которой следует начать порцию возвращаемых данных (см. рук-во разработчика).
            Получение ответа без данных означает, что указанное значение превышает число строк, возвращаемых запросом.
        - yielddatetype
            Тип доходности. Возможные значения: MBS, MATDATE, OFFERDATE

### История по всем бумагам на рынке за одну дату
        GET /history/engines/[engine]/markets/[datasource]/sessions/[session]/securities
        Например: https://iss.moex.com/iss/history/engines/stock/markets/index/securities.xml?date=2010-11-22
        Например: https://iss.moex.com/iss/history/engines/stock/markets/shares/boards/TQBR/securities/AFKS.xml?date=2010-11-22
        - sort_order
        - date - Дата за которую необходимо вывести данные. Формат: ГГГГ-ММ-ДД.
        - numtrades - Минимальное количество сделок с бумагой.
        - start
            Номер строки (отсчет с нуля), с которой следует начать порцию возвращаемых данных (см. рук-во разработчика).
            Получение ответа без данных означает, что указанное значение превышает число строк, возвращаемых запросом.
        - lang
        - interim - Показать промежуточные итоги торгов (только для валютного рынка)
        - assetcode
            Фильтр по коду базового актива. 
            Только для Фьючерсов и опционов.
        - sort_column - Поле по которому сортируются выдача данных.
        - limit - Количество отдаваемых строк. Доступные значения: 100, 50, 20, 10, 5, 1
        - tradingsession 
            Показать данные только за необходимую сессию (только для фондового рынка)
            0 - Утренняя
            1 - Основная
            2 - Вечерняя
            3 - Итого


history/engines/stock/markets/shares/boards/TQBR/securities/AFKS.xml


### Агрегированные итоги торгов за дату по рынкам
      GET /securities/[instrumentInList]/aggregates.[format] - агрегированные итоги торгов за дату по рынкам.
      Например https://iss.moex.com/iss/securities/AFKS/aggregates.xml?date=2023-04-7
      Параметры:
      - date - Дата за которую необходимо вывести данные. Формат: ГГГГ-ММ-ДД.
      - lang
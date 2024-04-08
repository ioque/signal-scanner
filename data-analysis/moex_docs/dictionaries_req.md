## Справочная информация

### Список доступных торговых систем
      GET /iss/engines.[format]
      Параметры:
      - lang, принимает значения ru или en, по дефолту ru

### Описание и режим работы торговой системы
      GET /engines/[engine].[format]
      Например https://iss.moex.com/iss/engines/stock.xml
      Параметры:
      - lang, принимает значения ru или en, по дефолту ru

### Список рынков торговой системы
      GET /engines/[engine]/markets.[format]
      Например: https://iss.moex.com/iss/engines/stock/markets.xml вернет список рынков фондового рынка и рынка депозитов
      Параметры:
      - lang, принимает значения ru или en, по дефолту ru

### Таблица инструментов торговой сессии по рынку в целом
      GET /engines/[engine]/markets/[datasource]/securities.[format]
      Например https://iss.moex.com/iss/engines/stock/markets/shares/securities.xml вернет список инструментов рынка акций.
      Параметры
      - primary_board
         Показывать инструменты - только для главного режима торгов по бумаге.
         Фильтр работает только для фондового (stock) и валютного (currency) рынка.
         Будьте осторожны используя данный фильтр за пределами основных режимов торгов.
         Список главных режимов торгов: https://iss.moex.com/iss/securities/ колонка "primary_boardid".
      - assets
         Фильтр по коду базового актива (только для срочного рынка). Колонка ASSETCODE.
         Пример: https://iss.moex.com/iss/engines/futures/markets/forts/securities?assets=EURRUBTOM,AFLT
         Ограничения: не более 5 типов бумаг.
      - index
         Выводить акции из базы индекса.
         Только для фондового рынка.
      - previous_session
         Данные предыдущей сессии для forts и options рынков
      - securities
         Фильтр-список инструментов. Получение данных производится только по инстурментам из списка securities. Например: securities=GAZP,AFLT,LKOH (не более 10 инструментов).
      - first
         Отдать n первых строк
      - leaders
         Лидеры торгов (только для futures/options)
      - nearest
         Выводить фьючерсы с ближайшей датой погашения
      - sectypes
         Фильтр по типу инструмента (только для срочного и фондового рынков).
         Например:
            - https://iss.moex.com/iss/engines/futures/markets/forts/securities?sectypes=ri,gz
            - https://iss.moex.com/iss/engines/stock/markets/bonds/securities?sectypes=8
         Ограничения: не более 5 типов бумаг.
         !! Для срочного рынка устарело. Используйте фильтр "assets".
      - sort_order (asc или desc)
      - sort_column
      - lang, принимает значения ru или en, по дефолту ru

### Данные по конкретному инструменту рынка
      GET /engines/[engine]/markets/[datasource]/securities/[instrumentInList].[format]
      Параметры повторяются из предыдущего эндпоинта (это странно немного)

### Список бумаг, торгуемых на бирже
      GET /securities
      Параметры
      - q
         Поиск инструмента по части Кода, Названию, ISIN, Идентификатору Эмитента, Номеру гос.регистрации.
         Например: https://iss.moex.com/iss/securities.xml?q=MOEX
         Слова длиной менее трёх букв игнорируются. Если параметром передано два слова через пробел. То каждое должно быть длиной не менее трёх букв.
      - engine
      - is_trading
      - datasource
      - group_by - Группировать выводимый результат по полю. Доступны значения group и type.
      - limit - Количество выводимых инструментов (5, 10, 20,100)
      - group_by_filter - Фильтровать по типам group или type. Зависит от значения фильтра group_by
      - start
         Номер строки (отсчет с нуля), с которой следует начать порцию возвращаемых данных (см. рук-во разработчика).
         Получение ответа без данных означает, что указанное значение превышает число строк, возвращаемых запросом.

### Спецификация инструмента
        GET /securities/[instrumentInList]
        Например: https://iss.moex.com/iss/securities/IMOEX.xml - вернет информацию по индексу мосбиржи
        Возвращает два блока данных - description и boards
        Параметры для description
        - lang (description и boards)
        - start (boards) - Номер строки (отсчет с нуля), с которой следует начать порцию возвращаемых данных (см. рук-во разработчика).
        Получение ответа без данных означает, что указанное значение превышает число строк, возвращаемых запросом.

### Список индексов, в которые входит инструмент
        GET /securities/[instrumentInList]/indices
        - lang
        - only_actual - Выводить индексы в базе которого бумага находится прямо сейчас
        
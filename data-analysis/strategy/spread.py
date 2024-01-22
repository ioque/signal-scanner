import time, uuid, datetime, sys

from tinkoff.invest import (
    Client,
    OrderDirection,
    OrderType
)

TOKEN = '*************************************************************************************************'

# Для тестирования на фондах без коммиссии
# figi_Ob = 'TCS00A1039N1'
# figi_Pref = 'BBG333333333'

figi_Ob = 'BBG004730N88'
figi_Pref = 'BBG0047315Y7'
Account_id = '************'
MAX_RETRIES = 30
KolVo = 0


# Функция для проверки, находится ли текущее время внутри интервала [start_time, end_time]
def is_within_time_interval(start_time, end_time):
    now = datetime.datetime.now().time()
    return start_time <= now <= end_time


# Функция для проверки, является ли текущий день недели будним (понедельник - пятница)
def is_weekday():
    now = datetime.datetime.now().weekday()
    return now >= 0 and now <= 4  # Понедельник - 0, Пятница - 4


# Функция показывающая кол-во обычки и префов в портфеле и состав портфеля
def get_portfolio_info():
    retries = 0
    while retries < MAX_RETRIES:
        try:
            PositionResponse = client.operations.get_positions(account_id=Account_id)
            break
        except Exception as e:
            print(f"Ошибка при получении информации о портфеле: {e}")
            retries += 1
            time.sleep(10)  # Подождать перед повторным запросом
    else:
        print("Не удалось получить информацию о портфеле после", MAX_RETRIES, "попыток.")

    Securities = PositionResponse.securities
    KObich = 0
    KPrefa = 0
    for security in Securities:
        if security.figi == figi_Pref:
            KPrefa = security.balance
        if security.figi == figi_Ob:
            KObich = security.balance

    print('Обычка =', KObich)
    print('Префа =', KPrefa)

    return KPrefa, KObich, Securities, PositionResponse


# Функция убирающая ордера
def close_orders():
    # Если произошла ошибка "Stream removed" или другие ошибки, связанные с соединением, можно попробовать повторить запрос через некоторый промежуток времени.
    retries = 0
    while retries < MAX_RETRIES:
        try:
            Orders = client.orders.get_orders(account_id=Account_id).orders
            for order in Orders:
                if order.execution_report_status in [4, 5]:  # 4 - Новая, 5 - Частично исполнена
                    # Снятие ордера по его ID
                    client.orders.cancel_order(account_id=Account_id, order_id=order.order_id)
                    print(f"Ордер {order.order_id} успешно снят.")
            break
        except Exception as e:
            print(f"Ошибка при снятии ордера {e}")
            retries += 1
            time.sleep(10)  # Подождать перед повторным запросом
    else:
        print("Не удалось снять ордера после", MAX_RETRIES, "попыток.")


# Функция для вычисления последней цены
def get_last_prices():
    # Если произошла ошибка "Stream removed" или другие ошибки, связанные с соединением, можно попробовать повторить запрос через некоторый промежуток времени.
    retries = 0
    while retries < MAX_RETRIES:
        try:
            LastPriceObich = client.market_data.get_order_book(figi=figi_Ob, depth=5).last_price
            LastPricePrefa = client.market_data.get_order_book(figi=figi_Pref, depth=5).last_price
            break
        except Exception as e:
            print(f"Ошибка при получении цены акции: {e}")
            retries += 1
            time.sleep(10)  # Подождать перед повторным запросом
    else:
        print("Не удалось получить цену акции после", MAX_RETRIES, "попыток.")

    LastPriceObichInteger = LastPriceObich.units + LastPriceObich.nano * 1e-9
    LastPricePrefaInteger = LastPricePrefa.units + LastPricePrefa.nano * 1e-9

    print('Цена обычки =', LastPriceObichInteger)
    print('Цена префов =', LastPricePrefaInteger)
    print('Спред =', round(LastPriceObichInteger - LastPricePrefaInteger, 2))
    return LastPriceObichInteger, LastPricePrefaInteger


def trade_stock_polnoe_ispolnenie(Client, Order, Account_id, KolVo, FigiStock, OrderDirection):
    # Если произошла ошибка "Stream removed" или другие ошибки, связанные с соединением, можно попробовать повторить запрос через некоторый промежуток времени.
    retries = 0
    while retries < MAX_RETRIES:
        try:
            Client.orders.post_order(  # Создаем заявку
                order_id=Order,  # id заявки - текущее время
                figi=FigiStock,  # Бумага
                # quantity=KolVo,                                    # Для бумаг где в лоте 1 шт.
                quantity=int(KolVo / 10),  # Для бумаг где в лоте 10 шт.
                account_id=Account_id,
                direction=OrderDirection,  # Заявка на покупку или продажу
                order_type=OrderType.ORDER_TYPE_BESTPRICE  # По лучшей цене
            )
            # print('Купили/продали', KolVo, 'шт.')              # Для бумаг где в лоте 1 шт.
            print('Купили/продали', int(KolVo / 10), 'шт.')  # Для бумаг где в лоте 10 шт.
            time.sleep(15)  # Даем время на покупку/продажу
            break  # Выходим из цикла, если выставили заявку
        except Exception as e:
            print(f"Ошибка при создании ордера: {e}")
            retries += 1
            time.sleep(10)  # Подождать перед повторным запросом
    else:
        print("Не удалось создать ордер после", MAX_RETRIES, "попыток.")


def trade_stock_procentnoe_ispolnenie(Client, Order, Account_id, LastPriceInteger, Percent, FigiStock, OrderDirection):
    # Если произошла ошибка "Stream removed" или другие ошибки, связанные с соединением, можно попробовать повторить запрос через некоторый промежуток времени.
    retries = 0
    while retries < MAX_RETRIES:
        try:
            # Получаем информацию о текущих позициях (деньги и акции)
            PositionResponse = client.operations.get_positions(account_id=Account_id)
            Money = int(
                PositionResponse.money[0].units) * 0.95  # Рассчитываем деньги, учитывая 5% на разницу между ценами
            # KolVo = int(Money/(LastPriceInteger)*Percent)         #Для бумаг где в лоте 1 шт.
            KolVo = int(
                Money / (LastPriceInteger * 10) * Percent)  # Рассчитываем количество акций, которое можем купить

            if KolVo > 1:  # Проверяем, достаточно ли денег для покупки
                # Создаем заявку на покупку
                Client.orders.post_order(
                    order_id=Order,  # ID заявки - текущее время
                    figi=FigiStock,  # Бумага
                    quantity=KolVo,  # Количество акций, которое хотим купить
                    account_id=Account_id,
                    direction=OrderDirection,  # Направление заявки (покупка или продажа)
                    order_type=OrderType.ORDER_TYPE_BESTPRICE  # Тип заявки - по лучшей цене
                )
                print('Купили/продали', KolVo, 'шт.')
                time.sleep(15)  # Даем время на покупку/продажу
                break  # Выходим из цикла, если выставили заявку
            else:
                print('Не хватает денег на покупку')
                break  # Выходим из цикла, если денег недостаточно для покупки
        except Exception as e:
            print(f"Ошибка при создании ордера: {e}")
            retries += 1
            time.sleep(10)  # Пауза в 10 секунд перед следующей попыткой
    else:
        print("Не удалось создать ордер после", MAX_RETRIES, "попыток.")


# Весь наш алгоритм основывается на КОЛИЧЕСТВЕННОМ отношении Обычки к Префу а не на ЦЕНОВОМ что более правильно. При разнице в несколько рублей хватит и колличественного а вот при разнице в десятки рублей нужно будет переписать
# Также в дальнейшем неплохо было бы если бы алгоритм сам изменял числа в услови (1,2,3,4) в зависимости от текущего спреда То есть если спред составит 8 то числа были бы (10, 9, 8, 7, 6) Пока я их задаю в ручную
def is_workday_moex():
    return is_weekday() and (
            is_within_time_interval(datetime.time(10, 5), datetime.time(18, 30)) or is_within_time_interval(
        datetime.time(19, 10), datetime.time(23, 40)))


# Если текущий день не является будним или текущее время не находится в указанных интервалах, ждем до следующего проверочного цикла
with Client(TOKEN) as client:
    print('Скрипт Strategy(0-100) запущен с акциями Сбера')

    while True:

        # Время работы мосбиржи
        if is_workday_moex():

            # Пишим логи в файл, а не в консоль
            output_file = open('D:\output.txt', 'a')
            original_stdout = sys.stdout
            sys.stdout = output_file

            print('------------------------------------------------------------------------------------------')
            # В начале проверяем есть ли акции в портфеле если только кэш то Sootnoshenie = 0
            k_prefa, KObich, Securities, PositionResponse = get_portfolio_info()

            # На всякий случай снимаем все поставленные ордера
            close_orders()

            # Узнаем цену акций
            LastPriceObichInteger, LastPricePrefaInteger = get_last_prices()
            print('------------------------------------------------------------------------------------------')

            if LastPriceObichInteger - LastPricePrefaInteger > 2.8:  # Если цена Обычки > Префов на 2.8 рублей тогда продаем обычку и покупаем преф

                print('---------------Спред больше 2,8 делаем соотношение 0% в обычке и 100% в префе---------------')
                print('Текущий спред', round(LastPriceObichInteger - LastPricePrefaInteger, 2))
                print('Дата:', datetime.datetime.now())
                print('Наш портфель акций:', Securities)
                print('У нас в портфеле денег:', int(PositionResponse.money[0].units), 'руб.')

                for security in Securities:  # Пробегаемся циклом по массиву акций

                    if security.figi == figi_Ob:  # Если у нас в портфеле обычка мы её продаем и покупаем префа

                        print('У нас в портфеле обычка', security.balance, 'шт. нужно ее продать и купить префа')
                        trade_stock_polnoe_ispolnenie(client, str(uuid.uuid4()), Account_id, security.balance, figi_Ob,
                                                      OrderDirection.ORDER_DIRECTION_SELL)  # Функция полного исполнения продает/покупает абсолютно все акции, в данном случае всю обычку в портфеле
                        print('Продали обычку', security.balance, 'шт.', 'нужно купить префа')

                        print('Покупаем префа')
                        trade_stock_procentnoe_ispolnenie(client, str(uuid.uuid4()), Account_id, LastPricePrefaInteger,
                                                          1, figi_Pref,
                                                          OrderDirection.ORDER_DIRECTION_BUY)  # Функция частичного процентного исполнения продает/покупает акции в зависимости от аргумента, в данном случае 1 = 100% или на все деньги

                    elif security.figi == figi_Pref:

                        print('У нас в портфеле префов', security.balance,
                              'шт. сидим курим, ждем когда обычка подешевеет')

                    else:

                        print('Портфель пустой, нужно купить префа')
                        trade_stock_procentnoe_ispolnenie(client, str(uuid.uuid4()), Account_id, LastPricePrefaInteger,
                                                          1, figi_Pref,
                                                          OrderDirection.ORDER_DIRECTION_BUY)  # Покупаем префа на 100% КЭШа

                print('------------------------------------------------------------------------------------------')

            elif LastPriceObichInteger - LastPricePrefaInteger < 0.4:  # Если цена Обычки < Префов на 0.4 рублей тогда продаем префа и покупаем обычку

                print('--------------Спред меньше 0.4 делаем соотношение 100% в обычке и 0% в префе--------------')
                print('Текущий спред', round(LastPriceObichInteger - LastPricePrefaInteger, 2))
                print('Дата:', datetime.datetime.now())
                print('Наш портфель акций:', Securities)
                print('У нас в портфеле денег:', int(PositionResponse.money[0].units), 'руб.')

                for security in Securities:  # Пробегаемся циклом по массиву акций

                    if security.figi == figi_Pref:  # Если у нас в портфеле префа мы их продаем и покупаем обычку

                        print('У нас в портфеле префа', security.balance, 'шт. нужно их продать и купить обычку')
                        trade_stock_polnoe_ispolnenie(client, str(uuid.uuid4()), Account_id, security.balance,
                                                      figi_Pref,
                                                      OrderDirection.ORDER_DIRECTION_SELL)  # Продаем абсолютно все префа
                        print('Продали префов', security.balance, 'шт.', 'нужно купить обычку')

                        print('Покупаем обычку')
                        trade_stock_procentnoe_ispolnenie(client, str(uuid.uuid4()), Account_id, LastPriceObichInteger,
                                                          1, figi_Ob,
                                                          OrderDirection.ORDER_DIRECTION_BUY)  # Покупаем обычку на 100% КЭШа

                    elif security.figi == figi_Ob:

                        print('У нас в портфеле обычка', security.balance,
                              'шт. сидим курим, ждем когда префа подешевеют')

                    else:

                        print('Портфель пустой, нужно купить обычки')
                        trade_stock_procentnoe_ispolnenie(client, str(uuid.uuid4()), Account_id, LastPriceObichInteger,
                                                          1, figi_Ob,
                                                          OrderDirection.ORDER_DIRECTION_BUY)  # Покупаем обычку на 100% КЭШа

                print('------------------------------------------------------------------------------------------')

            sys.stdout = original_stdout
            output_file.close()
            time.sleep(10)

        else:
            time.sleep(10)

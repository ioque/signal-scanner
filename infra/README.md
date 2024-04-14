# Инфраструктура проекта
Система запускается на одном хосте в докер-контейнерах.
Система состоит из следующих докер-контейнеров:
- backend - серверная часть приложения, выполняет бизнес-задачи.
- frontend - веб-UI приложения.
- moex-datasource - источник данных "Московская биржа", тонкий веб-клиент для взаимодействия с московской биржей через унифицированный API
- postgres - база данных
- datasource - конфигурируемый источник данных для тестирования системы. В среде stage используется по умолчанию.

# Настройка kvm-хоста
С помощью terraform выполняется создание и настройка двух виртуальных машин на kvm-хосте - для стейдж и прод окружения.

# Настройка виртуальных машин и поднятие системы
С помощью ansible выполняется настройка виртуальных машин и запуск на них системы. Доступны два варианта:
- стейдж для прогона приемочных тестов
- прод для запуска системы в штатном режиме

# CI/CD
Текущий вариант работает через gitlab-ci
Пайплан состоит из:
1) запуска модульных тестов
2) билда докер-образов и пуша их в нексус
3) деплоя на стейдж
4) запуска приемочных тестов
5) деплоя на прод по нажатию кнопки (в разработке)

Есть скрипт для Jenkins, но предпочтение пока что отдается gitlab.
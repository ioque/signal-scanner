# API системы

### Локальный запуск через gradle
export DB_HOST=localhost
export DB_PORT=<DB_PORT>
export SPRING_PROFILES_ACTIVE=test (для возможности использовать сервисные эндпоинты для очистки стейта и манипуляций со временем системы)
./gradlew bootRun

### Локальный запуск в докер-контейнере
cd ../infra && docker compose -f docker-compose-dev.yml up signal-scanner-db backend --wait
По умолчанию запускается с profile=test для доступа к сервисным эндпоинтам

### Источник данных
Для работы с Московской биржей доступен веб-клиент, являющийся адаптером публичного API Московской биржи.
Для его запуска достаточно выполнить:
cd ../moex-datasource && ./gradlew bootRun

Также есть возможность работать с фейковой имплементацией, у которой есть эндпоинт для инициализации датасета.
Для ее запуска достаточно выполнить:
cd ../acceptance && ./gradlew -p datasource bootRun
cd .. && docker compose -f docker-compose-dev.yml down -v
docker compose -f docker-compose-dev.yml up --build --wait
cd .. && docker compose -f docker-compose-dev.yml down -v --rmi all
docker compose -f docker-compose-dev.yml up --build --wait
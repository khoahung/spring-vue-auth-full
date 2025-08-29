cd auth-service
mvn clean install
cd .. && cd user-service
mvn clean install
cd ..
docker network create --driver bridge --subnet 172.19.0.0/16 --gateway=172.19.0.1 network_default_test
docker compose -f auth-service/docker-compose.yml up -d
docker compose -f user-service/docker-compose.yml up -d
docker compose -f fe-vue/docker-compose.yml up -d



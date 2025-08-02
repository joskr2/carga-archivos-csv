APP_NAME=pedidos-api
DOCKER_COMPOSE=docker-compose

# ========== GENERAL ==========

build:
	./mvnw clean package -DskipTests
	cp target/pedidos-ms-0.0.1-SNAPSHOT.jar target/pedidos-ms.jar

down:
	$(DOCKER_COMPOSE) down -v

clean:
	$(DOCKER_COMPOSE) down -v
	./mvnw clean

# ========== DEV ==========

up-dev: build
	cp .env.dev .env
	$(DOCKER_COMPOSE) --env-file .env.dev up --build

# ========== PROD ==========

up-prod: build
	cp .env.prod .env
	$(DOCKER_COMPOSE) --env-file .env.prod up --build

APP_NAME=pedidos-api
DOCKER_COMPOSE=docker-compose

# ========== GENERAL ==========

# Comando local para desarrollo (opcional)
build-local:
	./mvnw clean package -DskipTests
	cp target/pedidos-ms-0.0.1-SNAPSHOT.jar target/pedidos-ms.jar

# Test the application
test:
	./mvnw test

down:
	$(DOCKER_COMPOSE) down -v

clean:
	$(DOCKER_COMPOSE) down -v
	./mvnw clean

# ========== DEV ==========

up-dev:
	cp .env.dev .env
	$(DOCKER_COMPOSE) --env-file .env.dev up --build

# ========== PROD ==========

up-prod:
	cp .env.prod .env
	$(DOCKER_COMPOSE) --env-file .env.prod up --build

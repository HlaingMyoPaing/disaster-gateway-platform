
SHELL := /bin/bash

up-dev:
	docker compose  --env-file env/.env.dev -f docker-dev/docker-compose.db.yml \
	               -f docker-dev/docker-compose.idp.yml \
	               -f docker-dev/docker-compose.core.yml \
	               -f docker-dev/docker-compose.wiremock.yml \
	               -f docker-dev/docker-compose.proxy.yml up -d --build

down-dev:
	docker compose -f docker-dev/docker-compose.db.yml \
	               -f docker-dev/docker-compose.idp.yml \
	               -f docker-dev/docker-compose.core.yml \
	               -f docker-dev/docker-compose.wiremock.yml \
	               -f docker-dev/docker-compose.proxy.yml down -v

logs-dev:
	docker compose -f docker-dev/docker-compose.core.yml logs -f --tail=200

# Disaster Gateway Platform – Dev Starter

A disaster data processing gateway built with **Apache Camel**, running on **Spring Boot**, and secured using **Keycloak JWT**. Uses **PostgreSQL + PostGIS** for spatial data. Fully containerized for local development using Docker Compose.

## Table of Contents
- Quick Start
  - Prerequisites
  - Start dev environment
  - Useful URLs
- Windows: install make
- Troubleshooting
- Stack Overview
- License

## Quick Start

Prerequisites
- Docker & Docker Compose
- git
- (Optional) make for simplified dev commands (see Windows section if on Windows)

Start the development environment
1. Clone the repo:
```bash
git clone https://github.com/<your-org>/disaster-gateway-platform.git
cd disaster-gateway-platform
```
2. Start containers (development):
```bash
make up-dev
```

Useful URLs (local development)
- API proxy: http://host.docker.internal:3001/api/public/hello
- Keycloak Admin Console: http://host.docker.internal:8080
- WireMock Documentation: http://wiremock.org/docs/
- WireMock Admin API: http://localhost:8081/__admin

## Windows: install `make`
If you're on Windows and want to use the provided Makefile commands, install `make` (e.g. GnuWin32).

Install using Winget:
```bash
winget install GnuWin32.Make
```

Add the `make` binary folder to your PATH:
- Press Windows key → Search “Environment Variables” → Open “Edit the system environment variables”
- Click Environment Variables…
- Under System variables, find and select Path, then click Edit
- Click New, then add the folder path where make.exe is installed (e.g. `C:\Program Files (x86)\GnuWin32\bin`)

## Troubleshooting (quick)
- If services are unreachable, make sure Docker Desktop is running.
- On Windows use `host.docker.internal` to reach containers from the host.
- If Keycloak UI is not reachable, check container logs: `docker-compose logs keycloak` or `docker compose logs keycloak`
- Database tools: connect to Postgres container via the exposed port or use psql inside the container.

## Stack Overview
- Apache Camel (Spring Boot Starter)
- Keycloak (JWT Auth Provider)
- PostgreSQL + PostGIS (Spatial database)
- Docker Compose for local environment setup
- Spring Boot Actuator for health checks and monitoring

## License
This project is proprietary. See [LICENSE](./LICENSE) for details.

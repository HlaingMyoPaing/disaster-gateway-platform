# Disaster Gateway Platform – Dev Starter

A disaster data processing gateway built with **Apache Camel**, running on **Spring Boot**, and secured using **Keycloak JWT**. The platform uses **PostgreSQL + PostGIS** for handling spatial data. It's fully containerized for local development using Docker Compose.

---
## License

This project is proprietary. See [LICENSE](./LICENSE) for details.


##  Stack Overview

- ⚙️ **Apache Camel** (Spring Boot Starter)
- 🛡 **Keycloak** (JWT Auth Provider)
- 🗄 **PostgreSQL + PostGIS** (Spatial database)
- 🐳 **Docker Compose** for local environment setup
- 🧪 **Spring Boot Actuator** for health checks and monitoring

---

## 🚀 Quick Start

### Step 1: Install `make` (Windows Only)

If you're on **Windows**, install `make` for running dev commands:

#### 🛠 Install using Winget:

```bash
winget install GnuWin32.Make 
```
* Press Windows key → Search “Environment Variables” → Open “Edit the system environment variables”
* Click Environment Variables…
* Under System variables, find and select Path, then click Edit
* Click New, then add the folder path where make.exe is located.

Example : 
```bash
C:\Program Files (x86)\GnuWin32\bin
```

### Step 2: Clone the Repository
```bash
git clone https://github.com/<your-org>/disaster-gateway-platform.git
```
```bash
cd disaster-gateway
```

### Step 3: Start the Development Environment
```bash
make up-dev 
```

### Step 4: Access the Services Via Proxy Server
```bash
http://host.docker.internal:3001/api/public/hello
```

### Step 5: Access Keycloak Admin Console
```bash
http://host.docker.internal:8080
```
<img width="1772" height="728" alt="Screenshot 2025-08-26 at 20 22 05" src="https://github.com/user-attachments/assets/c507f7c6-02bf-4620-bebe-8759405e74d4" />


# 🚀 Finnish Company Search Service

A robust, secure, full-stack application for searching Finnish company details using the PRH Open API. The backend service is built with **Spring WebFlux** for reactive, non-blocking operations, and communicates securely via HTTPS. The frontend is a responsive **React** application that consumes the backend API, featuring clear component structure, input validation, and real-time data display.

---

## 📦 Tech Stack

### Backend (`prh-service`)
- ☕ **Java 17** (Eclipse Temurin)
- 🔥 **Spring Boot 3** + **Spring WebFlux**
- 🔐 **Spring Security** (SSL)
- ⚡ **WebClient** (Reactive HTTP Client)
- 🛡️ **Resilience4j** (Circuit Breaker, Retry) - *Implemented for PRH API calls*
- 📄 **Swagger UI** (OpenAPI Documentation)
- 🧪 **Junit 5**
- 🧙 **Mockito**
- 🧙 **Testcontainers**
- 🔬 **StepVerifier** (from Reactor Test)
- 💡 **Prometheus, logback, httpexchanges enabled as part of our observability culture** 

### Frontend (`web`)
- ⚛️ **React 18**
- 🟦 **TypeScript**
- ⚡ **Vite** (Build Tool)
- 💨 **Tailwind CSS** (Styling)
- 🌐 **Nginx** (Serving static files & API Gateway/Proxy)

### Infrastructure
- 🐳 **Docker** + **Docker Compose**

---

## 🧠 Design Decisions

This application is designed for efficient and user-friendly access to Finnish company data, keeping performance, security, and developer experience in mind.

### Backend (`prh-service`)
- ⚡ **Reactive Architecture with Spring WebFlux**
  Chosen for its **non-blocking I/O** capabilities, Spring WebFlux ensures the service can handle concurrent requests efficiently, which is vital for an API client that might serve many users. This also aligns well with potentially rate-limited external APIs like PRH.
- 🔐 **Security & Simplicity**
  All backend endpoints are secured over **HTTPS**. Passwords are configured via `application.yml` for convenience in development, but should be managed securely in production environments. Swagger UI is exposed via HTTPS for easy local testing.
- ♻️ **Resilience with Resilience4j**
  Circuit Breaker and Retry patterns are used to enhance the robustness of calls to the external PRH API, protecting the service from external API failures and network issues.
- 🎯 **PRH API Client**
  The backend acts as a dedicated client for the Finnish Patent and Registration Office (PRH) Open API, retrieving specific company details (Name, Website, Address, Main Line of Business) and mapping them to a simplified `CompanyDetailsDto` for the frontend. This centralizes external API interaction and data shaping.

### Frontend (`web`)
- ⚛️ **Modern React Development**
  Built with **React 18** and **TypeScript** for a robust, type-safe, and component-based user interface. Vite provides a fast development experience.
- 🎨 **Tailwind CSS for Responsive Design**
  Utility-first CSS framework for rapid and responsive UI development, ensuring the application looks good on all devices.
- 🌐 **Nginx as a Frontend Server & API Proxy**
  Nginx serves the static React application files and acts as a **reverse proxy** for API calls to the backend `prh-service`. This setup resolves potential CORS issues and provides a single entry point for the frontend, routing `/api` requests to the backend container.

The result is a **clean, reactive, and user-friendly application** that efficiently fetches and displays company information, suitable for integration into larger business systems.

---

## 📁 Project Structure & Key Files

The project has a clear monorepo-like structure within the `accountor` directory:

```

accountor/
├── prh-service/               \# Backend Spring Boot application
│   ├── src/main/java/...      \# Java source code
│   ├── Dockerfile             \# Docker build instructions for the backend
│   ├── application.yml        \# Spring Boot configuration (SSL, Auth)
│   └── keystore.p12           \# Self-signed SSL certificate for HTTPS
├── web/                       \# Frontend React application
│   ├── public/                \# Static assets for React app
│   ├── src/                   \# React source code
│   │   ├── components/        \# Reusable React components
│   │   │   ├── CompanySearch.tsx
│   │   │   └── CompanyDetails.tsx
│   │   ├── types/             \# Shared TypeScript interfaces
│   │   │   └── CompanyDetailsDto.ts
│   │   ├── App.tsx            \# Main React application component
│   │   ├── main.tsx           \# React entry point
│   │   └── index.css          \# Global CSS (Tailwind imports)
│   ├── Dockerfile             \# Docker build instructions for the frontend (Nginx)
│   ├── nginx.conf             \# Nginx configuration for serving React and proxying API
│   ├── package.json           \# Frontend dependencies and scripts
│   ├── tsconfig.json          \# TypeScript configuration for the frontend
│   └── vite.config.ts         \# Vite build configuration
├── docker-compose.yml         \# Orchestrates backend and frontend Docker containers
└── .gitignore                 \# Specifies files to ignore in Git

````

---

## 🚀 Getting Started

### Prerequisites

* **Docker Desktop** (or Docker Engine + Docker Compose) installed and running.

### Setup Steps

1.  **Navigate to the Project Root:**
    Open your terminal and change directory to the `accountor` folder (where `docker-compose.yml` is located).

2.  **Ensure Keystore is Present:**
    The backend service relies on an SSL keystore for HTTPS communication. Ensure you have your `keystore.p12` file in the `./prh-service/` directory. If you generated it in the project root previously, move it:
    ```bash
    mv keystore.p12 ./prh-service/
    ```

3.  **Build and Run the Application:**
    This command will build the Docker images for both your backend (`prh-service`) and frontend (`web`), and then start the containers. The frontend build includes compiling the React application and setting up Nginx.
    ```bash
    docker compose up --build
    ```
    To run in detached mode (in the background):
    ```bash
    docker compose up --build -d
    ```

    This command will start the following containers:

    | Container           | Description                                       | Port(s)        |
        |---------------------|---------------------------------------------------|----------------|
    | `prh_service_backend` | Spring Boot Backend Service with HTTPS & Auth     | `8888 (HTTPS)` |
    | `react_frontend`    | React Frontend served by Nginx & API Proxy        | `80 (HTTP)`    |

---

## 🌐 Accessing the Application

Once `docker compose up --build` has completed successfully:

### 🔸 Frontend Application

Open your web browser and navigate to:
**`http://localhost`**

This will display the React company search interface.

### 🔐 Backend Swagger UI (OpenAPI Documentation)

You can access the backend API documentation and test endpoints via Swagger UI:
**`https://localhost:8888/webjars/swagger-ui/index.html`**

> 🧠 **Note:** For HTTPS, your browser might display a security warning because it doesn't trust the self-signed certificate. You'll need to manually accept or proceed past this warning.

---

## 🧑‍💻 Authentication

The backend API endpoints are not secured with username/password or jwt token just for simplicity of testing. In real dev, we take this into account
-----

## 🔁 API Endpoints (Backend)

The `prh-service` exposes the following API endpoint:

| Method | Endpoint                            | Description                                        |
|--------|-------------------------------------|----------------------------------------------------|
| GET    | `/api/v1/prh/companies/{businessId}` | Retrieve detailed information for a Finnish company |

✅ This endpoint is directly consumed by the React frontend and is also visible and testable via Swagger UI.

-----

## 🔒 SSL Configuration (Backend)

SSL for the backend is configured in `prh-service/application.yml` as follows:

```yaml
server:
  port: 8443
  ssl:
    enabled: true
    key-store: file:/app/keystore.p12
    key-store-password: changeit
    key-store-type: PKCS12
    key-alias: prh-service-ssl
```

-----

## ⚠️ Important Notes

* The `keystore.p12` file **must not** be committed to Git. Please ensure it's added to your `.gitignore`:
  ```gitignore
  prh-service/keystore.p12
  ```
* **Frontend API Proxy**: The frontend (`react_frontend`) uses Nginx to proxy API requests to the backend. When making `fetch` calls from React, use relative paths starting with `/api` (e.g., `/api/v1/prh/companies/0100002-9`). Nginx handles routing these to `https://prh-service:8443` internally within Docker.
* **Logs**: The backend service logs detailed information, including potential PRH API responses and errors, which can be useful for debugging. We have exposed also metrics(prometheus), logs(logback xml), httpexchanges(tracing) and so far we have no distributed tracing enabled because we are just running a small monolith.**
-----

## 💡 Developer Tips

* **View Running Containers**:
  ```bash
  docker ps
  ```
* **Tail Backend Logs**:
  ```bash
  docker logs -f prh_service_backend
  ```
* **Tail Frontend (Nginx) Logs**:
  ```bash
  docker logs -f react_frontend
  ```
* **Applying Frontend Code Changes**:
  If you modify React code (`.tsx`, `.ts`, `.css` files), rebuild and restart *only* the frontend service for efficiency:
  ```bash
  docker compose up --build web
  ```
* **Resolving Build Cache Issues**:
  If you encounter unexpected build errors or old code persists, force a fresh build of a service:
  ```bash
  docker compose build --no-cache [service_name]
  ```
* **Rebuilding All Services**:
  For significant infrastructure changes or to ensure a completely fresh start for both services:
  ```bash
  docker compose down && docker compose up --build
  ```

-----

## ✅ Summary

* ✔️ Reactive backend microservice with Spring WebFlux.
* 🔐 Secure HTTPS communication for the backend.
* 🐳 Fully containerized full-stack application using Docker Compose.
* ⚛️ Responsive React frontend for intuitive company search.
* 📘 Swagger UI for backend API documentation and testing.
* 👮 Secured backend with Basic Authentication.
* 💡 Prometheus and logback for logs, httpexchanges for in-service tracing 
* 🛠️ Developer-friendly setup for easy local development.

<!-- end list -->

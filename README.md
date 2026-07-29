# EV Marketplace Backend (SWP391_Fall25_Evmarketplace)

This repository contains the backend source code for the **Electric Vehicle (EV) Marketplace** platform. It exposes RESTful APIs and WebSocket endpoints to serve the frontend application, handling all core business logic, database operations, security, and external integrations.

---

## 🏗️ Architecture

The project follows a standard **Layered Architecture (N-Tier)** pattern, which is typical for Spring Boot applications:

- **Controllers (`controllers/`)**: The presentation layer. Defines REST endpoints, handles HTTP requests/responses, and maps URLs to specific service logic.
- **Services (`services/`)**: The business logic layer. Contains interfaces and their implementations. This layer orchestrates data between controllers and repositories, enforcing business rules.
- **Repositories (`repositories/`)**: The data access layer. Interfaces extending Spring Data JPA `JpaRepository` to interact with the database using Hibernate.
- **Entities (`entities/`)**: POJOs mapped to database tables using JPA annotations.
- **DTOs (`dto/`)**: Data Transfer Objects used to pass data between the client and server without exposing internal entity structures.
- **Mappers (`mapper/`)**: Uses **MapStruct** to automatically map data between Entities and DTOs.
- **Security (`security/`)**: Contains JWT filters, custom user details services, and Spring Security configuration.
- **Exception Handling (`exception/`)**: Global exception handlers (using `@ControllerAdvice`) to standardize API error responses.
- **Scheduler (`scheduler/`)**: Contains CRON jobs for background tasks (e.g., cleaning up expired listings).
- **Websocket (`websocket/`)**: Configuration and handlers for real-time features (like chat).

---

## 🛠️ Tech Stack

### Core Frameworks
- **Java 21**: The primary programming language.
- **Spring Boot 3.5.x**: The core framework for building the application.
  - `spring-boot-starter-web`: For RESTful APIs.
  - `spring-boot-starter-data-jpa`: For ORM and database access.
  - `spring-boot-starter-security`: For authentication and authorization.
  - `spring-boot-starter-websocket`: For real-time bi-directional communication.
  - `spring-boot-starter-mail` & `spring-boot-starter-thymeleaf`: For sending templated emails.

### Database
- **MySQL 8**: The primary relational database. (`mysql-connector-j`)

### Security & Authentication
- **Spring Security**: Core security framework.
- **JJWT**: For generating and validating JSON Web Tokens (Access & Refresh tokens).
- **Google OAuth2**: Integration ready via configuration properties.

### Utilities & Tools
- **Lombok**: To reduce boilerplate code (getters, setters, constructors).
- **MapStruct**: For type-safe object mapping between Entities and DTOs.
- **Springdoc OpenAPI (Swagger)**: For auto-generating API documentation and testing UI.
- **VNPAY**: Integrated as the primary payment gateway.
- **MoceanAPI SDK**: Integrated for SMS notifications.
- **Spring Dotenv**: To load environment variables from a `.env` file easily.

---

## 🔄 Detailed Business Flow

### 1. Authentication & Authorization Flow
- **Login/Register**: Users can authenticate using a standard username/password (which generates a JWT) or via Google OAuth2.
- **Roles**: The system enforces Role-Based Access Control (RBAC). Roles include `ADMIN`, `MANAGER`, `MODERATOR`, `STAFF`, and `MEMBER`.
- **Token Validation**: Every secured API request must include a valid `Bearer Token`. The `JwtAuthenticationFilter` intercepts requests, validates the token, and sets the Security Context.

### 2. Vehicle Listing Flow
- **Creation**: A `MEMBER` creates a listing for an EV or Battery (via `ListingController` & `ProductController`).
- **Moderation**: The listing enters a pending state. A `MODERATOR` reviews the listing (approves or rejects).
- **Visibility**: Once approved, the listing becomes visible on the marketplace.
- **Auto-Cleanup**: Scheduled CRON jobs (`listing-cleanup`, `expired`) automatically manage the lifecycle of older or expired listings.

### 3. Consignment Flow (Ký gửi)
This is a complex multi-step process for users wanting the platform to sell on their behalf:
1. **Request (`ConsignmentRequestController`)**: User submits a request to consign a vehicle.
2. **Inspection (`ConsignmentInspectionController`)**: `STAFF` schedules and performs a physical/technical inspection of the vehicle.
3. **Agreement (`ConsignmentAgreementController`)**: If inspection passes, an agreement/contract is generated. Both parties agree on pricing and commission.
4. **Settlement (`ConsignmentSettlementController`)**: After a successful sale, funds are settled, taking the platform's commission into account.

### 4. Real-time Chat Flow
- **Connection**: Clients connect to the `/ws` endpoint using SockJS/STOMP.
- **Messaging**: Users can message each other directly regarding a listing. The `ChatController` handles incoming messages, saves them to the database, and broadcasts them to the specific subscriber via Spring's `SimpMessagingTemplate`.

### 5. Payment Flow (VNPAY)
- **Initiation**: User initiates a payment (e.g., purchasing a vehicle, paying a consignment fee).
- **Redirection**: The backend generates a secure VNPAY URL with a hash signature and redirects the user.
- **IPN (Instant Payment Notification)**: VNPAY calls the backend IPN URL asynchronously to confirm the transaction status securely.

---

## 🚀 Getting Started

### Prerequisites
- JDK 21
- Maven 3.8+ (or use the provided `mvnw` wrapper)
- MySQL 8.0+

### Setup Instructions

1. **Clone the repository:**
   ```bash
   git clone https://github.com/lethanhtrong161005/SWP391_Fall25_Evmarketplace.git
   cd SWP391_Fall25_Evmarketplace
   ```

2. **Database Setup:**
   Create a MySQL database named `evmarketplace` (or as configured in your env variables).

3. **Environment Configuration:**
   Copy the `.env.example` file to `.env` and fill in the necessary credentials:
   ```bash
   cp .env.example .env
   ```
   *Crucial variables include Database credentials, JWT Secret, VNPAY keys, Mail credentials, and Google OAuth keys.*

4. **Run the Application:**
   Using the Maven wrapper (with the `local` profile active by default):
   ```bash
   ./mvnw spring-boot:run
   ```
   The server will start on port `8089` (default).

5. **API Documentation:**
   Once running, access the Swagger UI to view and test all APIs:
   - `http://localhost:8089/swagger-ui.html`

### Docker Deployment
The project includes a `Dockerfile` and `docker-compose.yml` for containerized deployment, connecting to a MySQL container on the `docker` Spring profile.

```bash
docker-compose up -d --build
```

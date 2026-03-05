The User Service is responsible for managing user accounts in the e-commerce system. It handles user registration, authentication, and profile management. This service is part of the Spring Boot microservices architecture and registers with Eureka Service Discovery so that other services in the system can locate it.

The service is built using Spring Boot and uses Spring Security with JWT authentication to secure APIs. User information is stored in a MySQL database using Spring Data JPA, and Flyway is used to manage database schema migrations. Kafka is used to publish user-related events, such as when a new user is created.

The User Service exposes REST endpoints for user authentication and profile management.

Authentication endpoints include:

POST /users/register – Register a new user

POST /users/login – Authenticate a user and return a JWT token

These endpoints are implemented in the authentication controller responsible for user registration and login. 

AuthController

User profile endpoints include:

GET /users/me – Retrieve the profile of the authenticated user

PUT /users/me – Update the profile of the authenticated user

These endpoints allow users to view and update their own profile information. 

UserController

To run the User Service, ensure that Eureka Server, MySQL, and Kafka are running. The application can then be built and started using Maven.

mvn clean install
mvn spring-boot:run

By default, the service runs on port 8283, but the port and other configuration values such as database credentials, Kafka settings, and JWT secrets are externalized using environment variables so that the service can run in different environments without modifying the code.

The User Service acts as the authentication and identity component of the system and is used by other services such as the Cart Service, Order Service, and Payment Service to verify user identity through JWT tokens.

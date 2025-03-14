# Party

_The microservice is an umbrella service for the citizen and legalentity services, with the aim of simplifying for clients who need to translate between legalId and partyId for individuals or organizations._

_The service provides two resources. One for translation from individuals' and companies' partyId to their corresponding legalId, and one for translation from individuals' and companies' legalId to their corresponding partyId._

## Getting Started

### Prerequisites

- **Java 21 or higher**
- **Maven**
- **Git**
- **[Dependent Microservices](#dependencies)**

### Installation

1. **Clone the repository:**

```bash
git clone https://github.com/Sundsvallskommun/api-service-party.git
cd api-service-party
```

2. **Configure the application:**

   Before running the application, you need to set up configuration settings.
   See [Configuration](#configuration)

   **Note:** Ensure all required configurations are set; otherwise, the application may fail to start.

3. **Ensure dependent services are running:**

   If this microservice depends on other services, make sure they are up and accessible. See [Dependencies](#dependencies) for more details.

4. **Build and run the application:**

   - Using Maven:

   ```bash
   mvn spring-boot:run
   ```

   - Using Gradle:

   ```bash
   gradle bootRun
   ```

## Dependencies

This microservice depends on the following services:

- **Citizen**
  - **Purpose:** Used for translating between party id and legal id for private citizens.
- **LegalEntity**
  - **Purpose:** Used for translating between party id and legal id for organizations.

Ensure that these services are running and properly configured before starting this microservice.

## API Documentation

Access the API documentation via Swagger UI:

- **Swagger UI:** [http://localhost:8080/api-docs](http://localhost:8080/api-docs)

## Usage

### API Endpoints

See the [API Documentation](#api-documentation) for detailed information on available endpoints.

### Example Request

```bash
curl -X GET http://localhost:8080/2281/PRIVATE/81471222-5798-11e9-ae24-57fa13b361e1/legalId
```

## Configuration

Configuration is crucial for the application to run successfully. Ensure all necessary settings are configured in `application.yml`.

### Key Configuration Parameters

- **Server Port:**

```yaml
server:
  port: 8080
```

- **External Service URLs**

```yaml
integration:
  citizen:
    url: <service-url>
  legalentity:
    url: <service-url>
spring:
  security:
    oauth2:
      client:
        provider:
          citizen:
            token-uri: <token-url>
          legalentity:
            token-uri: <token-url>
      registration:
        citizen:
          client-id: <client-id>
          client-secret: <client-secret>
        legalentity:
          client-id: <client-id>
          client-secret: <client-secret>
```

### Additional Notes

- **Application Profiles:**

  Use Spring profiles (`dev`, `prod`, etc.) to manage different configurations for different environments.

- **Logging Configuration:**

  Adjust logging levels if necessary.

## Contributing

Contributions are welcome! Please see [CONTRIBUTING.md](https://github.com/Sundsvallskommun/.github/blob/main/.github/CONTRIBUTING.md) for guidelines.

## License

This project is licensed under the [MIT License](LICENSE).

## Status

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-party&metric=alert_status)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-party)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-party&metric=reliability_rating)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-party)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-party&metric=security_rating)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-party)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-party&metric=sqale_rating)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-party)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-party&metric=vulnerabilities)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-party)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-party&metric=bugs)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-party)

## 

&copy; 2023 Sundsvalls kommun

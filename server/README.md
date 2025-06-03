# Project Description

This project contains the code for the server-side (backend) of the application.

## Running Integration Tests

Integration tests with the suffix `IT` require the application to be running locally. Ensure that the application is started before executing these tests.

To start the application locally, run:

```bash
mvn spring-boot:run
```

## Setting Up Redis with Spring Boot

To configure Redis in your Spring Boot application, add the following settings to your `application.yml` file:

```yaml
spring:
  redis:
    host: localhost
    port: 6379
    password: 
    database: 0
```

Ensure that Redis is running locally on port `6379` before starting the application. You can start Redis using Docker with:

```bash
docker run --name redis -d -p 6379:6379 redis
```

* Mongo
```bash
  mongosh --password **** --username admin
```
* Redis
```bash
  redis-cli  --user default  --pass ****
```
## JWT Secret

The application now uses an HMAC secret for signing JWT tokens. Configure the
secret in `application.yml`:

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          secret: <your-very-long-secret>
```

Ensure the secret is at least 32 characters long.

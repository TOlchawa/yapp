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
## Certificates generate

````aiignore
openssl genpkey -algorithm RSA -out private.pem -pkeyopt rsa_keygen_bits:2048
openssl rsa -pubout -in private.pem -out public.pem
````
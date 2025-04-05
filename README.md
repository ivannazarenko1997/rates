# 🚀 Exchange Rate Service (Spring Boot + Virtual Threads)

A Spring Boot application using Java 21 and Virtual Threads that provides currency exchange rates fetched from [exchangerate.host](https://exchangerate.host). It includes caching, scheduled refresh, and Swagger documentation.

---

## 📌 Features

- Fetch exchange rates from external API ([exchangerate.host](https://exchangerate.host)).
- Cache exchange rates to minimize external API calls.
- Scheduled caching every 1 minute.
- API documentation and interactive testing using Swagger.

---

## 🛠 Technologies

- **Java 21+ (Virtual Threads)**
- **Spring Boot 3.2.x**
- **Spring Cache (Caffeine)**
- **Springdoc OpenAPI (Swagger UI)**
- **JUnit 5**

---

## ✅ Requirements

- Java 21 or later ([Download here](https://jdk.java.net/21/))
- Maven 3.8+ ([Download here](https://maven.apache.org/download.cgi))

Verify Java and Maven installations:

```bash
java -version
mvn -version


🚀 Running the Application
Clone the Repository
bash
Copy
Edit
git clone <your-repository-url>
cd exchange-rate-service


mvn spring-boot:run

http://localhost:8080

http://localhost:8080/swagger-ui.html


Get exchange rate (USD to EUR)

 
GET /api/rate?from=USD&to=EUR
Get all exchange rates from USD

 
GET /api/rates?from=USD
Convert 100 USD to EUR

 
GET /api/convert?from=USD&to=EUR&amount=100
Convert 100 USD to multiple currencies
 
POST /api/convert-multiple?from=USD&amount=100
Body: ["EUR", "GBP", "JPY"]
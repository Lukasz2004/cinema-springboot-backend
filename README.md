# "Absolute Cinema" Backend System
A RESTful backend application for an imaginary cinema's system for customers and employees allowing browsing the cinema schedule, reserving tickets and ticket inspection.
The app uses JWT tokens for authentication and JDBC for Oracle database connectivity.

The Oracle database and frontend applications for the customers and the employees were developed by other students and as such are not provided.
<br/><br/>
The project was developed as part of the "Databases 2" project classes at Wrocław University of Science and Technology (Politechnika Wrocławska), winter semester 2025/2026.
- Project Author: Łukasz Czerwiński
- Project Mentor: dr inż. Roman Ptak
## Key Features:
- Movie schedule and snack bar menu browsing
- Ticket reservation
- Ticket validation
- JWT-based stateless authentication with role-based authorization (Customer/Employee)
- Oracle Database integration using JDBC with support for Oracle Cloud wallets (production profile)
- ~~Included H2 Test Database (dev profile)~~ - WORK IN PROGRESS
- Full Swagger API documentation
- CLI tool for encrypting passwords using BCrypt
## Built with:
- Java, ver. 17
- Spring Boot, ver 4.0.0 (WebMVC, Security, Shell, JDBC)
- Swagger, ver 3.1
- Lombok
- Maven
## Getting Started
### Prerequisites
- Java, ver. 17
### Installation and running the App
The original project uses a live Oracle Cloud Infrastructure hosted Oracle database. Because of security concerns, the original database is not included.
Future versions will include a DEMO mode using a temporary local database.
- Install the newest .jar file from the „Releases” section of this GitHub page
- Run the packaged .jar
  - ~~DEMO mode~~ - WORK IN PROGRESS
  ```
  java -jar NAME_OF_THE_JAR_FILE.jar --spring.profiles.active=dev
  ```
  - Production use with Oracle Cloud Infrastructure using wallets (place wallet files in /wallet folder in the same folder as jar) 
  ```
  java -jar NAME_OF_THE_JAR_FILE.jar --spring.profiles.active=prod --spring.datasource.username=YOUR_DB_USERNAME --spring.datasource.password=YOUR_PASSWORD --application.security.jwt.secret-key=YOUR_SECRET_KEY
  ```
- Then head to Swagger to browse available endpoints and documentation
```
localhost:8080/swagger-ui/index.html#/
```
- To use the included BCrypt password generator type the following command into running program's console:
```
szyfruj PASSWORD_TO_ENCRYPT
```
  
## Current plans for the project:
- DEMO mode Spring profile allowing test database access
- English localization of the app's Swagger documentation

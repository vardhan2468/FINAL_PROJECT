# Learning Management System (LMS)

## Project Overview
A comprehensive web-based Learning Management System developed using Spring Boot, MySQL, and REST APIs for final-year college project.

## Technology Stack
- **Backend Framework:** Spring Boot 3.4.1
- **Language:** Java 17
- **Database:** MySQL
- **Build Tool:** Maven
- **API Architecture:** RESTful APIs

## Dependencies
- Spring Web
- Spring Data JPA
- Spring Security
- MySQL Driver

## Project Structure
```
lms/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── learnsphere/
│   │   │           └── lms/
│   │   │               └── LmsApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
│           └── com/
│               └── learnsphere/
│                   └── lms/
│                       └── LmsApplicationTests.java
├── pom.xml
└── README.md
```

## Getting Started

### Prerequisites
- Java 17 or higher
- MySQL Server
- Maven 3.6+

### Database Setup
1. Install and start MySQL server
2. Create a database named `lms_db` (or the application will create it automatically)
3. Update database credentials in `application.properties` if needed

### Running the Application
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Building the Project
```bash
mvn clean install
```

## Configuration
Database and application settings can be configured in:
```
src/main/resources/application.properties
```

## User Roles
- **Administrator:** System management and configuration
- **Instructor:** Course creation and student management
- **Student:** Access courses and submit assignments

## Project Information
- **Group ID:** com.learnsphere
- **Artifact ID:** lms
- **Version:** 0.0.1-SNAPSHOT

## Authors
Final Year Project Team

## Date
December 13, 2025

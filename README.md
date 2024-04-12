# BallSquad API

## Overview
BallSquad API is a Spring Boot application that utilizes MySQL for database and Hibernate for ORM. This application is designed to manage and provide data related to the authors and their works, ensuring efficient data handling and integrity.

## Prerequisites
Before you can run the application, make sure you have the following installed:
- **Java JDK 11** or higher
- **Maven 3.6** or higher for building the application
- **MySQL Server** running on your local machine or a remote server

## Configuration
Update the `application.properties` file located under `src/main/resources` with your MySQL database credentials:

    ```properties
    spring.datasource.password="Your Password"
    ```

Replace "Your Password" with your actual MySQL database password.

## Building The Project
To build the project, run the following command in the terminal from the root directory of the project:

    ```bash
    mvn clean install
    ```

This command compiles the project and runs any tests, creating a build artifact.

## Running the Application
To start the application, use the following Maven command:

    ```bash
    mvn spring-boot:run
    ```

This will launch the application on the default port (8080). You can access the API via `http://localhost:8080/`.

## Testing the Application
You can test the application's endpoints using tools like Postman or by executing curl commands. For example:

    ```bash
    curl http://localhost:8080/getAuthor?name=AuthorName
    curl http://localhost:8080/getWorks?authorId=AuthorId
    ```

## Important Note
Ensure your MySQL server is running before you start the application.
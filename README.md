# HBV501G - Team20

As the project for HBV501G - Software Project 1, using a plan-driven development, more especially a Unified Process Structure 
with 4 phases: Inception, Elaboration, Construction and Transition.

Here we present you 

<img width="281" alt="EMI" src="https://github.com/user-attachments/assets/97395c3c-eee5-4019-90e5-d35db765e06b">

# EMI - Educational Meeting and Motivational Interface

A platform to record study sessions, keep track of the duration of your sessions, 
even as specific as the topic and your frequency of studying. 
You can also see your favourite locations within the
University, and the number of people studying at them.
Using Emi, you can add your comments, thoughts, and more to each individual study activity,
your description and even photos.

People can interact with others' study activities by giving coffee, 
join study groups and interact with members through posts.


# Team members:
- Brynjar Steinn Traustasson (140296-2529) bst4@hi.is
- Catarina M. S. Lima (040499-3269) cms5@hi.is
- Luiza V Sampaio Ramos (280291-5449) lvs2@hi.is
- Steinunn María Bergþórsdóttir (180501-2230) smb23@hi.is

# Installation Details:
- Maven model 3.8 or higher.
- Spring boot framework version 3.3.4
- Java version 17 or higher.
- PostgreSQL version 13 or higher.
- Thymeleaf utilized to render the html pages.
- A postgresql database must be utilized in order for the application to run locally:
  - Create database by doing:
    ```sql
    CREATE DATABASE emidb;
    ```
  - In the application.properties file add the following (you may replace username and password with that you use in your system):
    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/emidb
    spring.datasource.username=root
    spring.datasource.password=password
    spring.datasource.driver-class-name=org.postgresql.Driver
    spring.jpa.hibernate.ddl-auto=update
    spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
    ```
  - Build the project using Maven:
  ```bash
  mvn clean install
  ```
  - Run the application:
  ```bash
  mvn spring-boot:run
  ```
  - Upon running the application should become available through: [http://localhost:8080](http://localhost:8080)

# UML Diagrams:

## State Machine Diagram
![EMI-State_Machine_Diagram](https://github.com/user-attachments/assets/404439d8-5139-45ba-8b59-d03b2e921c6a)


## Sequence Diagram
![EMI-Sequence_Diagram](https://github.com/user-attachments/assets/06f24d98-4094-4031-9ce8-43ddda7dfbec)

## Class Diagram
![EMI-Class_Diagram](https://github.com/user-attachments/assets/f4a298c7-4900-4e3e-92e4-479f9a7a0e60)

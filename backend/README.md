# How to Run the Backend Locally

Follow these steps to set up and run the backend application locally:

---

## Prerequisites

Ensure the following are installed on your system:

1. **Java Development Kit (JDK)** (version 8 or later)
2. **Apache Maven** (version 3.6.0 or later)

---

## Steps to Run

1. **Build the Project**  
   Open a terminal, navigate to the project's root directory, and run:
   ```bash
   mvn clean install -DskipTests
   ```

   This command compiles the code, packages the application, and skips running the tests.

2. **Start the Application**  
   Run the following command to start the backend server:
   ```bash
   mvn spring-boot:run
   ```

3. **Access the Backend**  
   Once the server starts, the backend will be available at:
   ```
   http://localhost:8080
   ```

---

That's it! The backend should now be running locally.
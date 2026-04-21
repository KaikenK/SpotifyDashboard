# Spotify Artist Dashboard

A full-stack, highly dynamic web application replicating the Spotify Artist Dashboard. Built with Java Spring Boot and React.

## System Architecture
- **Backend:** Java 17+, Spring Boot 3.x, Spring Security (JWT), Spring Data JPA.
- **Frontend:** React 18, TailwindCSS, Recharts, Framer Motion, Axios.
- **Database:** Relational DB (H2/MySQL/PostgreSQL) via Spring Data JPA.

## Prerequisites
- [Java 17+](https://adoptium.net/)
- [Maven 3.8+](https://maven.apache.org/)
- [Node.js 18+](https://nodejs.org/) & npm

## 🚀 Getting Started

### 1. Running the Java Spring Boot Backend
The backend utilizes Maven wrapper, ensuring you don't need a local Maven installation.

\`\`\`bash
# 1. Navigate to the backend directory
cd java-backend

# 2. Configure the database (Optional)
# The app looks for src/main/resources/application.properties or application.yml
# Ensure your database URL, username, password, and JWT secret are set.

# 3. Clean, compile, and run the application
./mvnw clean spring-boot:run
\`\`\`
*The REST API will be available on `http://localhost:8080`.*

### 2. Running the React Frontend
The frontend requires your backend API URL to be configured via environment variables.

\`\`\`bash
# 1. Navigate to the frontend directory
cd frontend

# 2. Install dependencies
npm install

# 3. Configure Environment Variables
# Create a .env file based on the environment configuration
echo "REACT_APP_BACKEND_URL=http://localhost:8080" > .env

# 4. Start the development server
npm start
\`\`\`
*The React application will open in your browser at `http://localhost:3000`.*

## 🏗️ Object-Oriented Design Implementation
- **Layered Architecture:** Strict separation between `Controller`, `Service`, and `Repository` layers.
- **Dependency Injection:** Utilizing Spring's IoC wrapper to enforce loose coupling.
- **Custom React Hooks:** Encapsulation of stateful API logic (e.g., `useArtistData`, `useAdminData`).

## 🛡️ Security
- **Stateless Authentication:** JWT-based stateless authentication using custom Spring Security Filter Chains.
- **Role-Based Routing:** Protected React routes utilizing React Context API (`AuthContext`) enforcing `ARTIST`, `FAN`, and `ADMIN` boundaries.
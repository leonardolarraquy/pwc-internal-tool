# PWC Admin System

A comprehensive employee and resource management system built with Spring Boot and React.

## Features

- **User Authentication**: JWT-based authentication with role-based access control
- **User Management**: Complete CRUD operations for employees
- **CSV Import**: Bulk import employees from CSV files (supports 10,000+ records)
- **Advanced Pagination**: Configurable page sizes (10, 50, 100 records per page)
- **Column Sorting**: Sort by any column in ascending or descending order
- **Role-Based Access**: Admin and User roles with different permissions
- **Modern UI**: Built with React, shadcn/ui components, and Tailwind CSS

## Technology Stack

### Backend
- Spring Boot 3.2.0
- Spring Security with JWT
- Spring Data JPA
- H2 Database (file-based, persistent)
- Maven

### Frontend
- React 18
- Vite
- shadcn/ui components
- Tailwind CSS
- React Router

## Prerequisites

- Java 17 or higher
- Node.js 18 or higher
- Maven 3.6 or higher
- Docker (optional, for containerized deployment)

## Default Admin Credentials

- **Email**: admin@pwc.com
- **Password**: admin123

⚠️ **Important**: Change the default admin password after first login!

## Local Development

### Backend Setup

1. Navigate to the project root:
```bash
cd /path/to/pwc
```

2. Build and run the Spring Boot application:
```bash
mvn spring-boot:run
```

The backend will be available at `http://localhost:8080`

### Frontend Setup

1. Navigate to the frontend directory:
```bash
cd frontend
```

2. Install dependencies:
```bash
npm install
```

3. Start the development server:
```bash
npm run dev
```

The frontend will be available at `http://localhost:5173`

4. Build for production:
```bash
npm run build
```
Note: The build outputs to `frontend/dist/`. To integrate with Spring Boot, copy the contents to `src/main/resources/static/` or use the Docker build which handles this automatically.

## Docker Deployment

### Build Docker Image

```bash
docker build -t pwc-admin .
```

### Run with Docker

```bash
docker run -d \
  -p 8080:8080 \
  -v pwc-data:/app/data \
  --name pwc-admin \
  pwc-admin
```

The `-v pwc-data:/app/data` flag ensures the H2 database persists even when the container is stopped or removed.

### Access the Application

- Application: http://localhost:8080
- H2 Console: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:file:./data/pwc-db`
  - Username: `sa`
  - Password: (leave empty)

## Database

The application uses H2 file-based database. The database file is stored in the `data/` directory:

- **Local**: `./data/pwc-db.mv.db`
- **Docker**: `/app/data/pwc-db.mv.db` (mounted as volume)

## API Endpoints

### Authentication
- `POST /api/auth/login` - Login user

### Users (Admin only)
- `GET /api/users` - Get all users (with pagination, sorting, search)
- `GET /api/users/{id}` - Get user by ID
- `POST /api/users` - Create new user
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user
- `POST /api/users/import` - Import users from CSV

### Query Parameters for GET /api/users
- `page` - Page number (default: 0)
- `size` - Page size (default: 100)
- `sortBy` - Column to sort by (default: id)
- `sortDir` - Sort direction: asc or desc (default: asc)
- `search` - Search term (searches in firstName, lastName, email, employeeId)

## CSV Import Format

The CSV file should contain the following columns (case-insensitive):

**Required columns:**
- `email`
- `employeeId`
- `firstName`
- `lastName`

**Optional columns:**
- `positionId`
- `positionTitle`
- `password` (if not provided, default password will be used)
- `role` (USER or ADMIN, defaults to USER if not provided)

Example CSV:
```csv
email,employeeId,firstName,lastName,positionId,positionTitle,password,role
john.doe@pwc.com,EMP001,John,Doe,POS001,Manager,SecurePass123,USER
jane.smith@pwc.com,EMP002,Jane,Smith,POS002,Director,SecurePass123,ADMIN
```

## Project Structure

```
pwc/
├── src/
│   └── main/
│       ├── java/com/pwc/
│       │   ├── config/          # Configuration classes
│       │   ├── controller/       # REST controllers
│       │   ├── dto/              # Data Transfer Objects
│       │   ├── model/            # Entity models
│       │   ├── repository/       # JPA repositories
│       │   ├── security/         # Security configuration
│       │   ├── service/          # Business logic
│       │   └── util/             # Utility classes
│       └── resources/
│           ├── application.yml   # Application configuration
│           └── static/           # Frontend build output
├── frontend/
│   ├── src/
│   │   ├── components/           # React components
│   │   ├── contexts/             # React contexts
│   │   ├── pages/                # Page components
│   │   ├── services/             # API services
│   │   └── lib/                  # Utility functions
│   ├── package.json
│   └── vite.config.js
├── data/                         # H2 database files (gitignored)
├── Dockerfile
├── pom.xml
└── README.md
```

## Security

- JWT tokens are used for authentication
- Passwords are encrypted using BCrypt
- Role-based access control (RBAC) implemented
- CORS enabled for development

## License

This project is proprietary software developed for PWC.


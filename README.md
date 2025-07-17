# Carhub - Car Dealership Management System

A comprehensive web application for managing a car dealership with modern technologies and best practices.

## Technology Stack

### Backend
- **Java 17** with **Spring Boot 3.2.x**
- **Spring Security** with JWT authentication
- **Spring Data JPA** with PostgreSQL
- **iText** for PDF generation
- **JavaMail API** for email notifications

### Frontend
- **Vue.js 3** with Composition API
- **Tailwind CSS** for styling
- **Chart.js** for analytics
- **Axios** for HTTP requests

### Database
- **PostgreSQL** with pgAdmin 4

## Features

### 🚗 Car Management
- Complete CRUD operations for vehicles
- Advanced filtering and search
- Image upload and management
- Status tracking (Available, Sold, Reserved, Maintenance)
- Bulk operations

### 📊 Dashboard Analytics
- Real-time statistics and KPIs
- Monthly sales charts
- Revenue tracking
- Brand performance analysis
- Recent activities feed

### 💰 Sales Processing
- Multi-step purchase workflow
- Client management integration
- Payment processing
- PDF invoice generation
- Email notifications

### 👥 Client Management
- Customer database
- Purchase history
- Contact information management
- Search and filtering

### 🎨 Modern UI/UX
- Dark/Light mode toggle
- Responsive design (mobile-first)
- Orange and black theme
- Smooth animations and transitions
- Loading states and error handling

## Setup Instructions

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+
- pgAdmin 4 (optional, for database management)
- Node.js (for frontend development, optional)

### Database Setup

1. **Install PostgreSQL and pgAdmin 4**
   \`\`\`bash
   # On Ubuntu/Debian
   sudo apt update
   sudo apt install postgresql postgresql-contrib
   
   # On macOS with Homebrew
   brew install postgresql
   
   # On Windows, download from https://www.postgresql.org/download/windows/
   \`\`\`

2. **Start PostgreSQL service**
   \`\`\`bash
   # On Linux
   sudo systemctl start postgresql
   sudo systemctl enable postgresql
   
   # On macOS
   brew services start postgresql
   
   # On Windows, it should start automatically
   \`\`\`

3. **Create database and user**
   \`\`\`bash
   sudo -u postgres psql
   \`\`\`
   
   In PostgreSQL shell:
   \`\`\`sql
   CREATE DATABASE carhub_db;
   CREATE USER carhub_user WITH PASSWORD 'your_password';
   GRANT ALL PRIVILEGES ON DATABASE carhub_db TO carhub_user;
   \q
   \`\`\`

4. **Run the initialization script**
   \`\`\`bash
   psql -U carhub_user -d carhub_db -f scripts/init-database.sql
   \`\`\`

### Backend Setup

1. **Clone and navigate to project**
   \`\`\`bash
   git clone <repository-url>
   cd carhub-dealership
   \`\`\`

2. **Configure application.properties**
   ```properties
   # Update database credentials
   spring.datasource.url=jdbc:postgresql://localhost:5432/carhub_db
   spring.datasource.username=carhub_user
   spring.datasource.password=your_password
   
   # Update email configuration (optional)
   spring.mail.username=your-email@gmail.com
   spring.mail.password=your-app-password
   \`\`\`

3. **Build and run the application**
   \`\`\`bash
   # Clean and compile
   mvn clean compile
   
   # Run tests
   mvn test
   
   # Package the application
   mvn clean package
   
   # Run the application
   mvn spring-boot:run
   
   # Or run the JAR file
   java -jar target/carhub-dealership-0.0.1-SNAPSHOT.jar
   \`\`\`

### Frontend Setup

The frontend is included in the `frontend/index.html` file and can be served directly or through a web server.

1. **For development, you can use a simple HTTP server:**
   \`\`\`bash
   # Using Python
   cd frontend
   python -m http.server 3000
   
   # Using Node.js
   npx http-server frontend -p 3000
   
   # Using PHP
   cd frontend
   php -S localhost:3000
   \`\`\`

2. **For production, serve through a web server like Nginx or Apache**

### Access

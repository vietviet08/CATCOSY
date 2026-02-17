# 🐱 CATCOSY - E-commerce Fashion Platform

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen)](https://github.com/vietviet08/CATCOSY)
[![Java Version](https://img.shields.io/badge/Java-21-orange)](https://openjdk.java.net/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.4-green)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)](https://www.mysql.com/)
[![Docker](https://img.shields.io/badge/Docker-Compose-blue)](https://docs.docker.com/compose/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

> A modern, full-stack e-commerce platform specializing in fashion apparel with separate admin and customer interfaces.

## 🚀 Quick Start

```bash
# 1. Clone the repository
git clone https://github.com/vietviet08/CATCOSY.git
cd CATCOSY

# 2. Start with Docker (Recommended)
docker-compose up -d

# 3. Wait for services to start (30-60 seconds)
docker-compose ps

# 4. Access the applications
# Customer Portal: http://localhost:8086
# Admin Dashboard: http://localhost:8085/admin
```

That's it! The application is now running with sample data. 🎉

---

## 📖 Table of Contents

-   [Description](#-description)
-   [Features](#-features)
-   [Tech Stack](#-tech-stack)
-   [Project Structure](#-project-structure)
-   [Prerequisites](#-prerequisites)
-   [Installation & Setup](#-installation--setup)
-   [Usage](#-usage)
-   [Configuration](#-configuration)
-   [API Documentation](#-api-documentation)
-   [Testing](#-testing)
-   [Deployment](#-deployment)
-   [Contributing](#-contributing)
-   [License](#-license)
-   [Authors](#-authors)
-   [Acknowledgments](#-acknowledgments)

## 🎯 Description

CATCOSY is a comprehensive e-commerce platform designed for fashion retail. The platform features a microservices-based architecture with separate modules for admin management, customer interface, and shared library components. Built with Spring Boot 3.2.4 and Java 21, it provides a scalable and modern solution for online fashion retail businesses.

The project follows best practices with a modular design, containerized deployment using Docker, and RESTful API architecture. It's designed to handle high traffic loads and provides a seamless shopping experience for customers while offering powerful management tools for administrators.

### Key Objectives:

-   🛍️ Provide seamless shopping experience for customers
-   🔧 Offer comprehensive admin tools for inventory and order management
-   📱 Ensure responsive design across all devices
-   🔒 Implement secure authentication and authorization
-   📊 Enable real-time analytics and reporting

## ✨ Features

### Customer Features

-   🏠 **Homepage**: Product showcases, new arrivals, sales items
-   🔍 **Product Catalog**: Advanced filtering by category, brand, size, price
-   🛒 **Shopping Cart**: Add/remove items, quantity management
-   👤 **User Account**: Profile management, order history, address book
-   💳 **Checkout**: Secure payment processing
-   📱 **Responsive Design**: Mobile-first approach
-   🔐 **OAuth2 Integration**: Social login support

### Admin Features

-   📊 **Dashboard**: Real-time statistics and analytics
-   📦 **Product Management**: CRUD operations, image uploads, inventory tracking
-   👥 **Customer Management**: User accounts, order history
-   🏢 **Employee Management**: Role-based access control
-   📋 **Order Management**: Order processing, status updates
-   📈 **Reports**: Sales analytics, export functionality
-   🏷️ **Category & Brand Management**: Organize product catalog

### Planned Features

-   🔔 **Push Notifications**: Real-time order updates
-   🤖 **AI Recommendations**: Personalized product suggestions
-   💬 **Live Chat Support**: Customer service integration
-   📱 **Mobile App**: Native iOS/Android applications

## 🛠️ Tech Stack

### Backend

-   **Java 21** - Programming language
-   **Spring Boot 3.2.4** - Application framework
-   **Spring Security** - Authentication and authorization
-   **Spring Data JPA** - Data persistence
-   **MySQL 8.0** - Primary database
-   **Maven** - Dependency management
-   **Thymeleaf** - Server-side templating

### Frontend

-   **HTML5 & CSS3** - Markup and styling
-   **JavaScript** - Client-side scripting
-   **Bootstrap** - UI framework
-   **jQuery** - DOM manipulation

### DevOps & Deployment

-   **Docker & Docker Compose** - Containerization
-   **AWS S3** - File storage
-   **Maven** - Build automation

## 📁 Project Structure

```
CATCOSY/
├── admin/                      # Admin module
│   ├── src/main/java/         # Admin backend code
│   │   └── com/catcosy/admin/ # Admin package
│   ├── src/main/resources/    # Admin resources
│   │   ├── static/            # CSS, JS, images
│   │   ├── templates/         # Thymeleaf templates
│   │   └── application.properties
│   ├── pom.xml                # Admin dependencies
│   └── Dockerfile             # Admin container config
├── customer/                   # Customer module
│   ├── src/main/java/         # Customer backend code
│   │   └── com/catcosy/customer/
│   ├── src/main/resources/    # Customer resources
│   │   ├── static/            # CSS, JS, images
│   │   ├── templates/         # Thymeleaf templates
│   │   └── application.properties
│   ├── pom.xml                # Customer dependencies
│   └── Dockerfile             # Customer container config
├── library/                    # Shared library
│   ├── src/main/java/         # Common entities, services, repositories
│   │   └── com/catcosy/library/
│   ├── src/main/resources/    # Shared resources
│   ├── pom.xml                # Library dependencies
│   └── Dockerfile             # Library container config
├── mysql-init/                 # Database initialization
│   └── latest/                # SQL initialization scripts
│       └── init2.sql          # Database schema and seed data
├── docker-compose.yml          # Multi-container orchestration
├── pom.xml                    # Root Maven configuration
├── .gitignore                 # Git ignore rules
└── README.md                  # Project documentation
```

### Module Responsibilities

-   **Library Module**: Shared entities, repositories, services, DTOs, and common utilities used by both admin and customer modules
-   **Admin Module**: Administrative interface with authentication, product management, order processing, and analytics
-   **Customer Module**: Customer-facing interface with product browsing, shopping cart, checkout, and user account management

### Database Schema

The database is automatically initialized with the schema defined in `mysql-init/latest/init2.sql`. The schema includes tables for:

-   **Users & Authentication**: User accounts, roles, permissions
-   **Products**: Product catalog, categories, brands, inventory
-   **Orders**: Order management, order items, shipping information
-   **Shopping Cart**: Cart items, user sessions
-   **Media**: Product images, file uploads

## 📋 Prerequisites

Before running this project, ensure you have:

-   **Java 21** or higher
-   **Maven 3.8+**
-   **Docker & Docker Compose**
-   **Git**
-   **4GB+ RAM** (for Docker containers)

### System Requirements

| Component | Minimum             | Recommended |
| --------- | ------------------- | ----------- |
| Java      | JDK 21              | JDK 21+     |
| RAM       | 4GB                 | 8GB+        |
| Storage   | 2GB                 | 5GB+        |
| OS        | Linux/Windows/macOS | Linux       |

## 🚀 Installation & Setup

### 1. Clone the Repository

```bash
git clone https://github.com/vietviet08/CATCOSY.git
cd CATCOSY
```

### 2. Environment Configuration

Create a `.env` file in the root directory (required for Docker setup):

```bash
# Database Configuration
MYSQL_ROOT_PASSWORD=root
MYSQL_DATABASE=catcosy
MYSQL_USER=catcosy
MYSQL_PASSWORD=root

# Application Configuration
SPRING_PROFILES_ACTIVE=default
SERVER_PORT_ADMIN=8085
SERVER_PORT_CUSTOMER=8086
SERVER_PORT_LIBRARY=8084

# AWS S3 Configuration (Optional - for file uploads)
AWS_ACCESS_KEY_ID=your_access_key
AWS_SECRET_ACCESS_KEY=your_secret_key
AWS_S3_BUCKET_NAME=your_bucket_name
AWS_S3_REGION=your_region

# Email Configuration (Optional - for notifications)
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=your_email@gmail.com
SPRING_MAIL_PASSWORD=your_app_password
```

**Note**: The `.env` file is optional for Docker setup as default values are configured in `docker-compose.yml`. However, it's **strongly recommended** for production deployments to:
- Override default credentials (current defaults are **NOT secure for production**)
- Configure custom database passwords
- Add your own AWS S3 credentials for file uploads
- Set up email service credentials

⚠️ **Security Warning**: Never use default credentials in production or public deployments. Always change all passwords and secrets before deploying to any environment accessible from the internet.

### 3. Build and Run with Docker

#### Quick Start (Recommended)

```bash
# Build and start all services
docker-compose up -d

# Wait for services to be healthy (30-60 seconds)
docker-compose ps

# View logs
docker-compose logs -f

# View logs for specific service
docker-compose logs -f admin
docker-compose logs -f customer
docker-compose logs -f library

# Stop services
docker-compose down

# Stop and remove all data
docker-compose down -v
```

#### Verify Services are Running

```bash
# Check all containers are up
docker-compose ps

# Test each service endpoint
curl http://localhost:8085/admin/
curl http://localhost:8086/
curl http://localhost:8084/

# Check database connection
docker exec -it catcosy-mysql mysql -uroot -proot -e "SHOW DATABASES;"
```

### 4. Manual Build (Alternative)

```bash
# Build the project
mvn clean install

# Run individual modules
cd library && mvn spring-boot:run &
cd admin && mvn spring-boot:run &
cd customer && mvn spring-boot:run &
```

## 🎮 Usage

### Screenshots

<div align="center">

> 📸 **Coming Soon**: Screenshots of the application will be added here to showcase the customer interface, admin dashboard, and key features.

</div>

### Access the Application

| Service         | URL                   | Description              |
| --------------- | --------------------- | ------------------------ |
| Customer Portal | http://localhost:8086 | Main shopping interface  |
| Admin Dashboard | http://localhost:8085 | Administrative interface |
| Library Service | http://localhost:8084 | Shared services API      |
| MySQL Database  | localhost:3306        | Database access          |

### Default Login Credentials

⚠️ **Security Note**: These are default credentials for development only. Change them immediately for any production or publicly accessible deployment.

**Admin Access:**

```
URL: http://localhost:8085/admin/login
Username: admin
Password: Admin@admin_909!
```

**Test Customer:**

```
URL: http://localhost:8086/login
Username: customer
Password: customer123
```

> 💡 **Tip**: You can register new customer accounts at http://localhost:8086/register

### Sample API Endpoints

```bash
# Customer homepage
GET http://localhost:8086/

# Get all products (Customer)
GET http://localhost:8086/products

# Admin dashboard stats
GET http://localhost:8085/admin/index

# Product management (Admin)
POST http://localhost:8085/admin/save-product
```

## ⚙️ Configuration

### Troubleshooting

#### Common Issues and Solutions

**Issue: Port already in use**
```bash
# Check what's using the port
lsof -i :8085  # or :8086, :8084, :3306

# Solution 1: Stop the process using the port
# Solution 2: Change ports in docker-compose.yml
```

**Issue: Database connection failed**
```bash
# Check MySQL is running
docker-compose ps mysql

# Check MySQL logs
docker-compose logs mysql

# Restart MySQL service
docker-compose restart mysql

# Wait for MySQL to be healthy
docker-compose up -d mysql
```

**Issue: Services not starting**
```bash
# Remove old containers and volumes
docker-compose down -v

# Rebuild images
docker-compose build --no-cache

# Start fresh
docker-compose up -d
```

**Issue: Application throws errors on startup**
```bash
# Check application logs
docker-compose logs -f admin
docker-compose logs -f customer

# Verify environment variables
docker-compose config

# Check if .env file exists and is properly configured
cat .env
```

## ⚙️ Configuration

### Application Properties

**Admin Module** (`admin/src/main/resources/application.properties`):

```properties
server.port=8085
server.servlet.context-path=/admin    # All admin URLs will be prefixed with /admin
spring.datasource.url=jdbc:mysql://localhost:3306/catcosy
spring.jpa.hibernate.ddl-auto=update
spring.profiles.active=default
```

> **Note**: The `/admin` context path is already configured in the actual application. This means all admin endpoints must be accessed with the `/admin` prefix (e.g., `http://localhost:8085/admin/`).

**Customer Module** (`customer/src/main/resources/application.properties`):

```properties
server.port=8086
spring.datasource.url=jdbc:mysql://localhost:3306/catcosy
spring.jpa.hibernate.ddl-auto=update
spring.profiles.active=default
```

### Important Environment Variables

| Variable                     | Description                | Default                           |
| ---------------------------- | -------------------------- | --------------------------------- |
| `SPRING_DATASOURCE_URL`      | Database connection string | `jdbc:mysql://mysql:3306/catcosy` |
| `SPRING_DATASOURCE_USERNAME` | Database username          | `root`                            |
| `SPRING_DATASOURCE_PASSWORD` | Database password          | `root`                            |
| `SPRING_PROFILES_ACTIVE`     | Active Spring profile      | `default`                         |

## 📚 API Documentation

### Core Endpoints

#### Customer API

-   `GET /` - Homepage
-   `GET /products` - Product listing with filters
-   `GET /product-detail/{id}` - Product details
-   `POST /add-to-cart` - Add product to cart
-   `GET /cart` - View shopping cart
-   `GET /account` - User account management
-   `POST /checkout` - Process order
-   `GET /order-history` - View order history

#### Admin API

-   `GET /admin/index` - Dashboard statistics
-   `GET /admin/products` - Product management
-   `POST /admin/save-product` - Create/update product
-   `DELETE /admin/delete-product/{id}` - Delete product
-   `GET /admin/orders` - Order management
-   `PUT /admin/update-order-status` - Update order status
-   `GET /admin/customers` - Customer management
-   `GET /admin/employees` - Employee management

#### Authentication

-   `POST /login` - User authentication
-   `POST /register` - User registration
-   `GET /logout` - User logout

### Response Format

```json
{
    "status": "success|error",
    "message": "Description",
    "data": {},
    "timestamp": "2024-01-01T00:00:00Z"
}
```

## 🧪 Testing

### Run Unit Tests

```bash
# Run all tests
mvn test

# Run tests for specific module
cd admin && mvn test
cd customer && mvn test
cd library && mvn test

# Run with coverage
mvn test jacoco:report
```

### Test Coverage

Current test coverage targets:

-   Unit Tests: 70%+
-   Integration Tests: 60%+
-   End-to-End Tests: 50%+

### Testing Strategy

-   **Unit Tests**: Service layer, repository layer
-   **Integration Tests**: API endpoints, database operations
-   **UI Tests**: Selenium for critical user flows

## 🚀 Deployment

### Docker Deployment

```bash
# Production build
docker-compose -f docker-compose.prod.yml up -d

# Scale services
docker-compose up -d --scale customer=3 --scale admin=2
```

### CI/CD Pipeline

```yaml
# .github/workflows/deploy.yml
name: Deploy to Production
on:
    push:
        branches: [main]
jobs:
    deploy:
        runs-on: ubuntu-latest
        steps:
            - uses: actions/checkout@v2
            - name: Build and Deploy
              run: |
                  mvn clean package
                  docker build -t catcosy:latest .
                  docker push registry/catcosy:latest
```

### Production Environment

#### AWS Deployment

-   **ECS/EKS**: Container orchestration
-   **RDS MySQL**: Managed database
-   **S3**: Static file storage
-   **CloudFront**: CDN for static assets
-   **ALB**: Load balancing

#### Performance Optimization

-   Enable JPA 2nd level cache
-   Implement Redis for session management
-   Use CDN for static resources
-   Database connection pooling

## 🤝 Contributing

We welcome contributions! Please see our [Contributing Guide](CONTRIBUTING.md) for details.

### Development Workflow

1. **Fork** the repository
2. **Create** a feature branch (`git checkout -b feature/amazing-feature`)
3. **Commit** your changes (`git commit -m 'Add amazing feature'`)
4. **Push** to the branch (`git push origin feature/amazing-feature`)
5. **Open** a Pull Request

### Coding Standards

-   Follow **Java Naming Conventions**
-   Use **Spring Boot Best Practices**
-   Write **comprehensive tests**
-   Document **public APIs**
-   Follow **RESTful principles**
-   Implement **proper error handling**
-   Use **meaningful commit messages**

### Security Best Practices

-   Never commit sensitive data (passwords, API keys) to the repository
-   Use environment variables for configuration
-   Always validate user input
-   Implement proper authentication and authorization
-   Keep dependencies up to date
-   Follow OWASP security guidelines

### Code Review Process

1. All changes require **pull request review**
2. Automated tests must **pass**
3. Code coverage should not **decrease**
4. Follow **conventional commits**

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

```
MIT License

Copyright (c) 2024 CATCOSY Team

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.
```

## 👥 Authors

### Core Team

-   **Viet Quoc** - _Lead Developer_ - [@vietviet08](https://github.com/vietviet08)
    -   📧 Email: vietquoc2408@gmail.com
    -   🌐 LinkedIn: [Your LinkedIn](https://linkedin.com/in/vietquoc)

### Contributors

We appreciate all contributors who have helped improve this project. See the full list of [contributors](https://github.com/vietviet08/CATCOSY/contributors).

## 🙏 Acknowledgments

### Frameworks & Libraries

-   [Spring Boot](https://spring.io/projects/spring-boot) - Application framework
-   [Spring Security](https://spring.io/projects/spring-security) - Security framework
-   [Spring Data JPA](https://spring.io/projects/spring-data-jpa) - Data access layer
-   [Thymeleaf](https://www.thymeleaf.org/) - Templating engine
-   [Bootstrap](https://getbootstrap.com/) - UI framework
-   [MySQL](https://www.mysql.com/) - Database management
-   [Docker](https://www.docker.com/) - Containerization platform

### Tools & Services

-   [AWS S3](https://aws.amazon.com/s3/) - Cloud storage
-   [Maven](https://maven.apache.org/) - Build automation
-   [IntelliJ IDEA](https://www.jetbrains.com/idea/) - Development environment
-   [Postman](https://www.postman.com/) - API testing

### Inspiration & References

-   [Spring Petclinic](https://github.com/spring-projects/spring-petclinic) - Project structure inspiration
-   [E-commerce Design Patterns](https://microservices.io/patterns/) - Architecture patterns
-   [Spring Boot Best Practices](https://spring.io/guides) - Implementation guidelines

## ❓ FAQ

**Q: Can I use this project for commercial purposes?**  
A: Yes, this project is licensed under the MIT License, which allows commercial use.

**Q: How do I change the default ports?**  
A: Edit the port mappings in `docker-compose.yml` or modify the `server.port` in each module's `application.properties`.

**Q: How do I add custom products?**  
A: Log in to the admin dashboard at http://localhost:8085/admin/login using the credentials `admin / Admin@admin_909!` (default development credentials) and navigate to the Products section.

**Q: Can I use a different database?**  
A: Yes, you can configure Spring Boot to use PostgreSQL, H2, or other databases by updating the dependencies and datasource configuration.

**Q: How do I enable AWS S3 for file uploads?**  
A: Add your AWS credentials to the `.env` file and ensure the S3 configuration is properly set in the application properties.

**Q: Is there a production deployment guide?**  
A: See the [Deployment](#-deployment) section for Docker deployment and CI/CD pipeline configuration.

**Q: How do I contribute to this project?**  
A: Please read the [Contributing](#-contributing) section for guidelines on submitting pull requests.

---

<div align="center">

**[⬆ Back to Top](#-catcosy---e-commerce-fashion-platform)**

Made with ❤️ by the CATCOSY Team

[🐱 Visit our website](https://catcosy.com) • [📧 Contact us](mailto:contact@catcosy.com) • [🐛 Report issues](https://github.com/vietviet08/CATCOSY/issues)

</div>

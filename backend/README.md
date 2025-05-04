# 🐾 Pawlly Mobile - Backend Implementation Guide

## Architecture Overview
The backend follows a three-tier architecture:
- Mobile App (Kotlin/Compose) → Spring Boot Backend → Supabase (Storage)

## Tech Stack
- **Framework:** Spring Boot 3.2.10
- **Database:** Supabase (PostgreSQL) for storage
- **Authentication:** Spring Security with JWT
- **Storage:** Supabase Storage
- **Build Tool:** Maven
- **Java Version:** 17

## Project Structure
```
backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/g1appdev/Hubbits/
│   │   │       ├── config/
│   │   │       │   ├── SecurityConfig.java
│   │   │       │   └── SupabaseConfig.java
│   │   │       ├── controller/
│   │   │       │   ├── AdoptionController.java
│   │   │       │   ├── LostAndFoundController.java
│   │   │       │   ├── PetController.java
│   │   │       │   └── UserController.java
│   │   │       ├── model/
│   │   │       │   ├── AuthRequest.java
│   │   │       │   └── AuthResponse.java
│   │   │       ├── entity/
│   │   │       │   ├── UserEntity.java
│   │   │       │   ├── PetEntity.java
│   │   │       │   ├── AdoptionEntity.java
│   │   │       │   └── LostAndFoundEntity.java
│   │   │       ├── repository/
│   │   │       │   ├── UserRepository.java
│   │   │       │   ├── PetRepository.java
│   │   │       │   ├── AdoptionRepository.java
│   │   │       │   └── LostAndFoundRepository.java
│   │   │       ├── security/
│   │   │       │   ├── JwtTokenProvider.java
│   │   │       │   └── JwtAuthenticationFilter.java
│   │   │       ├── service/
│   │   │       │   ├── UserService.java
│   │   │       │   ├── PetService.java
│   │   │       │   ├── AdoptionService.java
│   │   │       │   └── LostAndFoundService.java
│   │   │       └── HubbitsApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── application-supabase.properties
│   └── test/
└── pom.xml
```

## Authentication & Security

### JWT Implementation
- Spring Security with JWT token-based authentication
- Token validation and user extraction
- Role-based access control
- Secure password handling

### Security Configuration
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    // JWT configuration
    // Security filters
    // CORS configuration
    // Authentication provider
}
```

## Supabase Integration

### Storage Configuration
```properties
# application-supabase.properties
supabase.url=your-supabase-url
supabase.key=your-supabase-key
supabase.storage.bucket=your-bucket-name
```

### File Storage
- Image upload and retrieval
- File type validation
- Secure URL generation
- CDN integration

## API Endpoints

### User Management
- Registration
- Login
- Profile management
- Password changes

### Pet Management
- Pet listing and search
- Pet details
- Image upload and management

### Adoption Process
- Adoption application
- Status tracking
- Process management

### Lost & Found
- Report management
- Status updates
- Location tracking

## Mobile Integration

### Base URLs
- Development: `http://localhost:8080/api/`
- Android Emulator: `http://10.0.2.2:8080/api/`
- Production: `https://your-production-url/api/`

### CORS Configuration
- Configured for mobile app access
- Secure headers
- Pre-flight request handling

## Testing

### Unit Tests
- Service layer tests
- Repository tests
- Security tests

### Integration Tests
- API endpoint tests
- Database integration
- File upload tests

## Deployment

### Docker Support
- Multi-stage build
- Optimized image size
- Health checks
- Non-root user security

### Environment Configuration
- Development
- Staging
- Production

## Monitoring

### Logging
- Application logs
- Error tracking
- Performance metrics

### Health Checks
- Actuator endpoints
- Custom health indicators
- Performance monitoring
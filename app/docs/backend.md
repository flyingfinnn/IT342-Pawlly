# 🐾 Pawlly Mobile - Backend Implementation Guide

## Overview
The backend is built using Spring Boot and integrates with Supabase for data storage. This document outlines the implementation strategy and architecture.

## Tech Stack
- **Framework:** Spring Boot 3.x
- **Database:** Supabase (PostgreSQL)
- **Authentication:** Supabase Auth
- **Storage:** Supabase Storage
- **Build Tool:** Maven
- **Java Version:** 17

## Project Structure
```
backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/sysinteg/pawlly/
│   │   │       ├── config/
│   │   │       │   ├── SecurityConfig.java
│   │   │       │   └── SupabaseConfig.java
│   │   │       ├── controller/
│   │   │       │   ├── AdoptionController.java
│   │   │       │   ├── LostFoundController.java
│   │   │       │   ├── PetController.java
│   │   │       │   └── UserController.java
│   │   │       ├── model/
│   │   │       │   ├── Adoption.java
│   │   │       │   ├── LostFound.java
│   │   │       │   ├── Pet.java
│   │   │       │   └── User.java
│   │   │       ├── repository/
│   │   │       │   ├── AdoptionRepository.java
│   │   │       │   ├── LostFoundRepository.java
│   │   │       │   ├── PetRepository.java
│   │   │       │   └── UserRepository.java
│   │   │       ├── service/
│   │   │       │   ├── AdoptionService.java
│   │   │       │   ├── LostFoundService.java
│   │   │       │   ├── PetService.java
│   │   │       │   └── UserService.java
│   │   │       └── PawllyApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── application-supabase.properties
│   └── test/
└── pom.xml
```

## Supabase Integration

### Configuration
1. **Database Connection:**
   ```properties
   # application-supabase.properties
   supabase.url=your-supabase-url
   supabase.key=your-supabase-key
   supabase.jwt.secret=your-jwt-secret
   ```

2. **Security Configuration:**
   - JWT authentication using Supabase tokens
   - Role-based access control
   - CORS configuration for mobile app

### Data Models
1. **User Model:**
   ```java
   @Entity
   public class User {
       @Id
       private String id;  // Supabase UUID
       private String email;
       private String name;
       private String profilePicture;
       // ... other fields
   }
   ```

2. **Pet Model:**
   ```java
   @Entity
   public class Pet {
       @Id
       private String id;
       private String name;
       private String breed;
       private String age;
       private List<String> images;
       // ... other fields
   }
   ```

## API Implementation

### Controllers
1. **UserController:**
   - User registration and authentication
   - Profile management
   - Password changes

2. **PetController:**
   - Pet listing and search
   - Pet details
   - Image upload and management

3. **AdoptionController:**
   - Adoption process management
   - Status updates
   - Application tracking

4. **LostFoundController:**
   - Lost/found pet reports
   - Status updates
   - Location tracking

### Services
1. **UserService:**
   - User management
   - Authentication
   - Profile updates

2. **PetService:**
   - Pet CRUD operations
   - Image handling
   - Search functionality

3. **AdoptionService:**
   - Adoption process
   - Status management
   - Notifications

4. **LostFoundService:**
   - Report management
   - Location services
   - Matching algorithm

## Security Implementation

### Authentication
1. **JWT Token Validation:**
   - Validate Supabase JWT tokens
   - Extract user information
   - Role verification

2. **Authorization:**
   - Role-based access control
   - Resource ownership checks
   - Admin privileges

### Data Protection
1. **Input Validation:**
   - Request body validation
   - File upload validation
   - SQL injection prevention

2. **Error Handling:**
   - Custom exception handling
   - Proper error responses
   - Logging

## File Storage

### Image Handling
1. **Upload Process:**
   - Validate file types
   - Resize images
   - Generate thumbnails
   - Store in Supabase Storage

2. **Retrieval:**
   - Generate signed URLs
   - Cache management
   - CDN integration

## Testing Strategy

### Unit Tests
- Service layer tests
- Repository tests
- Utility tests

### Integration Tests
- API endpoint tests
- Database integration
- File upload tests

### Security Tests
- Authentication tests
- Authorization tests
- Input validation tests

## Deployment

### Environment Setup
1. **Development:**
   - Local Supabase instance
   - Development database
   - Debug logging

2. **Production:**
   - Production Supabase
   - SSL configuration
   - Monitoring

### CI/CD Pipeline
1. **Build:**
   - Maven build
   - Test execution
   - Code quality checks

2. **Deploy:**
   - Docker containerization
   - Kubernetes deployment
   - Health checks

## Monitoring and Logging

### Logging
- Application logs
- Error tracking
- Performance metrics

### Monitoring
- Health endpoints
- Performance monitoring
- Error tracking

## API Documentation

### Swagger/OpenAPI
- API documentation
- Request/response examples
- Authentication details

### Postman Collection
- API testing
- Documentation
- Example requests

## Mobile Integration Notes (Android Emulator)

- **Base URL for Emulator:**
  - When testing from the Android emulator, use `http://10.0.2.2:8080/api/` as the backend base URL instead of `localhost` or `127.0.0.1`.
  - For real devices, use your host machine's local network IP (e.g., `http://192.168.1.100:8080/api/`).
- **CORS:**
  - Ensure CORS settings on the backend allow requests from the emulator and your local network.
- **Moshi (Kotlin Clients):**
  - If using Moshi for JSON serialization/deserialization in Android/Kotlin, add `KotlinJsonAdapterFactory` to the Moshi builder:
    ```kotlin
    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    ```
  - Add the dependency: `implementation("com.squareup.moshi:moshi-kotlin:1.15.0")`

## Troubleshooting Mobile/Backend Integration

- **Cannot connect to backend from emulator:**
  - Make sure you are using `10.0.2.2` as the host in your base URL.
  - Ensure your backend is running and accessible on port 8080.
  - Check firewall settings on your host machine.
- **Moshi converter error (Kotlin):**
  - Ensure you are using `KotlinJsonAdapterFactory` in your Moshi builder.
  - Make sure your data classes match the backend JSON structure.
- **CORS errors:**
  - Update backend CORS configuration to allow requests from your app's origin.

## Next Steps

1. **Immediate Tasks:**
   - Set up Supabase configuration
   - Implement basic CRUD operations
   - Set up authentication

2. **Future Enhancements:**
   - Real-time updates
   - Advanced search
   - Analytics integration

## Authentication/Login Response

- On successful login, the backend returns a JSON object:
  ```json
  { "token": "<JWT token here>" }
  ```
- Clients (mobile/web) must parse the response as JSON and extract the `token` property.
- The backend no longer returns a plain string for the token.

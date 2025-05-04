# Pawlly Mobile — Technical Overview

## App Identity
- **App Name:** Pawlly Mobile
- **Package:** `com.sysinteg.pawlly`
- **Version:** 1.0 (versionCode: 1)
- **Minimum SDK:** 24 (Android 7.0)
- **Target/Compile SDK:** 34 (Android 14)
- **Build Tools:** AGP 8.2.2, Gradle 8.10, Kotlin 1.9.22, JDK 17

## Architecture
- **Mobile App (Kotlin/Compose)** → **Spring Boot Backend** → **Supabase (Storage)**
- **Authentication:** JWT with Spring Security (Backend) + Firebase OAuth (Mobile)

## Core Technologies

- **Language:** Kotlin
- **UI:** Jetpack Compose (BOM-managed, 2024.05.00), Material Design 3
- **Architecture:** MVVM, Clean Architecture, Repository Pattern
- **Dependency Injection:** Hilt 2.50
- **Navigation:** Navigation Compose (2.7.7)
- **Image Loading:** Coil (2.5.0)
- **Animations:** Lottie
- **Networking:** Retrofit (2.9.0), OkHttp (4.12.0), Moshi
- **Authentication:** Firebase OAuth
- **Persistence:** Room (2.6.1), DataStore
- **Async:** Coroutines, Flow, WorkManager
- **Image Processing:** ImagePicker (2.1)

## Project Structure
```
app/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/sysinteg/pawlly/
│   │   │       ├── MainActivity.kt
│   │   │       ├── PawllyApplication.kt
│   │   │       ├── di/
│   │   │       │   ├── FirebaseModule.kt
│   │   │       │   └── NetworkModule.kt
│   │   │       ├── navigation/
│   │   │       │   └── NavGraph.kt
│   │   │       ├── ui/
│   │   │       │   ├── screens/
│   │   │       │   │   ├── Adopt_PetDetail.kt
│   │   │       │   │   ├── Adopt_SearchResults.kt
│   │   │       │   │   ├── HomeScreen.kt
│   │   │       │   │   ├── LandingScreen.kt
│   │   │       │   │   ├── ProfileScreen.kt
│   │   │       │   │   ├── SettingsScreen.kt
│   │   │       │   │   ├── LoginScreen.kt
│   │   │       │   │   └── SignUpScreen.kt
│   │   │       │   ├── components/
│   │   │       │   │   ├── ImageCarousel.kt
│   │   │       │   │   └── PetCard.kt
│   │   │       │   └── theme/
│   │   │       │       ├── Color.kt
│   │   │       │       ├── Theme.kt
│   │   │       │       └── Type.kt
│   │   │       ├── model/
│   │   │       ├── network/
│   │   │       ├── api/
│   │   │       └── utils/
│   │   └── res/
│   └── test/
```

## UI Components

### Navigation
- **NavGraph.kt:** Centralized navigation using Compose Navigation
- **Bottom Navigation:** Custom `PawllyNavBar` with three main sections (Home, Notifications, Profile)

### Reusable Components
- **ImageCarousel:** Used in `Adopt_PetDetail.kt` for displaying multiple pet images
- **PetCard:** Used in `HomeScreen.kt` and `Adopt_SearchResults.kt` for displaying pet information

### Screens
- **Adopt Flow:**
  - Home Screen with pet listings
  - Search Results with filtering
  - Pet Detail with image carousel
  - Adoption process steps
- **Authentication:**
  - Login with Firebase OAuth
  - Sign Up with form validation
- **Profile & Settings:**
  - User profile management
  - App settings
- **Notifications:**
  - System notifications
  - Adoption status updates

## Networking

### API Integration
- Retrofit for REST API calls
- OkHttp for network operations
- Moshi for JSON serialization
- JWT token handling
- Firebase OAuth integration

### API Configuration
```kotlin
// ApiConfig.kt
object ApiConfig {
    const val BASE_URL = "http://10.0.2.2:8080/api/" // Android emulator
    // const val BASE_URL = "http://localhost:8080/api/" // Development
    // const val BASE_URL = "https://your-production-url/api/" // Production
}
```

## Authentication

### Firebase OAuth
- Google Sign-In integration
- Token management
- User session handling

### JWT Integration
- Token storage in SharedPreferences
- Automatic token refresh
- Secure API calls

## Image Handling

### Components
- **ImageCarousel:** For displaying multiple images
- **PetCard:** For displaying pet thumbnails
- **Profile Images:** For user avatars

### Image Processing
- ImagePicker for photo selection
- Coil for image loading and caching
- Image compression and optimization

## Theme & Styling

### Material Design 3
- Custom color scheme
- Typography system
- Component styling
- Dark/Light mode support

### Custom Components
- Purple rounded rectangle navigation bar
- White mode option buttons
- Custom card designs
- Consistent spacing and padding

## Testing

### Unit Tests
- ViewModel tests
- Repository tests
- Utility tests

### UI Tests
- Screen navigation tests
- Component interaction tests
- State management tests

## Build & Deployment

### Gradle Configuration
- AGP 8.2.2
- Kotlin 1.9.22
- JDK 17
- Dependency management

### Environment Setup
1. Clone repository
2. Configure Firebase project
3. Set up backend connection
4. Build and run

## Troubleshooting

### Common Issues
- Firebase OAuth configuration
- Backend connection in emulator
- Image loading and caching
- Navigation state management

### Debugging Tips
- Use Android Studio's Layout Inspector
- Monitor network calls with OkHttp logging
- Check Firebase Authentication logs
- Verify JWT token validity
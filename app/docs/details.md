# Pawlly Mobile — Technical Overview

## App Identity
- **App Name:** Pawlly Mobile
- **Package:** `com.sysinteg.pawlly`
- **Version:** 1.0 (versionCode: 1)
- **Minimum SDK:** 24 (Android 7.0)
- **Target/Compile SDK:** 34 (Android 14)
- **Build Tools:** AGP 8.2.2, Gradle 8.10, Kotlin 1.9.22, JDK 17

---

## Core Technologies

- **Language:** Kotlin
- **UI:** Jetpack Compose (BOM-managed, 2024.05.00), Material Design 3 (BOM-managed)
- **Architecture:** MVVM, Clean Architecture, Repository Pattern
- **Dependency Injection:** Hilt 2.50
- **Navigation:** Navigation Compose (2.7.7)
- **Image Loading:** Coil (2.5.0)
- **Animations:** Lottie
- **Networking:** Retrofit (2.9.0), OkHttp (4.12.0), Moshi
- **Persistence:** Room (2.6.1), DataStore
- **Async:** Coroutines, Flow, WorkManager

---

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
│   │   │       │   └── FirebaseModule.kt
│   │   │       ├── navigation/
│   │   │       │   └── NavGraph.kt
│   │   │       └── ui/
│   │   │           ├── screens/
│   │   │           │   ├── HomeScreen.kt
│   │   │           │   ├── LandingScreen.kt
│   │   │           │   ├── ProfileScreen.kt
│   │   │           │   ├── SettingsScreen.kt
│   │   │           │   ├── LoginScreen.kt
│   │   │           │   ├── SignUpScreen.kt
│   │   │           │   ├── NotificationScreen.kt
│   │   │           │   ├── NavBarItem.kt
│   │   │           │   └── PawllyNavBar.kt
│   │   │           └── theme/
│   │   │               ├── Color.kt
│   │   │               ├── Theme.kt
│   │   │               └── Type.kt
│   │   └── res/
│   │       ├── drawable/
│   │       ├── mipmap-xxxhdpi/ (ic_launcher.png, ic_launcher_round.webp)
│   │       ├── mipmap-xxhdpi/ (ic_launcher.png, ic_launcher_round.webp)
│   │       ├── mipmap-xhdpi/ (ic_launcher.png, ic_launcher_round.webp)
│   │       ├── mipmap-hdpi/ (ic_launcher.png, ic_launcher_round.webp)
│   │       ├── mipmap-mdpi/ (ic_launcher.png, ic_launcher_round.webp)
│   │       ├── mipmap-anydpi-v26/ (empty)
│   │       ├── values/
│   │       └── xml/
│   └── test/
│   └── androidTest/
```

---

## UI & Design Updates (2024)

- **Navigation Bar:**
  - All main screens (Home, Profile, Notifications) use a consistent `PawllyNavBar` component.
  - The navbar is a purple rounded rectangle, always visible, with three icons and labels (Home, Notifications, Profile).
  - The selected item is fully white (icon and text); unselected items are semi-transparent white.
  - The navbar is implemented in `ui/screens/PawllyNavBar.kt` and uses `NavBarItem` for each button.

- **Mode Option Buttons (HomeScreen):**
  - All mode option buttons are white, 120dp tall, with a left-aligned icon, bold title, description, and a chevron (arrow) on the right.
  - Buttons are centered vertically on the page.
  - No selection highlight; all buttons remain white.

- **General UI:**
  - All UI is built with Jetpack Compose and Material3.
  - The Inter font is used for all text.
  - Theming and color are managed in `ui/theme/Theme.kt`.

---

## Resource & Icon Conventions

- **Launcher Icon:**  
  - All `mipmap-*` folders contain `ic_launcher.png` (your logo, resized).
  - `ic_launcher_round.webp` is used for round icon variant.
  - No adaptive icon XMLs are present, so the PNG is used directly.
- **Drawable Usage:**  
  - Use the `ResourceImage` composable to load drawables by name in Compose:
    ```kotlin
    ResourceImage(resourceName = "logoiconpurple", contentDescription = "Logo")
    ```
  - For Google sign-in, a Material icon (`Icons.Filled.AccountCircle`) is used instead of a custom drawable.
  - This keeps resource usage dynamic and Compose-idiomatic.

---

## Dependency Versions & Compose BOM

- **Compose BOM:** 2024.05.00 (all Compose dependencies are versionless and managed by the BOM)
- **foundation-layout:** Used for Modifier.weight support in Compose layouts.
- **Material3:** Versionless, managed by BOM.
- **Best Practice:** Always use the Compose BOM to avoid version conflicts and ensure compatibility.
- **Other Key Libraries:**
  - **AndroidX Core:** 1.12.0
  - **AppCompat:** 1.6.1
  - **Lifecycle Runtime:** 2.7.0
  - **Activity Compose:** 1.8.2
  - **Navigation Compose:** 2.7.7
  - **Hilt:** 2.50
  - **Hilt Navigation Compose:** 1.1.0
  - **Retrofit:** 2.9.0
  - **OkHttp:** 4.12.0
  - **Room:** 2.6.1
  - **Coil:** 2.5.0
  - **JUnit:** 4.13.2
  - **MockK:** 1.13.9
  - **Espresso:** 3.5.1
  - **Material Components:** 1.11.0

---

## Build & Tooling

- **Gradle:** 8.10 (see `gradle/wrapper/gradle-wrapper.properties`)
- **Android Gradle Plugin:** 8.2.2
- **Kotlin:** 1.9.22
- **JDK:** 17
- **Android Studio:** Hedgehog | 2023.1.1 or later

---

## Setup Instructions

1. **Clone the repository**
2. **Open in Android Studio**
3. **Sync Gradle**
4. **Build the project**
5. **Run on device/emulator**

---

## Testing

- **Unit Tests:** JUnit, MockK
- **UI Tests:** Espresso, Compose UI Test
- **Hilt Testing:** Supported

---

## Special Notes

- **Hilt:**  
  - `@AndroidEntryPoint` is used in all Hilt-injected activities and viewmodels.
  - `PawllyApplication.kt` is annotated with `@HiltAndroidApp` and registered in the manifest.
  - A Hilt module (`FirebaseModule.kt`) provides `FirebaseAuth` for dependency injection.
- **Theme:**  
  - Custom theme in `ui/theme/Theme.kt`, with light/dark and dynamic color support.
- **Navigation:**  
  - Centralized in `navigation/NavGraph.kt` using Compose navigation.
- **Compose Migration:**
  - All major screens have been migrated to Jetpack Compose.
  - Experimental Material3 APIs are used with `@OptIn(ExperimentalMaterial3Api::class)` where required.
- **Troubleshooting:**  
  - If launcher icon does not update, uninstall the app and clear launcher cache.
  - All resources should be referenced by name using `ResourceImage` in Compose, except for Google sign-in which uses a Material icon.
  - If you see Hilt errors, ensure the application class and manifest are set up as above.
  - If you see errors about Modifier.weight, ensure `foundation-layout` is included and Compose BOM is up to date.
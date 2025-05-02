package com.sysinteg.pawlly.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sysinteg.pawlly.model.Notification
import com.sysinteg.pawlly.model.NotificationType
import com.sysinteg.pawlly.ui.screens.*
import java.util.*
import androidx.compose.runtime.mutableStateOf
import android.content.Context
import android.widget.Toast
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import androidx.compose.ui.platform.LocalContext
import androidx.activity.ComponentActivity
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import com.sysinteg.pawlly.userApi
import androidx.compose.runtime.rememberCoroutineScope
import com.sysinteg.pawlly.LoginRequest
import com.sysinteg.pawlly.utils.Constants.PAWLLY_PREFS
import com.sysinteg.pawlly.utils.Constants.KEY_JWT_TOKEN
import com.sysinteg.pawlly.utils.Constants.KEY_REMEMBER_ME
import com.sysinteg.pawlly.utils.Constants.KEY_SAVED_EMAIL
import androidx.compose.runtime.LaunchedEffect
import com.sysinteg.pawlly.UserSignupRequest
import java.io.File
import okhttp3.RequestBody.Companion.asRequestBody

sealed class Screen(val route: String) {
    object Landing : Screen("landing")
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object Home : Screen("home")
    object Profile : Screen("profile")
    object Settings : Screen("settings")
    object Notifications : Screen("notifications")
    object NotificationDetail : Screen("notification_detail/{notificationId}")
    // Adopt flow
    object AdoptHome : Screen("adopt")
    object AdoptResults : Screen("adopt/results")
    object AdoptPetDetail : Screen("adopt/pet/{id}")
    object AdoptStep1 : Screen("adopt/start")
    object AdoptStep2 : Screen("adopt/address")
    object AdoptStep3 : Screen("adopt/home")
    object AdoptStep4 : Screen("adopt/images")
    object AdoptStep5 : Screen("adopt/roommate")
    object AdoptStep6 : Screen("adopt/other-animals")
    object AdoptStep7 : Screen("adopt/confirm")
    object AdoptFinish : Screen("adopt/finish")
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Landing.route
    ) {
        composable(Screen.Landing.route) {
            LandingScreen(navController)
        }
        composable(Screen.Login.route) {
            val context = LocalContext.current
            val coroutineScope = rememberCoroutineScope()
            LoginScreen(
                onLoginClick = { email, password ->
                    coroutineScope.launch(Dispatchers.IO) {
                        try {
                            // Validate email format
                            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(context, "Invalid email address!", Toast.LENGTH_LONG).show()
                                }
                            } else if (password.length < 8) {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(context, "Password must be at least 8 characters!", Toast.LENGTH_LONG).show()
                                }
                            } else {
                                val loginRequest = LoginRequest(
                                    email = email.trim(),
                                    password = password.trim()
                                )
                                
                                Log.d("NavGraph", "Attempting login with email: ${loginRequest.email}")
                                val loginResponse = userApi.login(loginRequest)
                                val token = loginResponse.token
                                
                                // Store token in SharedPreferences using constants
                                val prefs = context.getSharedPreferences(PAWLLY_PREFS, Context.MODE_PRIVATE)
                                prefs.edit().putString(KEY_JWT_TOKEN, token).apply()
                                
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(context, "Login successful!", Toast.LENGTH_LONG).show()
                                    navController.navigate(Screen.Home.route) {
                                        popUpTo(Screen.Login.route) { inclusive = true }
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("NavGraph", "Login failed", e)
                            withContext(Dispatchers.Main) {
                                val errorMessage = when {
                                    e.message?.contains("401") == true -> "Invalid email or password"
                                    e.message?.contains("500") == true -> "Server error. Please try again later"
                                    e.message?.contains("Failed to connect") == true -> "Server connection error. Please check your internet connection"
                                    e.message?.contains("timeout") == true -> "Connection timeout. Please check your internet connection"
                                    else -> "Login failed: ${e.message}"
                                }
                                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                },
                onForgotPasswordClick = { /* TODO: handle forgot password */ },
                onSignUpClick = { navController.navigate(Screen.SignUp.route) },
                onGoogleSignInClick = {
                    // The actual Google Sign-In is handled in LoginScreen.kt
                    // This is just a placeholder to satisfy the interface
                },
                showSuccessChip = false,
                navController = navController
            )
        }
        composable(Screen.SignUp.route) {
            val signUpStatus = remember { mutableStateOf(SignUpStatus.None) }
            val signUpErrorMessage = remember { mutableStateOf("") }
            val context = LocalContext.current
            val coroutineScope = rememberCoroutineScope()
            SignUpScreen(
                onSignUpClick = { firstName, lastName, username, email, password, phoneNumber, address, confirmPassword, profilePictureUri ->
                    coroutineScope.launch(Dispatchers.IO) {
                        try {
                            // Validate passwords match before proceeding
                            if (password != confirmPassword) {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(context, "Passwords do not match!", Toast.LENGTH_LONG).show()
                                    signUpStatus.value = SignUpStatus.Failed
                                    signUpErrorMessage.value = "Passwords do not match"
                                }
                                return@launch
                            }

                            // Validate password length
                            if (password.length < 8) {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(context, "Password must be at least 8 characters!", Toast.LENGTH_LONG).show()
                                    signUpStatus.value = SignUpStatus.Failed
                                    signUpErrorMessage.value = "Password too short"
                                }
                                return@launch
                            }

                            // Validate email format
                            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(context, "Invalid email address!", Toast.LENGTH_LONG).show()
                                    signUpStatus.value = SignUpStatus.Failed
                                    signUpErrorMessage.value = "Invalid email address"
                                }
                                return@launch
                            }

                            // Validate required fields
                            if (firstName.isNullOrEmpty() || lastName.isNullOrEmpty() || username.isNullOrEmpty() || 
                                email.isNullOrEmpty() || password.isNullOrEmpty() || phoneNumber.isNullOrEmpty() || 
                                address.isNullOrEmpty()) {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(context, "All fields are required!", Toast.LENGTH_LONG).show()
                                    signUpStatus.value = SignUpStatus.Failed
                                    signUpErrorMessage.value = "Missing required fields"
                                }
                                return@launch
                            }

                            Log.d("SignUpActivity", "Coroutine started")
                            // Create a JSON object for the user data
                            val userJson = JSONObject().apply {
                                put("username", username)
                                put("firstName", firstName)
                                put("lastName", lastName)
                                put("email", email)
                                put("password", password)
                                put("address", address)
                                put("phoneNumber", phoneNumber)
                            }.toString()

                            // Create the user part of the multipart request
                            val userPart = userJson.toRequestBody("application/json".toMediaTypeOrNull())

                            // Handle profile picture if provided
                            var profilePicturePart: MultipartBody.Part? = null
                            profilePictureUri?.let { uri ->
                                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                                    val file = File(context.cacheDir, "profile_picture")
                                    file.outputStream().use { outputStream ->
                                        inputStream.copyTo(outputStream)
                                    }
                                    val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                                    profilePicturePart = MultipartBody.Part.createFormData(
                                        "profilePicture",
                                        "profile_picture.jpg",
                                        requestFile
                                    )
                                }
                            }

                            // Call the multipart signup API
                            val response = userApi.signUpWithProfilePicture(
                                user = userPart,
                                profilePicture = profilePicturePart
                            )
                            Log.d("SignUpActivity", "Sign up response: $response")
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "Signup successful!", Toast.LENGTH_LONG).show()
                                signUpStatus.value = SignUpStatus.Success
                                signUpErrorMessage.value = ""
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(Screen.SignUp.route) { inclusive = true }
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("SignUpActivity", "Sign up error", e)
                            withContext(Dispatchers.Main) {
                                val msg = e.message ?: "Unknown error"
                                val errorMsg = when {
                                    msg.contains("Username already exists", true) -> "Failed: Username already exists"
                                    msg.contains("Email already exists", true) -> "Failed: Email already exists"
                                    msg.contains("Password must be at least", true) -> "Failed: Password too short"
                                    msg.contains("All fields", true) -> "Failed: Missing fields"
                                    msg.contains("Network", true) || msg.contains("timeout", true) -> "Failed: Network error. Please try again."
                                    msg.contains("address", true) && msg.contains("invalid", true) -> "Failed: Invalid email address"
                                    else -> "Failed: $msg"
                                }
                                Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                                signUpStatus.value = SignUpStatus.Failed
                                signUpErrorMessage.value = errorMsg
                            }
                        }
                    }
                },
                onGoogleSignInClick = { /* TODO: handle Google sign in */ },
                onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                signUpStatus = signUpStatus,
                errorMessage = signUpErrorMessage
            )
        }
        composable(Screen.Home.route) {
            AdoptScreen(
                onBrowseAll = { navController.navigate(Screen.AdoptResults.route) },
                onPetClick = { id -> navController.navigate("adopt/pet/$id") },
                onFilterClick = { /* TODO: filter logic */ },
                onLostFoundClick = { /* TODO: navigate to Lost and Found */ },
                onNavHome = { navController.navigate(Screen.Home.route) },
                onNavNotifications = { navController.navigate(Screen.Notifications.route) },
                onNavProfile = { navController.navigate(Screen.Profile.route) }
            )
        }
        composable(Screen.Profile.route) {
            val context = LocalContext.current
            val prefs = remember { context.getSharedPreferences(PAWLLY_PREFS, Context.MODE_PRIVATE) }
            val token = remember { prefs.getString(KEY_JWT_TOKEN, null) }
            
            if (token == null) {
                // If no token, redirect to login
                LaunchedEffect(Unit) {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Profile.route) { inclusive = true }
                    }
                }
                return@composable
            }

            ProfileScreen(
                onLogout = { 
                    // Clear all authentication data
                    prefs.edit()
                        .remove(KEY_JWT_TOKEN)
                        .remove(KEY_REMEMBER_ME)
                        .remove(KEY_SAVED_EMAIL)
                        .apply()
                    
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Profile.route) { inclusive = true }
                    }
                },
                onNavHome = { navController.navigate(Screen.Home.route) },
                onNavProfile = { navController.navigate(Screen.Profile.route) },
                onNavNotifications = { navController.navigate(Screen.Notifications.route) },
                onAddPet = { navController.navigate("add_pet") },
                onPetDetail = { petId -> navController.navigate("pet_detail/$petId") }
            )
        }
        composable(Screen.Notifications.route) {
            NotificationScreen(
                navController = navController,
                onNavHome = { navController.navigate(Screen.Home.route) },
                onNavProfile = { navController.navigate(Screen.Profile.route) },
                onNavNotifications = { navController.navigate(Screen.Notifications.route) },
                selectedScreen = "Notifications"
            )
        }
        composable(
            route = Screen.NotificationDetail.route,
            arguments = listOf(
                navArgument("notificationId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val notificationId = backStackEntry.arguments?.getString("notificationId") ?: ""
            val notification = Notification(
                id = notificationId,
                title = "Sample Notification",
                body = "This is a sample notification detail view.",
                type = NotificationType.SYSTEM,
                timestamp = Date()
            )
            NotificationDetailScreen(
                notification = notification,
                navController = navController
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(navController)
        }
        // Adopt flow
        composable(Screen.AdoptHome.route) {
            AdoptScreen(
                onBrowseAll = { navController.navigate(Screen.AdoptResults.route) },
                onPetClick = { id -> navController.navigate("adopt/pet/$id") },
                onFilterClick = { /* TODO: filter logic */ },
                onLostFoundClick = { /* TODO: navigate to Lost and Found */ },
                onNavHome = { navController.navigate(Screen.Home.route) },
                onNavNotifications = { navController.navigate(Screen.Notifications.route) },
                onNavProfile = { navController.navigate(Screen.Profile.route) }
            )
        }
        composable(Screen.AdoptResults.route) {
            AdoptSearchResultsScreen(
                onPetClick = { id -> navController.navigate("adopt/pet/$id") },
                onBack = { navController.popBackStack() },
                onFilter = { /* TODO: filter logic */ },
                onNavHome = { navController.navigate(Screen.Home.route) },
                onNavNotifications = { navController.navigate(Screen.Notifications.route) },
                onNavProfile = { navController.navigate(Screen.Profile.route) }
            )
        }
        composable("adopt/pet/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: 1
            AdoptPetDetailScreen(
                petId = id,
                onAdoptNow = { navController.navigate(Screen.AdoptStep1.route) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.AdoptStep1.route) {
            AdoptAdoptionStep1Screen(
                onStart = { navController.navigate(Screen.AdoptStep2.route) }
            )
        }
        composable(Screen.AdoptStep2.route) {
            AdoptAdoptionStep2Screen(
                onContinue = { navController.navigate(Screen.AdoptStep3.route) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.AdoptStep3.route) {
            AdoptAdoptionStep3Screen(
                onContinue = { navController.navigate(Screen.AdoptStep4.route) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.AdoptStep4.route) {
            AdoptAdoptionStep4Screen(
                onContinue = { navController.navigate(Screen.AdoptStep5.route) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.AdoptStep5.route) {
            AdoptAdoptionStep5Screen(
                onContinue = { navController.navigate(Screen.AdoptStep6.route) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.AdoptStep6.route) {
            AdoptAdoptionStep6Screen(
                onContinue = { navController.navigate(Screen.AdoptStep7.route) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.AdoptStep7.route) {
            AdoptAdoptionStep7Screen(
                onReturnToHome = { navController.navigate(Screen.Home.route) }
            )
        }
        composable(Screen.AdoptFinish.route) {
            AdoptAdoptionFinishScreen(
                onReturnToProfile = { navController.navigate(Screen.Profile.route) },
                onAdoptMore = { navController.navigate(Screen.AdoptHome.route) }
            )
        }
        composable("add_pet") {
            AddPetScreen(
                onPetAdded = { navController.popBackStack() }
            )
        }
        composable("pet_detail/{petId}") { backStackEntry ->
            val petId = backStackEntry.arguments?.getString("petId")?.toIntOrNull() ?: 0
            PetDetailScreen(
                petId = petId,
                onEdit = { /* TODO: handle edit */ },
                onBack = { navController.popBackStack() }
            )
        }
    }
} 
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState

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
    object AdoptPetDetail : Screen("adopt/pet/{id}/{name}")
    object AdoptStart : Screen("adopt/start")
    object AdoptPersonal : Screen("adopt/personal")
    object AdoptHousehold : Screen("adopt/household")
    object AdoptOtherPets : Screen("adopt/otherpets")
    object AdoptLifestyle : Screen("adopt/lifestyle")
    object AdoptAgreement : Screen("adopt/agreement")
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
                                
                                // Fetch user details to get user_id
                                val user = userApi.getMe("Bearer $token")
                                prefs.edit().putLong("user_id", user.userId ?: 0L).apply()
                                
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
                                put("role", "ROLE_USER") // Always assign ROLE_USER
                            }.toString()

                            // Log the profilePictureUri
                            Log.d("SignUpActivity", "profilePictureUri: $profilePictureUri")

                            // (Optional) Warn if no profile picture is selected
                            if (profilePictureUri == null) {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(context, "No profile picture selected. Proceeding without one.", Toast.LENGTH_SHORT).show()
                                }
                            }

                            // Create the user part of the multipart request
                            val userPart = userJson.toRequestBody("application/json".toMediaTypeOrNull())

                            // Handle profile picture if provided
                            var profilePicturePart: MultipartBody.Part? = null
                            profilePictureUri?.let { uri ->
                                val mimeType = context.contentResolver.getType(uri) ?: "image/*"
                                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                                    val file = File(context.cacheDir, "profile_picture")
                                    file.outputStream().use { outputStream ->
                                        inputStream.copyTo(outputStream)
                                    }
                                    Log.d("SignUpActivity", "Profile picture MIME type: $mimeType, file size: ${file.length()} bytes")
                                    val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())
                                    profilePicturePart = MultipartBody.Part.createFormData(
                                        "profilePicture",
                                        "profile_picture.${mimeType.substringAfterLast('/')}",
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
                onPetClick = { id -> navController.navigate("pet_detail/$id") },
                onAdoptPetDetail = { id, name -> navController.navigate("adopt/pet/$id/$name") },
                onFilterClick = { /* TODO: filter logic */ },
                onLostFoundClick = { /* TODO: navigate to Lost and Found */ },
                onNavHome = { navController.navigate(Screen.Home.route) },
                onNavNotifications = { navController.navigate(Screen.Notifications.route) },
                onNavProfile = { navController.navigate(Screen.Profile.route + "?editMode=false") }
            )
        }
        composable(
            route = Screen.Profile.route + "?editMode={editMode}",
            arguments = listOf(
                navArgument("editMode") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { backStackEntry ->
            val context = LocalContext.current
            val prefs = context.getSharedPreferences(PAWLLY_PREFS, Context.MODE_PRIVATE)
            val token = prefs.getString(KEY_JWT_TOKEN, null)
            val editMode = backStackEntry.arguments?.getBoolean("editMode") ?: false
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
                onPetDetail = { petId -> navController.navigate("pet_detail/$petId") },
                editMode = editMode
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
                onPetClick = { id -> navController.navigate("pet_detail/$id") },
                onAdoptPetDetail = { id, name -> navController.navigate("adopt/pet/$id/$name") },
                onFilterClick = { /* TODO: filter logic */ },
                onLostFoundClick = { /* TODO: navigate to Lost and Found */ },
                onNavHome = { navController.navigate(Screen.Home.route) },
                onNavNotifications = { navController.navigate(Screen.Notifications.route) },
                onNavProfile = { navController.navigate(Screen.Profile.route + "?editMode=false") }
            )
        }
        composable(Screen.AdoptResults.route) {
            AdoptSearchResultsScreen(
                onPetClick = { id -> navController.navigate("pet_detail/$id") },
                onAdoptPetDetail = { id, name -> navController.navigate("adopt/pet/$id/$name") },
                onBack = { navController.popBackStack() },
                onFilter = { /* TODO: filter logic */ },
                onNavHome = { navController.navigate(Screen.Home.route) },
                onNavNotifications = { navController.navigate(Screen.Notifications.route) },
                onNavProfile = { navController.navigate(Screen.Profile.route + "?editMode=false") }
            )
        }
        composable("adopt/existing_application") {
            val context = LocalContext.current
            val prefs = context.getSharedPreferences(PAWLLY_PREFS, Context.MODE_PRIVATE)
            val petId = prefs.getInt("adopt_pet_id", 0)
            var petName by remember { mutableStateOf<String?>(null) }
            // Optionally fetch pet name if needed
            AdoptExistingApplicationScreen(
                petName = petName,
                onGoToProfile = { navController.navigate(Screen.Profile.route) }
            )
        }
        composable("adopt/pet/{id}/{name}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: 1
            val name = backStackEntry.arguments?.getString("name") ?: ""
            val context = LocalContext.current
            val prefs = context.getSharedPreferences(PAWLLY_PREFS, Context.MODE_PRIVATE)
            // Store the selected pet's pid and name as adopt_pet_id and adopt_pet_name
            LaunchedEffect(id, name) {
                prefs.edit().putInt("adopt_pet_id", id).putString("adopt_pet_name", name).apply()
            }
            AdoptPetDetailScreen(
                petId = id,
                petName = name,
                onAdoptNow = { /* not used */ },
                onBack = { navController.popBackStack() },
                onNavigateToExisting = { navController.navigate("adopt/existing_application") },
                onNavigateToStart = { navController.navigate(Screen.AdoptStart.route) }
            )
        }
        composable(Screen.AdoptStart.route) {
            val context = LocalContext.current
            val prefs = context.getSharedPreferences(PAWLLY_PREFS, Context.MODE_PRIVATE)
            val userId = prefs.getLong("user_id", 0L)
            val petId = prefs.getInt("adopt_pet_id", 0)
            val petName = prefs.getString("adopt_pet_name", "")
            var hasExistingApplication by remember { mutableStateOf(false) }
            val coroutineScope = rememberCoroutineScope()
            LaunchedEffect(userId, petId) {
                if (userId != 0L && petId != 0) {
                    try {
                        val applications = userApi.getAdoptionApplications(userId, petId)
                        android.util.Log.d("AdoptStartAdoption", "userId=$userId, petId=$petId, applications found=${applications.size}")
                        if (applications.isNotEmpty()) {
                            hasExistingApplication = true
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("AdoptStartAdoption", "Error fetching applications", e)
                    }
                }
            }
            AdoptStartAdoptionScreen(
                onNext = { navController.navigate("adopt/process") },
                hasExistingApplication = hasExistingApplication,
                petName = petName,
                onGoToProfile = { navController.navigate(Screen.Profile.route) }
            )
        }
        // --- New Single-Screen Adoption Flow ---
        composable("adopt/process") {
            val viewModel: AdoptionViewModel = hiltViewModel()
            val context = LocalContext.current
            val prefs = context.getSharedPreferences(PAWLLY_PREFS, Context.MODE_PRIVATE)
            val userId = prefs.getLong("user_id", 0L)
            val petId = prefs.getInt("adopt_pet_id", 0)
            val petName = prefs.getString("adopt_pet_name", "")
            val coroutineScope = rememberCoroutineScope()
            
            // Check if we have a valid user ID
            if (userId == 0L) {
                LaunchedEffect(Unit) {
                    Toast.makeText(context, "Please log in again", Toast.LENGTH_LONG).show()
                    navController.navigate(Screen.Login.route) {
                        popUpTo("adopt/process") { inclusive = true }
                    }
                }
                return@composable
            }
            
            AdoptionProcessScreen(
                onBack = { navController.popBackStack() },
                onSubmit = { personal, household, lifestyle ->
                    viewModel.setPersonalInfo(personal)
                    viewModel.setHouseholdInfo(household)
                    viewModel.setLifestyle(lifestyle)
                    coroutineScope.launch {
                        viewModel.submitAdoptionApplication(userId, petId, petName) { success ->
                            if (success) {
                                navController.navigate(Screen.AdoptFinish.route) {
                                    popUpTo("adopt/process") { inclusive = true }
                                }
                            } else {
                                Toast.makeText(context, "Failed to submit application. Please try again.", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            )
        }
        composable(Screen.AdoptFinish.route) {
            AdoptFinishScreen(
                onBack = { navController.popBackStack() },
                onGoToHome = { navController.navigate(Screen.Home.route) }
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
                onBack = { navController.popBackStack() }
            )
        }
    }
} 
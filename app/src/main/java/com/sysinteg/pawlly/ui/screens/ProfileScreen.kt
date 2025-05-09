package com.sysinteg.pawlly.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.auth.FirebaseAuth
import com.sysinteg.pawlly.ui.theme.BoneWhite
import com.sysinteg.pawlly.ui.theme.Inter
import com.sysinteg.pawlly.ui.theme.Purple
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.wrapContentHeight
import com.sysinteg.pawlly.ui.components.PetCard
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import com.sysinteg.pawlly.ui.screens.StatusType
import com.sysinteg.pawlly.ui.screens.AnimatedStatusChip
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import com.sysinteg.pawlly.userApi
import com.sysinteg.pawlly.UserResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import com.sysinteg.pawlly.utils.Constants.PAWLLY_PREFS
import com.sysinteg.pawlly.utils.Constants.KEY_JWT_TOKEN
import android.util.Log
import android.util.Base64
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import java.io.File
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.sysinteg.pawlly.PetResponse
import com.sysinteg.pawlly.model.Pet
import androidx.compose.foundation.layout.heightIn
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.sysinteg.pawlly.R
import androidx.compose.material3.Switch
import androidx.compose.runtime.State

val LightRed = Color(0xFFFF6B6B)

data class Application(
    val id: Int,
    val petId: Int,
    val petName: String,
    val status: ApplicationStatus
)

enum class ApplicationStatus {
    PENDING,
    ACCEPTED
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLogout: () -> Unit = {},
    onNavHome: () -> Unit = {},
    onNavNotifications: () -> Unit = {},
    onNavProfile: () -> Unit = {},
    onAddPet: () -> Unit = {},
    onPetDetail: (Int) -> Unit = {},
    editMode: Boolean = false,
    onAdoptPetDetail: (Int, String) -> Unit = { _, _ -> },
    onViewApplication: (Int, Boolean) -> Unit = { _, _ -> },
    refreshTrigger: State<Boolean>? = null
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences(PAWLLY_PREFS, Context.MODE_PRIVATE) }
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser

    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(true) }
    var fetchError by remember { mutableStateOf("") }

    // User fields from backend
    var userId by remember { mutableStateOf<Long?>(null) }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }
    var profilePictureBase64 by remember { mutableStateOf("") }

    // Edit fields
    var editFirstName by remember { mutableStateOf("") }
    var editLastName by remember { mutableStateOf("") }
    var editUsername by remember { mutableStateOf("") }
    var editEmail by remember { mutableStateOf("") }
    var editPhone by remember { mutableStateOf("") }
    var editAddress by remember { mutableStateOf("") }

    // Load initial values from SharedPreferences or Firebase
    var isEditing by remember { mutableStateOf(editMode) }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    val myPets = remember { mutableStateListOf<Pet>() }
    var petsLoading by remember { mutableStateOf(true) }
    var petsError by remember { mutableStateOf("") }
    var showLogoutDialog by remember { mutableStateOf(false) }
    val myApplications = remember { mutableStateListOf<Application>() }
    var statusType by remember { mutableStateOf(StatusType.None) }
    var showStatusChip by remember { mutableStateOf(false) }

    // For discarding changes, keep a backup of the original values
    var originalFirstName by remember { mutableStateOf(firstName) }
    var originalLastName by remember { mutableStateOf(lastName) }
    var originalUsername by remember { mutableStateOf(username) }
    var originalEmail by remember { mutableStateOf(email) }
    var originalPhone by remember { mutableStateOf(phone) }
    var originalAddress by remember { mutableStateOf(address) }

    // Decode profile picture bitmap safely for Compose
    val decodedProfileBitmap = remember(profilePictureBase64) {
        try {
            if (profilePictureBase64.isNotEmpty()) {
                val imageBytes = Base64.decode(profilePictureBase64, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            } else null
        } catch (e: Exception) {
            null
        }
    }

    // State for profile picture update
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var newProfileBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    var isImageChanged by remember { mutableStateOf(false) }

    // Detect if user signed in with Google
    val isGoogleUser = remember {
        val firebaseUser = auth.currentUser
        firebaseUser?.providerData?.any { it.providerId == "google.com" } == true
    }

    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            selectedImageUri = it
            isImageChanged = true
            val inputStream = context.contentResolver.openInputStream(it)
            newProfileBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
        }
    }

    // Google sign out setup
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()
    val googleSignInClient = GoogleSignIn.getClient(context, gso)

    // Helper for password validation
    fun isValidPassword(password: String): Boolean {
        val passwordPattern = Regex("^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#${'$'}%^&*()_+={};':\"|,.<>/?-]).{8,}")
        return passwordPattern.containsMatchIn(password)
    }

    // Fetch user details on load
    LaunchedEffect(Unit) {
        isLoading = true
        fetchError = ""
        try {
            val token = prefs.getString(KEY_JWT_TOKEN, null)
            Log.d("ProfileScreen", "Retrieved token: $token")
            if (token == null) {
                fetchError = "Not authenticated"
                onLogout()
                return@LaunchedEffect
            }

            try {
                Log.d("ProfileScreen", "Making API call with token: Bearer $token")
                val user = userApi.getMe("Bearer $token")
                Log.d("ProfileScreen", "API call successful, user: $user")
                if (user != null) {
                    userId = user.userId ?: 0L
                    firstName = user.firstName ?: ""
                    lastName = user.lastName ?: ""
                    username = user.username ?: ""
                    email = user.email ?: ""
                    phone = user.phoneNumber ?: ""
                    address = user.address ?: ""
                    role = user.role ?: ""
                    profilePictureBase64 = user.profilePicture ?: ""

                    // Initialize edit fields with current values
                    editFirstName = firstName
                    editLastName = lastName
                    editUsername = username
                    editEmail = email
                    editPhone = phone
                    editAddress = address
                } else {
                    fetchError = "Failed to fetch user details"
                }
            } catch (e: Exception) {
                Log.e("ProfileScreen", "API call failed", e)
                fetchError = e.message ?: "Failed to fetch user details"
                if (e.message?.contains("401") == true ||
                    e.message?.contains("expired", true) == true ||
                    e.message?.contains("JWT", true) == true) {
                    // Clear JWT and force logout
                    prefs.edit().remove(KEY_JWT_TOKEN).apply()
                    onLogout()
                }
            }
        } catch (e: Exception) {
            Log.e("ProfileScreen", "Error in profile loading", e)
            fetchError = e.message ?: "Failed to fetch user details"
            if (e.message?.contains("401") == true ||
                e.message?.contains("expired", true) == true ||
                e.message?.contains("JWT", true) == true) {
                prefs.edit().remove(KEY_JWT_TOKEN).apply()
                onLogout()
            }
        }
        isLoading = false
    }

    // Fetch user's pets from backend
    LaunchedEffect(username) {
        if (username.isNotBlank()) {
            petsLoading = true
            petsError = ""
            try {
                val userPets = withContext(Dispatchers.IO) { userApi.getPetsByUserName(username) }
                myPets.clear()
                myPets.addAll(userPets.map {
                    Pet(
                        id = it.pid,
                        name = it.name ?: "",
                        breed = it.breed ?: "",
                        age = it.age ?: "",
                        location = it.address ?: "",
                        photo1 = it.photo1,
                        photo1Thumb = it.photo1Thumb,
                        photo2 = it.photo2,
                        photo3 = it.photo3,
                        photo4 = it.photo4,
                        weight = it.weight,
                        color = it.color,
                        height = it.height,
                        user_name = it.userName
                    )
                })
                petsLoading = false
            } catch (e: Exception) {
                petsError = e.message ?: "Failed to load pets."
                petsLoading = false
            }
        }
    }

    // Fetch user's adoption applications from backend
    LaunchedEffect(userId) {
        if (userId != null && userId != 0L) {
            try {
                val applications = withContext(Dispatchers.IO) {
                    userApi.getAdoptionApplicationsByUserId(userId!!)
                }
                myApplications.clear()
                myApplications.addAll(applications.map {
                    Application(
                        id = it.id.toInt(),
                        petId = it.petId.toInt(),
                        petName = it.petName ?: "Pet Name",
                        status = when (it.status.lowercase()) {
                            "pending" -> ApplicationStatus.PENDING
                            "accepted", "success" -> ApplicationStatus.ACCEPTED
                            else -> ApplicationStatus.PENDING
                        }
                    )
                })
            } catch (e: Exception) {
                Log.e("ProfileScreen", "Failed to fetch applications", e)
            }
        }
    }

    // Observe refreshTrigger and reload applications when it changes
    LaunchedEffect(refreshTrigger?.value) {
        if (refreshTrigger != null && userId != null && userId != 0L) {
            try {
                val applications = withContext(Dispatchers.IO) {
                    userApi.getAdoptionApplicationsByUserId(userId!!)
                }
                myApplications.clear()
                myApplications.addAll(applications.map {
                    Application(
                        id = it.id.toInt(),
                        petId = it.petId.toInt(),
                        petName = it.petName ?: "Pet Name",
                        status = when (it.status.lowercase()) {
                            "pending" -> ApplicationStatus.PENDING
                            "accepted", "success" -> ApplicationStatus.ACCEPTED
                            else -> ApplicationStatus.PENDING
                        }
                    )
                })
            } catch (e: Exception) {
                Log.e("ProfileScreen", "Failed to refresh applications", e)
            }
        }
    }

    LaunchedEffect(statusType) {
        if (statusType != StatusType.None) {
            showStatusChip = true
            delay(3000)
            showStatusChip = false
            statusType = StatusType.None
        }
    }

    val systemUiController = rememberSystemUiController()
    val useDarkIcons = true
    LaunchedEffect(systemUiController) {
        systemUiController.setSystemBarsColor(
            color = Color(0xFFFAF9F6),
            darkIcons = useDarkIcons
        )
    }

    // Use the username from state as currentUsername
    val currentUsernameValue = username

    // Add state for toggle
    var showIncoming by remember { mutableStateOf(false) }

    // Add state for incoming applications
    val incomingApplications = remember { mutableStateListOf<Application>() }
    var incomingLoading by remember { mutableStateOf(false) }
    var incomingError by remember { mutableStateOf("") }

    // Fetch incoming applications when myPets changes or when showIncoming is toggled on
    LaunchedEffect(myPets, showIncoming) {
        if (showIncoming && myPets.isNotEmpty() && userId != null && userId != 0L) {
            incomingLoading = true
            incomingError = ""
            incomingApplications.clear()
            try {
                val allApplications = mutableListOf<Application>()
                for (pet in myPets) {
                    try {
                        val apps = withContext(Dispatchers.IO) {
                            userApi.getAdoptionApplications(userId = null, petId = pet.id)
                        }
                        allApplications.addAll(apps.filter { it.userId != userId }.map {
                            Application(
                                id = it.id.toInt(),
                                petId = it.petId.toInt(),
                                petName = it.petName ?: pet.name,
                                status = when (it.status.lowercase()) {
                                    "pending" -> ApplicationStatus.PENDING
                                    "accepted", "success" -> ApplicationStatus.ACCEPTED
                                    else -> ApplicationStatus.PENDING
                                }
                            )
                        })
                    } catch (e: Exception) {
                        // Ignore errors for individual pets
                    }
                }
                incomingApplications.addAll(allApplications)
            } catch (e: Exception) {
                incomingError = e.message ?: "Failed to load incoming applications."
            }
            incomingLoading = false
        }
    }

    // Update onViewApplication to accept isOwnerView
    fun handleViewApplication(applicationId: Int, isOwnerView: Boolean) {
        onViewApplication(applicationId, isOwnerView)
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Loading user details...", color = Color.Gray)
        }
        return
    }
    if (fetchError.isNotEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Error: $fetchError", color = Color.Red)
        }
        return
    }

    Scaffold(
        containerColor = BoneWhite,
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                PawllyNavBar(
                    selectedScreen = "Profile",
                    onNavHome = onNavHome,
                    onNavNotifications = onNavNotifications,
                    onNavProfile = onNavProfile
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .background(BoneWhite)
                .padding(innerPadding)
        ) {
            // Status bar background
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
                    .background(Color(0xFFFAF9F6))
            )
            // Status chip in top right
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, end = 16.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                AnimatedStatusChip(visible = showStatusChip, status = statusType)
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 8.dp)
                    .statusBarsPadding(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(48.dp)) // To balance the row visually (like NotificationScreen)
            }

            if (isEditing) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 80.dp)
                        .statusBarsPadding()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp, vertical = 32.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Profile Picture
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(CircleShape)
                                    .background(Purple)
                                    .clickable(enabled = isEditing) { 
                                        if (isEditing) {
                                            imagePickerLauncher.launch("image/*") 
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                val showBitmap = if (isImageChanged && newProfileBitmap != null) newProfileBitmap else decodedProfileBitmap
                                if (showBitmap != null) {
                                    androidx.compose.foundation.Image(
                                        bitmap = showBitmap.asImageBitmap(),
                                        contentDescription = "Profile Picture",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Profile Picture",
                                        tint = Color.Black,
                                        modifier = Modifier.size(80.dp)
                                    )
                                }
                            }
                            if (isEditing) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Tap to change profile picture",
                                    color = Color.Gray,
                                    fontSize = 12.sp,
                                    fontFamily = Inter
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(32.dp))
                        // Edit Form
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                "First Name",
                                fontWeight = FontWeight.Bold,
                                fontFamily = Inter,
                                fontSize = 14.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            OutlinedTextField(
                                value = editFirstName,
                                onValueChange = { editFirstName = it },
                                placeholder = { Text("Enter first name", color = Color.Black) },
                                enabled = isEditing,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedPlaceholderColor = Color.Black,
                                    focusedPlaceholderColor = Color.Black,
                                    disabledPlaceholderColor = Color.Black,
                                    unfocusedBorderColor = Color.LightGray,
                                    focusedBorderColor = Purple,
                                    unfocusedTextColor = Color.Black,
                                    focusedTextColor = Color.Black
                                )
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                "Last Name",
                                fontWeight = FontWeight.Bold,
                                fontFamily = Inter,
                                fontSize = 14.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            OutlinedTextField(
                                value = editLastName,
                                onValueChange = { editLastName = it },
                                placeholder = { Text("Enter last name", color = Color.Black) },
                                enabled = isEditing,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedPlaceholderColor = Color.Black,
                                    focusedPlaceholderColor = Color.Black,
                                    disabledPlaceholderColor = Color.Black,
                                    unfocusedBorderColor = Color.LightGray,
                                    focusedBorderColor = Purple,
                                    unfocusedTextColor = Color.Black,
                                    focusedTextColor = Color.Black
                                )
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                "Username",
                                fontWeight = FontWeight.Bold,
                                fontFamily = Inter,
                                fontSize = 14.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            OutlinedTextField(
                                value = editUsername,
                                onValueChange = { editUsername = it },
                                placeholder = { Text("Enter username", color = Color.Black) },
                                enabled = isEditing,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedPlaceholderColor = Color.Black,
                                    focusedPlaceholderColor = Color.Black,
                                    disabledPlaceholderColor = Color.Black,
                                    unfocusedBorderColor = Color.LightGray,
                                    focusedBorderColor = Purple,
                                    unfocusedTextColor = Color.Black,
                                    focusedTextColor = Color.Black
                                )
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                "Email",
                                fontWeight = FontWeight.Bold,
                                fontFamily = Inter,
                                fontSize = 14.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            OutlinedTextField(
                                value = editEmail,
                                onValueChange = { editEmail = it },
                                placeholder = { Text("Enter email", color = Color.Black) },
                                enabled = isEditing,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedPlaceholderColor = Color.Black,
                                    focusedPlaceholderColor = Color.Black,
                                    disabledPlaceholderColor = Color.Black,
                                    unfocusedBorderColor = Color.LightGray,
                                    focusedBorderColor = Purple,
                                    unfocusedTextColor = Color.Black,
                                    focusedTextColor = Color.Black
                                )
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                "Phone",
                                fontWeight = FontWeight.Bold,
                                fontFamily = Inter,
                                fontSize = 14.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            OutlinedTextField(
                                value = editPhone,
                                onValueChange = { editPhone = it },
                                placeholder = { Text("Enter phone number", color = Color.Black) },
                                enabled = isEditing,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedPlaceholderColor = Color.Black,
                                    focusedPlaceholderColor = Color.Black,
                                    disabledPlaceholderColor = Color.Black,
                                    unfocusedBorderColor = Color.LightGray,
                                    focusedBorderColor = Purple,
                                    unfocusedTextColor = Color.Black,
                                    focusedTextColor = Color.Black
                                )
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                "Address",
                                fontWeight = FontWeight.Bold,
                                fontFamily = Inter,
                                fontSize = 14.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            OutlinedTextField(
                                value = editAddress,
                                onValueChange = { editAddress = it },
                                placeholder = { Text("Enter address", color = Color.Black) },
                                enabled = isEditing,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedPlaceholderColor = Color.Black,
                                    focusedPlaceholderColor = Color.Black,
                                    disabledPlaceholderColor = Color.Black,
                                    unfocusedBorderColor = Color.LightGray,
                                    focusedBorderColor = Purple,
                                    unfocusedTextColor = Color.Black,
                                    focusedTextColor = Color.Black
                                )
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            if (!isGoogleUser) {
                                Text("Password", fontWeight = FontWeight.Bold, fontFamily = Inter, fontSize = 14.sp, color = Color.Gray)
                                OutlinedTextField(
                                    value = password,
                                    onValueChange = { password = it },
                                    placeholder = { Text("Enter your password", color = Color.Black) },
                                    modifier = Modifier.fillMaxWidth(),
                                    visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                                    colors = OutlinedTextFieldDefaults.colors(),
                                    textStyle = TextStyle(color = Color.Black)
                                )
                                Spacer(modifier = Modifier.height(12.dp))

                                Text("Confirm Password", fontWeight = FontWeight.Bold, fontFamily = Inter, fontSize = 14.sp, color = Color.Gray)
                                OutlinedTextField(
                                    value = confirmPassword,
                                    onValueChange = { confirmPassword = it },
                                    placeholder = { Text("Confirm your password", color = Color.Black) },
                                    modifier = Modifier.fillMaxWidth(),
                                    visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                                    colors = OutlinedTextFieldDefaults.colors(),
                                    textStyle = TextStyle(color = Color.Black)
                                )
                            }
                            if (errorMessage.isNotEmpty()) {
                                Text(errorMessage, color = LightRed, fontSize = 14.sp, modifier = Modifier.padding(top = 8.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(32.dp))
                        // Edit Profile and Logout Buttons in the same row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        errorMessage = ""
                                        // Password logic
                                        val newPassword = password.trim()
                                        val confirmNewPassword = confirmPassword.trim()
                                        var passwordToSend: String? = null
                                        if (newPassword.isNotEmpty() || confirmNewPassword.isNotEmpty()) {
                                            if (newPassword != confirmNewPassword) {
                                                errorMessage = "Passwords do not match."
                                                return@launch
                                            }
                                            if (!isValidPassword(newPassword)) {
                                                errorMessage = "Password must be at least 8 characters, include 1 uppercase letter, 1 number, and 1 special character."
                                                return@launch
                                            }
                                            passwordToSend = newPassword
                                        }
                                        // Prepare the user JSON
                                        val userJson = JSONObject().apply {
                                            if (editFirstName.isNotEmpty()) put("firstName", editFirstName)
                                            if (editLastName.isNotEmpty()) put("lastName", editLastName)
                                            if (editUsername.isNotEmpty()) put("username", editUsername)
                                            if (editEmail.isNotEmpty()) put("email", editEmail)
                                            if (editPhone.isNotEmpty()) put("phoneNumber", editPhone)
                                            if (editAddress.isNotEmpty()) put("address", editAddress)
                                            if (passwordToSend != null) put("password", passwordToSend)
                                        }.toString()

                                        // Create RequestBody for user JSON
                                        val userPart = userJson.toRequestBody("application/json".toMediaType())

                                        // Create MultipartBody.Part for profile picture if changed
                                        val profilePicturePart = if (selectedImageUri != null) {
                                            // Copy the content from the Uri to a temp file
                                            val inputStream = context.contentResolver.openInputStream(selectedImageUri!!)
                                            val tempFile = File.createTempFile("profile_picture", null, context.cacheDir)
                                            inputStream?.use { input ->
                                                tempFile.outputStream().use { output ->
                                                    input.copyTo(output)
                                                }
                                            }
                                            val mimeType = context.contentResolver.getType(selectedImageUri!!) ?: "image/*"
                                            val requestFile = tempFile.asRequestBody(mimeType.toMediaType())
                                            MultipartBody.Part.createFormData("profilePicture", tempFile.name, requestFile)
                                        } else {
                                            null
                                        }

                                        // Make the API call
                                        val response = userApi.updateUser(userId ?: 0L, userPart, profilePicturePart)
                                        // Update UI with new values
                                        if (editFirstName.isNotEmpty()) firstName = editFirstName
                                        if (editLastName.isNotEmpty()) lastName = editLastName
                                        if (editUsername.isNotEmpty()) username = editUsername
                                        if (editEmail.isNotEmpty()) email = editEmail
                                        if (editPhone.isNotEmpty()) phone = editPhone
                                        if (editAddress.isNotEmpty()) address = editAddress
                                        if (selectedImageUri != null) {
                                            isImageChanged = false
                                        }
                                        isEditing = false
                                        errorMessage = ""
                                        statusType = StatusType.Success
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Purple),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                            ) {
                                Text("Save Changes", color = Color.White, fontFamily = Inter, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Button(
                                onClick = {
                                    // Discard changes
                                    editFirstName = originalFirstName
                                    editLastName = originalLastName
                                    editUsername = originalUsername
                                    editEmail = originalEmail
                                    editPhone = originalPhone
                                    editAddress = originalAddress
                                    isEditing = false
                                    errorMessage = ""
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = LightRed),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                            ) {
                                Text("Discard", color = Color.White, fontFamily = Inter, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1)
                            }
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .statusBarsPadding()
                        .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 32.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.Start
                ) {
                    // Profile Picture Row (left-aligned)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(Purple)
                                .clickable(enabled = isEditing) {
                                    if (isEditing) {
                                        imagePickerLauncher.launch("image/*")
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            val showBitmap = if (isImageChanged && newProfileBitmap != null) newProfileBitmap else decodedProfileBitmap
                            if (showBitmap != null) {
                                androidx.compose.foundation.Image(
                                    bitmap = showBitmap.asImageBitmap(),
                                    contentDescription = "Profile Picture",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Profile Picture",
                                    tint = Color.Black,
                                    modifier = Modifier.size(80.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                    // User Info
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text("Name", fontWeight = FontWeight.Bold, fontFamily = Inter, fontSize = 14.sp, color = Color.Gray)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("$firstName $lastName", fontSize = 16.sp, color = Color.Black)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Username", fontWeight = FontWeight.Bold, fontFamily = Inter, fontSize = 14.sp, color = Color.Gray)
                        Text(username, fontSize = 16.sp, color = Color.Black, modifier = Modifier.padding(bottom = 16.dp))
                        Text("Email Address", fontWeight = FontWeight.Bold, fontFamily = Inter, fontSize = 14.sp, color = Color.Gray)
                        Text(email, fontSize = 16.sp, color = Color.Black, modifier = Modifier.padding(bottom = 16.dp))
                        Text("Phone Number", fontWeight = FontWeight.Bold, fontFamily = Inter, fontSize = 14.sp, color = Color.Gray)
                        Text(phone, fontSize = 16.sp, color = Color.Black, modifier = Modifier.padding(bottom = 16.dp))
                        Text("Address", fontWeight = FontWeight.Bold, fontFamily = Inter, fontSize = 14.sp, color = Color.Gray)
                        Text(address, fontSize = 16.sp, color = Color.Black, modifier = Modifier.padding(bottom = 16.dp))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    // Edit Profile and Logout Buttons in the same row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                // Enter edit mode, backup current values
                                originalFirstName = firstName
                                originalLastName = lastName
                                originalUsername = username
                                originalEmail = email
                                originalPhone = phone
                                originalAddress = address
                                isEditing = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Purple),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                        ) {
                            Text("Edit Profile", color = Color.White, fontFamily = Inter, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Button(
                            onClick = { showLogoutDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = LightRed.copy(alpha = 0.1f),
                                contentColor = LightRed
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Logout,
                                    contentDescription = "Logout",
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    "Logout",
                                    fontSize = 14.sp,
                                    fontFamily = Inter,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                    // My Pets Section
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "My Pets",
                                fontWeight = FontWeight.Bold,
                                fontFamily = Inter,
                                fontSize = 20.sp,
                                color = Purple
                            )
                            Button(
                                onClick = onAddPet,
                                colors = ButtonDefaults.buttonColors(containerColor = Purple),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("+ Add Pet", color = Color.White, fontFamily = Inter)
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        if (myPets.isEmpty()) {
                            Text(
                                "Have a pet you want to rehome? Click Add Pet to get started",
                                color = Color.Gray,
                                fontFamily = Inter,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 200.dp, max = 500.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(myPets.take(4)) { pet ->
                                    PetCard(
                                        pet = pet,
                                        currentUsername = currentUsernameValue,
                                        onOwnerClick = { onPetDetail(pet.id) },
                                        onPublicClick = { onAdoptPetDetail(pet.id, pet.name) }
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                    // Applications Section
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Applications",
                                fontWeight = FontWeight.Bold,
                                fontFamily = Inter,
                                fontSize = 20.sp,
                                color = Purple,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    if (showIncoming) "Incoming" else "Outgoing",
                                    fontFamily = Inter,
                                    fontSize = 14.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(end = 4.dp)
                                )
                                Switch(
                                    checked = showIncoming,
                                    onCheckedChange = { showIncoming = it },
                                    thumbContent = null
                                )
                            }
                        }

                        if (!showIncoming) {
                            // Outgoing applications (current behavior)
                            if (myApplications.isEmpty()) {
                                Text(
                                    "Ready to bring your new friend home?",
                                    color = Color.Gray,
                                    fontFamily = Inter,
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )
                                Button(
                                    onClick = onNavHome,
                                    colors = ButtonDefaults.buttonColors(containerColor = Purple),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Browse Pets", color = Color.White, fontFamily = Inter)
                                }
                            } else {
                                myApplications.forEach { application ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp)
                                            .align(Alignment.CenterHorizontally)
                                    ) {
                                        Card(
                                            shape = RoundedCornerShape(16.dp),
                                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA)),
                                            elevation = CardDefaults.cardElevation(2.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .align(Alignment.Center)
                                                .clickable { handleViewApplication(application.id, false) }
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(20.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Text(
                                                        text = "Adopt Application for ${application.petName}",
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 18.sp,
                                                        color = Color.Black
                                                    )
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Text(
                                                            text = "ID #${application.id}",
                                                            fontSize = 14.sp,
                                                            color = Color.Gray,
                                                            fontWeight = FontWeight.Medium
                                                        )
                                                        Spacer(modifier = Modifier.width(12.dp))
                                                        Text(
                                                            text = "STATUS: ${application.status.name.replaceFirstChar { it.uppercase() }}",
                                                            fontSize = 14.sp,
                                                            color = Color.Gray,
                                                            fontWeight = FontWeight.Medium
                                                        )
                                                    }
                                                }
                                                // Status dot
                                                val statusColor = when (application.status.name.lowercase()) {
                                                    "pending" -> Color(0xFFFFC107)
                                                    "success" -> Color(0xFF4CAF50)
                                                    "accepted" -> Color(0xFF4CAF50)
                                                    "rejected" -> Color(0xFFE53935)
                                                    else -> Color.LightGray
                                                }
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .size(14.dp)
                                                        .background(statusColor, shape = CircleShape)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            // Incoming applications (for user's pets)
                            if (incomingLoading) {
                                Text("Loading incoming applications...", color = Color.Gray, fontFamily = Inter)
                            } else if (incomingError.isNotEmpty()) {
                                Text("Error: $incomingError", color = Color.Red, fontFamily = Inter)
                            } else if (incomingApplications.isEmpty()) {
                                Text(
                                    "No incoming applications for your pets yet.",
                                    color = Color.Gray,
                                    fontFamily = Inter,
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )
                            } else {
                                incomingApplications.forEach { application ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp)
                                            .align(Alignment.CenterHorizontally)
                                    ) {
                                        Card(
                                            shape = RoundedCornerShape(16.dp),
                                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA)),
                                            elevation = CardDefaults.cardElevation(2.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .align(Alignment.Center)
                                                .clickable { handleViewApplication(application.id, true) }
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(20.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Text(
                                                        text = "Adopt Application for ${application.petName}",
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 18.sp,
                                                        color = Color.Black
                                                    )
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Text(
                                                            text = "ID #${application.id}",
                                                            fontSize = 14.sp,
                                                            color = Color.Gray,
                                                            fontWeight = FontWeight.Medium
                                                        )
                                                        Spacer(modifier = Modifier.width(12.dp))
                                                        Text(
                                                            text = "STATUS: ${application.status.name.replaceFirstChar { it.uppercase() }}",
                                                            fontSize = 14.sp,
                                                            color = Color.Gray,
                                                            fontWeight = FontWeight.Medium
                                                        )
                                                    }
                                                }
                                                // Status dot
                                                val statusColor = when (application.status.name.lowercase()) {
                                                    "pending" -> Color(0xFFFFC107)
                                                    "success" -> Color(0xFF4CAF50)
                                                    "accepted" -> Color(0xFF4CAF50)
                                                    "rejected" -> Color(0xFFE53935)
                                                    else -> Color.LightGray
                                                }
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .size(14.dp)
                                                        .background(statusColor, shape = CircleShape)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(120.dp)) // Add extra padding at the bottom for better scrolling
                }
            }
        }
        // Logout Confirmation Dialog
        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = {
                    Text(
                        "Logout",
                        fontFamily = Inter,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        "Are you sure you want to logout?",
                        fontFamily = Inter
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showLogoutDialog = false
                            // Clear Firebase auth state
                            auth.signOut()
                            googleSignInClient.signOut()
                            // Clear shared preferences
                            prefs.edit().clear().apply()
                            // Navigate to login screen
                            onLogout()
                        }
                    ) {
                        Text(
                            "Yes, Logout",
                            color = LightRed,
                            fontFamily = Inter,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showLogoutDialog = false }
                    ) {
                        Text(
                            "Cancel",
                            fontFamily = Inter
                        )
                    }
                }
            )
        }
    }
}

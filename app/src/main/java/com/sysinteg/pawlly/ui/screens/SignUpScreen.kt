package com.sysinteg.pawlly.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sysinteg.pawlly.R
import com.sysinteg.pawlly.ui.theme.Purple
import com.sysinteg.pawlly.ui.theme.White
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Divider
import androidx.compose.foundation.border
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileOutputStream
import com.sysinteg.pawlly.ui.theme.Inter
import androidx.compose.ui.text.font.FontWeight
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.zIndex
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.widget.Toast
import com.sysinteg.pawlly.GoogleSignInRequest
import com.sysinteg.pawlly.userApi
import com.sysinteg.pawlly.utils.Constants.PAWLLY_PREFS
import com.sysinteg.pawlly.utils.Constants.KEY_JWT_TOKEN
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.remember

enum class SignUpStatus {
    Pending, Submitted, Success, Failed, Expired, None
}

@Composable
fun StatusChip(status: SignUpStatus, message: String? = null, modifier: Modifier = Modifier) {
    val icon: ImageVector?
    val text: String
    val color: Color
    val textColor: Color
    when (status) {
        SignUpStatus.Pending -> {
            icon = Icons.Default.HourglassEmpty
            text = "Pending"
            color = Color(0xFFFFA726)
            textColor = Color(0xFFFFA726)
        }
        SignUpStatus.Submitted -> {
            icon = Icons.Default.Schedule
            text = "Submitted"
            color = Color(0xFF1976D2)
            textColor = Color(0xFF1976D2)
        }
        SignUpStatus.Success -> {
            icon = Icons.Default.CheckCircle
            text = "Success"
            color = Color(0xFF43A047)
            textColor = Color(0xFF43A047)
        }
        SignUpStatus.Failed -> {
            icon = Icons.Default.Cancel
            text = "Failed"
            color = Color(0xFFE53935)
            textColor = Color(0xFFE53935)
        }
        SignUpStatus.Expired -> {
            icon = Icons.Default.Error
            text = "Expired"
            color = Color(0xFFBDBDBD)
            textColor = Color(0xFF757575)
        }
        SignUpStatus.None -> {
            icon = null
            text = ""
            color = Color.Transparent
            textColor = Color.Transparent
        }
    }
    if (status != SignUpStatus.None) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .background(Color.White, RoundedCornerShape(50))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = text,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = message?.takeIf { it.isNotBlank() } ?: text,
                color = textColor,
                fontSize = 16.sp,
                maxLines = 2
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    onSignUpClick: (String, String, String, String, String, String?, String?, String, Uri?) -> Unit,
    onGoogleSignInClick: () -> Unit = {},
    onNavigateToLogin: () -> Unit,
    signUpStatus: androidx.compose.runtime.MutableState<SignUpStatus>,
    errorMessage: androidx.compose.runtime.MutableState<String>
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var showPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var showStatusChip by remember { mutableStateOf(false) }
    var googleSignInRequest by remember { mutableStateOf<GoogleSignInRequest?>(null) }
    var showPasswordInfo by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val systemUiController = rememberSystemUiController()
    val useDarkIcons = true
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = useDarkIcons
        )
    }

    // Handle Google Sign-In
    LaunchedEffect(googleSignInRequest) {
        googleSignInRequest?.let { request ->
            try {
                val response = userApi.googleSignIn(request)
                val token = response.token
                
                // Store token in SharedPreferences
                val prefs = context.getSharedPreferences(PAWLLY_PREFS, Context.MODE_PRIVATE)
                prefs.edit().putString(KEY_JWT_TOKEN, token).apply()
                
                Toast.makeText(context, "Sign up successful!", Toast.LENGTH_LONG).show()
                signUpStatus.value = SignUpStatus.Success
                errorMessage.value = ""
                onNavigateToLogin()
            } catch (e: Exception) {
                Log.e("SignUpScreen", "Google sign in failed", e)
                signUpStatus.value = SignUpStatus.Failed
                errorMessage.value = "Google sign in failed: ${e.message}"
                Toast.makeText(context, "Google sign in failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    val auth = FirebaseAuth.getInstance()

    // Configure Google Sign In
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()

    val googleSignInClient = GoogleSignIn.getClient(context, gso)

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Get the current user
                        val user = auth.currentUser
                        user?.let { firebaseUser ->
                            // Create Google sign-in request
                            googleSignInRequest = GoogleSignInRequest(
                                email = firebaseUser.email ?: "",
                                name = firebaseUser.displayName ?: "",
                                googleId = firebaseUser.uid
                            )
                        }
                    } else {
                        Log.e("SignUpScreen", "Firebase auth failed", task.exception)
                        signUpStatus.value = SignUpStatus.Failed
                        errorMessage.value = "Google sign in failed"
                        Toast.makeText(context, "Google sign in failed", Toast.LENGTH_LONG).show()
                    }
                }
        } catch (e: ApiException) {
            Log.e("SignUpScreen", "Google sign in failed", e)
            signUpStatus.value = SignUpStatus.Failed
            errorMessage.value = "Google sign in failed: ${e.message}"
            Toast.makeText(context, "Google sign in failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    // Helper function to check if a Uri is JPG or PNG
    fun isJpgOrPng(context: Context, uri: Uri): Boolean {
        val contentResolver = context.contentResolver
        val type = contentResolver.getType(uri)
        if (type == "image/jpeg" || type == "image/png") return true
        // Fallback: check file extension
        val name = uri.lastPathSegment?.lowercase() ?: ""
        return name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png")
    }

    // Image Picker
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            if (!isJpgOrPng(context, it)) {
                errorMessage.value = "Only JPG and PNG images are allowed as profile pictures."
                signUpStatus.value = SignUpStatus.Failed
                return@let
            }
            selectedImageUri = it
            // Save to local storage
            saveImageToLocalStorage(context, it)
        }
    }

    // Show chip for 3 seconds when status changes
    LaunchedEffect(signUpStatus.value) {
        if (signUpStatus.value != SignUpStatus.None) {
            showStatusChip = true
            delay(3000)
            showStatusChip = false
            signUpStatus.value = SignUpStatus.None
            errorMessage.value = ""
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        contentAlignment = Alignment.TopCenter
    ) {
        // Status chip centered below the status bar, always on top
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 4.dp)
                .zIndex(10f), // Ensure chip is on the topmost layer
            contentAlignment = Alignment.TopCenter
        ) {
            AnimatedVisibility(
                visible = showStatusChip && signUpStatus.value != SignUpStatus.None,
                enter = slideInVertically(
                    initialOffsetY = { fullHeight -> -fullHeight },
                    animationSpec = tween(durationMillis = 400)
                ),
                exit = slideOutVertically(
                    targetOffsetY = { fullHeight -> -fullHeight },
                    animationSpec = tween(durationMillis = 400)
                )
            ) {
                StatusChip(status = signUpStatus.value, message = if (signUpStatus.value == SignUpStatus.Failed) errorMessage.value else null)
            }
        }
        Image(
            painter = painterResource(id = R.drawable.splashbg),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .zIndex(-1f),
            contentScale = ContentScale.Crop
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 32.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logoiconpurpleround),
                contentDescription = "Pawlly Logo",
                modifier = Modifier
                    .size(90.dp)
                    .padding(top = 32.dp, bottom = 10.dp)
            )

            // Title
            Text(
                text = "Create your account to get started",
                color = White,
                fontFamily = Inter,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Profile Picture Upload (moved below title)
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(60.dp))
                    .border(2.dp, Purple, RoundedCornerShape(60.dp))
                    .background(White)
                    .clickable { imagePicker.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (selectedImageUri != null) {
                    Image(
                        bitmap = loadImageFromUri(context, selectedImageUri!!),
                        contentDescription = "Profile Picture",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = "Add Profile Picture",
                        tint = Purple,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
            Text(
                text = "Add Profile Picture",
                color = White,
                fontFamily = Inter,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp
            )

            // First Name
            TextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("First Name") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                textStyle = TextStyle(color = Color.Black),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            // Last Name
            TextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Last Name") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                textStyle = TextStyle(color = Color.Black),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            // Username
            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                textStyle = TextStyle(color = Color.Black),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            // Email
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                textStyle = TextStyle(color = Color.Black),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            // Phone Number
            TextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Phone Number") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                ),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                textStyle = TextStyle(color = Color.Black),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            // Address
            TextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Address") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                textStyle = TextStyle(color = Color.Black),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            // Password
            TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            textStyle = TextStyle(color = Color.Black),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .clip(RoundedCornerShape(8.dp)),
            trailingIcon = {
                if (password.isNotEmpty()) {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(
                            imageVector = if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (showPassword) "Hide password" else "Show password"
                        )
                    }
                }
            }
        )

            // Confirm Password
            TextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                textStyle = TextStyle(color = Color.Black),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
                    .clip(RoundedCornerShape(8.dp)),
                trailingIcon = {
                    if (confirmPassword.isNotEmpty()) {
                        IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                            Icon(
                                imageVector = if (showConfirmPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = if (showConfirmPassword) "Hide password" else "Show password"
                            )
                        }
                    }
                }
            )

            Button(
                onClick = {
                    try {
                        Log.d("SignUpScreen", "Password before sending: $password")
                        Log.d("SignUpScreen", "All fields: firstName=$firstName, lastName=$lastName, username=$username, email=$email, password=$password, confirmPassword=$confirmPassword, phoneNumber=$phoneNumber, address=$address")
                        val passwordPattern = Regex("^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#${'$'}%^&*()_+={};':\"|,.<>/?-]).{8,}")
                        if (!passwordPattern.containsMatchIn(password)) {
                            signUpStatus.value = SignUpStatus.Failed
                            errorMessage.value = "Password must be at least 8 characters, include 1 uppercase letter, 1 number, and 1 special character."
                            return@Button
                        }
                        Log.d("SignUpActivity", "Sign up button clicked")
                        if (firstName.isNotEmpty() && lastName.isNotEmpty() && 
                            username.isNotEmpty() && email.isNotEmpty() && 
                            password.isNotEmpty() && confirmPassword.isNotEmpty() &&
                            phoneNumber.isNotEmpty() && address.isNotEmpty()) {
                            isLoading = true
                            signUpStatus.value = SignUpStatus.Submitted
                            errorMessage.value = ""
                            onSignUpClick(
                                firstName,
                                lastName,
                                username,
                                email,
                                password,
                                phoneNumber,
                                address,
                                confirmPassword,
                                selectedImageUri
                            )
                        } else if (password != confirmPassword) {
                            Log.d("SignUpActivity", "Validation failed: Passwords do not match")
                            signUpStatus.value = SignUpStatus.Failed
                            errorMessage.value = "Failed: Passwords do not match"
                        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                            Log.d("SignUpActivity", "Validation failed: Invalid email address")
                            signUpStatus.value = SignUpStatus.Failed
                            errorMessage.value = "Failed: Invalid email address"
                        } else if (password.length < 8) {
                            Log.d("SignUpActivity", "Validation failed: Password too short")
                            signUpStatus.value = SignUpStatus.Failed
                            errorMessage.value = "Failed: Password too short"
                        } else {
                            Log.d("SignUpActivity", "Validation failed: Missing fields")
                            signUpStatus.value = SignUpStatus.Failed
                            errorMessage.value = "Failed: Missing fields"
                        }
                    } catch (e: Exception) {
                        Log.e("SignUpScreen", "Exception in Sign Up button", e)
                        Toast.makeText(context, "Crash: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Purple
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
                    .height(44.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Sign Up")
                }
            }

            // Make the entire text clickable and add margin bottom
            Text(
                text = "Already have an account? Sign in",
                color = Color.White,
                fontSize = 14.sp,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 32.dp)
                    .clickable(onClick = onNavigateToLogin),
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun saveImageToLocalStorage(context: Context, uri: Uri) {
    try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.filesDir, "profile_picture.jpg")
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

private fun loadImageFromUri(context: Context, uri: Uri): ImageBitmap {
    val inputStream = context.contentResolver.openInputStream(uri)
    val bitmap = BitmapFactory.decodeStream(inputStream)
    inputStream?.close()
    return bitmap.asImageBitmap()
} 
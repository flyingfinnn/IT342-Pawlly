package com.sysinteg.pawlly.ui.screens

import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.sysinteg.pawlly.R
import com.sysinteg.pawlly.ui.theme.Purple
import com.sysinteg.pawlly.ui.theme.White
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.foundation.clickable
import androidx.compose.ui.text.font.FontWeight
import com.sysinteg.pawlly.ui.theme.Inter
import androidx.compose.ui.layout.ContentScale
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.ui.zIndex
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import com.sysinteg.pawlly.utils.Constants.KEY_REMEMBER_ME
import com.sysinteg.pawlly.utils.Constants.KEY_SAVED_EMAIL
import com.sysinteg.pawlly.utils.Constants.PAWLLY_PREFS
import com.sysinteg.pawlly.utils.Constants.KEY_JWT_TOKEN
import com.sysinteg.pawlly.userApi
import com.sysinteg.pawlly.GoogleSignInRequest
import com.sysinteg.pawlly.navigation.Screen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.util.Log
import android.widget.Toast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginClick: (String, String) -> Unit,
    onForgotPasswordClick: () -> Unit,
    onSignUpClick: () -> Unit,
    onGoogleSignInClick: () -> Unit,
    showSuccessChip: Boolean = false,
    navController: NavController
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences(PAWLLY_PREFS, Context.MODE_PRIVATE) }
    
    var email by remember { mutableStateOf(prefs.getString(KEY_SAVED_EMAIL, "") ?: "") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var showPassword by remember { mutableStateOf(false) }
    var showChip by remember { mutableStateOf(showSuccessChip) }
    var rememberMe by remember { mutableStateOf(prefs.getBoolean(KEY_REMEMBER_ME, false)) }
    var googleSignInRequest by remember { mutableStateOf<GoogleSignInRequest?>(null) }
    
    // Handle loading state reset
    LaunchedEffect(isLoading) {
        if (isLoading) {
            delay(1000) // Wait for potential error response
            isLoading = false
        }
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
                
                Toast.makeText(context, "Login successful!", Toast.LENGTH_LONG).show()
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            } catch (e: Exception) {
                Log.e("LoginScreen", "Google sign in failed", e)
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
                        Log.e("LoginScreen", "Firebase auth failed", task.exception)
                        Toast.makeText(context, "Google sign in failed", Toast.LENGTH_LONG).show()
                    }
                }
        } catch (e: ApiException) {
            Log.e("LoginScreen", "Google sign in failed", e)
            Toast.makeText(context, "Google sign in failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    val systemUiController = rememberSystemUiController()
    val useDarkIcons = true // Black icons for light background
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = useDarkIcons
        )
    }

    // Auto-hide after 3 seconds
    LaunchedEffect(showChip) {
        if (showChip) {
            kotlinx.coroutines.delay(3000)
            showChip = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.splashbg),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .zIndex(-1f),
            contentScale = ContentScale.Crop
        )
        // Success chip in top right
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, end = 16.dp),
            contentAlignment = Alignment.TopEnd
        ) {
            AnimatedVisibility(visible = showChip) {
                StatusChip(status = SignUpStatus.Success)
            }
        }
        // Main content column moved up by 30px
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .widthIn(max = 300.dp)
                .offset(y = (-30).dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logoiconpurpleround),
                contentDescription = "Pawlly Logo",
                modifier = Modifier
                    .size(80.dp)
                    .padding(bottom = 8.dp)
            )

            // Title
            Text(
                text = "Welcome to Pawlly",
                color = White,
                fontFamily = Inter,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )

            // Subtitle
            Text(
                text = "Login to your account to get started.",
                color = Color(0xFFB0B0B0), // grey
                fontFamily = Inter,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email", fontFamily = Inter, fontWeight = FontWeight.Normal) },
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
                    .clip(RoundedCornerShape(6.dp))
            )

            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password", fontFamily = Inter, fontWeight = FontWeight.Normal) },
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
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
                    .clip(RoundedCornerShape(6.dp)),
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

            // Row for Remember Me and Forgot Password
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Remember Me section
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 2.dp)
                ) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = { 
                            rememberMe = it
                            prefs.edit().putBoolean(KEY_REMEMBER_ME, it).apply()
                            if (!it) {
                                prefs.edit().remove(KEY_SAVED_EMAIL).apply()
                            }
                        },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Purple,
                            uncheckedColor = White,
                            checkmarkColor = White
                        ),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Remember me",
                        color = White,
                        fontFamily = Inter,
                        fontWeight = FontWeight.Normal,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(start = 2.dp)
                    )
                }
                
                // Forgot Password text
                Text(
                    text = "Forgot password?",
                    color = White,
                    fontFamily = Inter,
                    fontWeight = FontWeight.Normal,
                    fontSize = 11.sp,
                    modifier = Modifier
                        .clickable(onClick = onForgotPasswordClick)
                        .padding(end = 4.dp)
                )
            }

            Button(
                onClick = {
                    if (email.isNotEmpty() && password.isNotEmpty()) {
                        isLoading = true
                        if (rememberMe) {
                            prefs.edit().putString(KEY_SAVED_EMAIL, email).apply()
                        }
                        onLoginClick(email, password)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Purple
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Login", fontFamily = Inter, fontWeight = FontWeight.Normal)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp, bottom = 2.dp)
            ) {
                Divider(
                    color = Color(0xFFAAAAAA),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "No account yet? Sign up or",
                    color = White,
                    fontSize = 12.sp,
                    fontFamily = Inter,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.clickable(onClick = onSignUpClick),
                    style = TextStyle(textDecoration = TextDecoration.Underline)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Divider(
                    color = Color(0xFFAAAAAA),
                    modifier = Modifier.weight(1f)
                )
            }

            OutlinedButton(
                onClick = {
                    val signInIntent = googleSignInClient.signInIntent
                    googleSignInLauncher.launch(signInIntent)
                },
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                border = BorderStroke(1.dp, Color.Black),
                modifier = Modifier
                    .width(240.dp)
                    .clip(RoundedCornerShape(6.dp))
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_google_logo),
                    contentDescription = "Google Logo",
                    modifier = Modifier.size(18.dp),
                    tint = Color.Unspecified
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    "Sign in with Google",
                    fontFamily = Inter,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    maxLines = 1
                )
            }
        }
    }
} 
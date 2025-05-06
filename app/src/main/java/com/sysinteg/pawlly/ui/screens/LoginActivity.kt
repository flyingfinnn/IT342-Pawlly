package com.sysinteg.pawlly.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.sysinteg.pawlly.ui.theme.PawllyTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.view.WindowCompat
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.sysinteg.pawlly.userApi
import com.sysinteg.pawlly.LoginRequest
import android.content.Context
import android.content.Intent
import com.sysinteg.pawlly.utils.Constants.PAWLLY_PREFS
import com.sysinteg.pawlly.utils.Constants.KEY_JWT_TOKEN
import com.sysinteg.pawlly.ui.screens.HomeActivity
import android.util.Log

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            PawllyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val showSuccessChip = intent?.getBooleanExtra("showSuccessChip", false) ?: false
                    val context = this@LoginActivity
                    LoginScreen(
                        navController = navController,
                        onLoginClick = { email, password ->
                            lifecycleScope.launch(Dispatchers.IO) {
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
                                        // Clear any existing token first
                                        val prefs = context.getSharedPreferences(PAWLLY_PREFS, Context.MODE_PRIVATE)
                                        prefs.edit().remove(KEY_JWT_TOKEN).apply()

                                        val loginRequest = LoginRequest(
                                            email = email.trim(),
                                            password = password.trim()
                                        )
                                        
                                        Log.d("LoginActivity", "Attempting login with email: ${loginRequest.email}")
                                        val loginResponse = userApi.login(loginRequest)
                                        val token = loginResponse.token
                                        
                                        // Store token in SharedPreferences
                                        prefs.edit()
                                            .putString(KEY_JWT_TOKEN, token)
                                            .apply()
                                        // Fetch user details to get user_id
                                        val user = userApi.getMe("Bearer $token")
                                        prefs.edit().putLong("user_id", user.userId ?: 0L).apply()
                                        
                                        // Verify token was stored correctly
                                        val storedToken = prefs.getString(KEY_JWT_TOKEN, null)
                                        if (storedToken == null || storedToken != token) {
                                            throw Exception("Failed to store authentication token")
                                        }
                                        
                                        withContext(Dispatchers.Main) {
                                            Toast.makeText(context, "Login successful!", Toast.LENGTH_LONG).show()
                                            // Navigate to Home
                                            startActivity(Intent(context, HomeActivity::class.java))
                                            finish()
                                        }
                                    }
                                } catch (e: Exception) {
                                    Log.e("LoginActivity", "Login failed", e)
                                    withContext(Dispatchers.Main) {
                                        val errorMessage = when {
                                            e.message?.contains("401") == true -> "Invalid email or password"
                                            e.message?.contains("500") == true -> {
                                                Log.e("LoginActivity", "Server error details: ${e.message}")
                                                "Server error. Please try again later. If the problem persists, contact support."
                                            }
                                            e.message?.contains("Failed to store") == true -> "Authentication error. Please try again"
                                            e.message?.contains("Failed to connect") == true -> "Server connection error. Please check your internet connection"
                                            e.message?.contains("timeout") == true -> "Connection timeout. Please check your internet connection"
                                            e.message?.contains("SSL") == true -> "Secure connection error. Please check your internet connection"
                                            else -> {
                                                Log.e("LoginActivity", "Unexpected error: ${e.message}")
                                                "Login failed. Please try again later."
                                            }
                                        }
                                        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                        },
                        onForgotPasswordClick = { /* TODO: handle forgot password */ },
                        onSignUpClick = { /* TODO: handle sign up */ },
                        onGoogleSignInClick = { /* TODO: handle Google sign in */ },
                        showSuccessChip = showSuccessChip
                    )
                }
            }
        }
    }
} 
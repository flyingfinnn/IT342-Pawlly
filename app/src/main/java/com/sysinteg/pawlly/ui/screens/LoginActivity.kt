package com.sysinteg.pawlly.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.sysinteg.pawlly.ui.screens.LoginScreen
import com.sysinteg.pawlly.ui.theme.PawllyTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.view.WindowCompat

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
                    LoginScreen(
                        onLoginClick = { email, password ->
                            // Handle login
                        },
                        onForgotPasswordClick = {
                            // Handle forgot password
                        },
                        onSignUpClick = {
                            // Handle sign up
                        },
                        onGoogleSignInClick = {
                            // Handle Google sign in
                        }
                    )
                }
            }
        }
    }
} 
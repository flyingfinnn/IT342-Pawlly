package com.sysinteg.pawlly.ui.screens

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.sysinteg.pawlly.ui.screens.SignUpScreen
import com.sysinteg.pawlly.ui.theme.PawllyTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.view.WindowCompat

@AndroidEntryPoint
class SignUpActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            PawllyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SignUpScreen(
                        onSignUpClick = { firstName, lastName, username, email, password, phoneNumber, address, confirmPassword, profilePictureUri ->
                            // TODO: Implement signup logic
                            // For now, just finish the activity
                            finish()
                        },
                        onGoogleSignInClick = {
                            // TODO: Implement Google sign in
                        },
                        onNavigateToLogin = {
                            finish()
                        }
                    )
                }
            }
        }
    }
} 
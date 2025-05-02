package com.sysinteg.pawlly.ui.screens

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.sysinteg.pawlly.ui.theme.PawllyTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.view.WindowCompat

@AndroidEntryPoint
class HomeActivity : ComponentActivity() {
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val logoutState by viewModel.logoutState.collectAsState()

            LaunchedEffect(logoutState) {
                when (logoutState) {
                    is LogoutState.Success -> {
                        Toast.makeText(this@HomeActivity, "Signed out successfully", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@HomeActivity, LoginActivity::class.java))
                        finish()
                    }
                    is LogoutState.Error -> {
                        Toast.makeText(this@HomeActivity, (logoutState as LogoutState.Error).message, Toast.LENGTH_SHORT).show()
                    }
                    else -> {}
                }
            }

            PawllyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AdoptScreen(
                        onBrowseAll = {},
                        onPetClick = {},
                        onFilterClick = {},
                        onLostFoundClick = {},
                        onNavHome = {},
                        onNavNotifications = {},
                        onNavProfile = {}
                    )
                }
            }
        }
    }
} 
package com.sysinteg.pawlly.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sysinteg.pawlly.ui.theme.Purple

@Composable
fun PawllyNavBar(
    selectedScreen: String,
    onNavHome: () -> Unit,
    onNavNotifications: () -> Unit,
    onNavProfile: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(bottom = 32.dp)
            .widthIn(max = 340.dp)
            .shadow(12.dp, RoundedCornerShape(24.dp), clip = false)
            .clip(RoundedCornerShape(24.dp))
            .background(Purple)
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            NavBarItem(
                icon = Icons.Default.Home,
                label = "Home",
                selected = selectedScreen == "Home",
                onClick = onNavHome,
                modifier = Modifier.weight(1f),
                selectedColor = Color.White,
                unselectedColor = Color.White.copy(alpha = 0.6f)
            )
            NavBarItem(
                icon = Icons.Default.Notifications,
                label = "Notifications",
                selected = selectedScreen == "Notifications",
                onClick = onNavNotifications,
                modifier = Modifier.weight(1f),
                selectedColor = Color.White,
                unselectedColor = Color.White.copy(alpha = 0.6f)
            )
            NavBarItem(
                icon = Icons.Default.Person,
                label = "Profile",
                selected = selectedScreen == "Profile",
                onClick = onNavProfile,
                modifier = Modifier.weight(1f),
                selectedColor = Color.White,
                unselectedColor = Color.White.copy(alpha = 0.6f)
            )
        }
    }
} 
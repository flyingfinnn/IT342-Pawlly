package com.sysinteg.pawlly.ui.screens

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.sysinteg.pawlly.R
import com.sysinteg.pawlly.ui.theme.Inter
import com.sysinteg.pawlly.ui.theme.Purple
import com.sysinteg.pawlly.ui.theme.BoneWhite
import com.sysinteg.pawlly.ui.screens.PawllyNavBar
import androidx.compose.foundation.layout.statusBarsPadding
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import androidx.compose.runtime.SideEffect
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.TextFieldDefaults

val LightRed = Color(0xFFFF6B6B)

private const val PROFILE_PREFS = "profile_prefs"
private const val KEY_NAME = "name"
private const val KEY_USERNAME = "username"
private const val KEY_EMAIL = "email"
private const val KEY_PHONE = "phone"
private const val KEY_ADDRESS = "address"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onEditProfile: () -> Unit = {},
    onLogout: () -> Unit = {},
    onNavHome: () -> Unit = {},
    onNavNotifications: () -> Unit = {},
    onNavProfile: () -> Unit = {},
    selectedScreen: String = "Profile"
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences(PROFILE_PREFS, Context.MODE_PRIVATE) }
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser

    // Load initial values from SharedPreferences or Firebase
    var isEditing by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf(prefs.getString(KEY_NAME, user?.displayName ?: "") ?: "") }
    var username by remember { mutableStateOf(prefs.getString(KEY_USERNAME, user?.email?.substringBefore("@") ?: "") ?: "") }
    var email by remember { mutableStateOf(prefs.getString(KEY_EMAIL, user?.email ?: "") ?: "") }
    var phone by remember { mutableStateOf(prefs.getString(KEY_PHONE, user?.phoneNumber ?: "") ?: "") }
    var address by remember { mutableStateOf(prefs.getString(KEY_ADDRESS, "") ?: "") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    val systemUiController = rememberSystemUiController()
    val useDarkIcons = true
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = useDarkIcons
        )
    }

    fun saveProfile() {
        if (password.isNotEmpty() && password != confirmPassword) {
            errorMessage = "Passwords do not match"
            return
        }
        prefs.edit()
            .putString(KEY_NAME, name)
            .putString(KEY_USERNAME, username)
            .putString(KEY_EMAIL, email)
            .putString(KEY_PHONE, phone)
            .putString(KEY_ADDRESS, address)
            .apply()
        errorMessage = ""
        isEditing = false
        password = ""
        confirmPassword = ""
    }

    fun discardChanges() {
        name = prefs.getString(KEY_NAME, user?.displayName ?: "") ?: ""
        username = prefs.getString(KEY_USERNAME, user?.email?.substringBefore("@") ?: "") ?: ""
        email = prefs.getString(KEY_EMAIL, user?.email ?: "") ?: ""
        phone = prefs.getString(KEY_PHONE, user?.phoneNumber ?: "") ?: ""
        address = prefs.getString(KEY_ADDRESS, "") ?: ""
        password = ""
        confirmPassword = ""
        errorMessage = ""
        isEditing = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .background(BoneWhite)
    ) {
        // Blurred/semi-transparent status bar overlay
        Box(
            Modifier
                .fillMaxWidth()
                .height(WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
                .background(BoneWhite.copy(alpha = 0.7f))
        )
        if (!isEditing) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Picture
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Purple),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile Picture",
                        tint = Color.White,
                        modifier = Modifier.size(80.dp)
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
                // User Info
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text("Name", fontWeight = FontWeight.Bold, fontFamily = Inter, fontSize = 14.sp, color = Color.Gray)
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(),
                        textStyle = TextStyle(color = Color.Black)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Username", fontWeight = FontWeight.Bold, fontFamily = Inter, fontSize = 14.sp, color = Color.Gray)
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Username") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(),
                        textStyle = TextStyle(color = Color.Black)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Email Address", fontWeight = FontWeight.Bold, fontFamily = Inter, fontSize = 14.sp, color = Color.Gray)
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(),
                        textStyle = TextStyle(color = Color.Black)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Phone Number", fontWeight = FontWeight.Bold, fontFamily = Inter, fontSize = 14.sp, color = Color.Gray)
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone Number") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(),
                        textStyle = TextStyle(color = Color.Black)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Address", fontWeight = FontWeight.Bold, fontFamily = Inter, fontSize = 14.sp, color = Color.Gray)
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Address") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(),
                        textStyle = TextStyle(color = Color.Black)
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = { isEditing = true },
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
                        onClick = onLogout,
                        colors = ButtonDefaults.buttonColors(containerColor = LightRed),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Text("Logout", color = Color.White, fontFamily = Inter, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 80.dp) // Add padding for the navigation bar
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 32.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Profile Picture
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(Purple),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile Picture",
                            tint = Color.White,
                            modifier = Modifier.size(80.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                    // Edit Form
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start
                    ) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Name") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.outlinedTextFieldColors(),
                            textStyle = TextStyle(color = Color.Black)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it },
                            label = { Text("Username") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.outlinedTextFieldColors(),
                            textStyle = TextStyle(color = Color.Black)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email Address") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.outlinedTextFieldColors(),
                            textStyle = TextStyle(color = Color.Black)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Phone Number") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.outlinedTextFieldColors(),
                            textStyle = TextStyle(color = Color.Black)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = address,
                            onValueChange = { address = it },
                            label = { Text("Address") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.outlinedTextFieldColors(),
                            textStyle = TextStyle(color = Color.Black)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password") },
                            modifier = Modifier.fillMaxWidth(),
                            visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                            colors = TextFieldDefaults.outlinedTextFieldColors(),
                            textStyle = TextStyle(color = Color.Black)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = { Text("Confirm Password") },
                            modifier = Modifier.fillMaxWidth(),
                            visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                            colors = TextFieldDefaults.outlinedTextFieldColors(),
                            textStyle = TextStyle(color = Color.Black)
                        )
                        if (errorMessage.isNotEmpty()) {
                            Text(errorMessage, color = LightRed, fontSize = 14.sp, modifier = Modifier.padding(top = 8.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 50.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            onClick = { saveProfile() },
                            colors = ButtonDefaults.buttonColors(containerColor = Purple),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                        ) {
                            Text("Save Changes", color = Color.White, fontFamily = Inter, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Button(
                            onClick = { discardChanges() },
                            colors = ButtonDefaults.buttonColors(containerColor = LightRed),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                        ) {
                            Text("Discard", color = Color.White, fontFamily = Inter, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
        // Navigation Bar (bottom)
        Box(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            PawllyNavBar(
                selectedScreen = "Profile",
                onNavHome = onNavHome,
                onNavNotifications = onNavNotifications,
                onNavProfile = onNavProfile
            )
        }
    }
} 
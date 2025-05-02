package com.sysinteg.pawlly.ui.screens

import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.sysinteg.pawlly.ui.screens.SignUpScreen
import com.sysinteg.pawlly.ui.theme.PawllyTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.view.WindowCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStream
import com.sysinteg.pawlly.UserSignupRequest
import com.sysinteg.pawlly.userApi
import android.content.Intent
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import android.util.Log
import androidx.lifecycle.lifecycleScope
import java.io.File
import okhttp3.RequestBody.Companion.asRequestBody

@AndroidEntryPoint
class SignUpActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            PawllyTheme {
                val signUpStatus = remember { mutableStateOf(SignUpStatus.None) }
                val signUpErrorMessage = remember { mutableStateOf("") }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SignUpScreen(
                        onSignUpClick = { firstName, lastName, username, email, password, phoneNumber, address, confirmPassword, profilePictureUri ->
                            val context = this
                            Toast.makeText(context, "Button click reached", Toast.LENGTH_SHORT).show()
                            Log.d("SignUpActivity", "Button click reached, about to launch coroutine")
                            try {
                                lifecycleScope.launch(Dispatchers.IO) {
                                    Log.d("SignUpActivity", "Coroutine started")
                                    try {
                                        Log.d("SignUpActivity", "About to call userApi.signUp with username: $username")
                                        
                                        // Create a JSON object for the user data
                                        val userJson = JSONObject().apply {
                                            put("username", username)
                                            put("firstName", firstName)
                                            put("lastName", lastName)
                                            put("email", email)
                                            put("password", password)
                                            put("address", address)
                                            put("phoneNumber", phoneNumber)
                                        }.toString()

                                        // Create the user part of the multipart request
                                        val userPart = userJson.toRequestBody("application/json".toMediaTypeOrNull())

                                        // Handle profile picture if provided
                                        var profilePicturePart: MultipartBody.Part? = null
                                        profilePictureUri?.let { uri ->
                                            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                                                val file = File(context.cacheDir, "profile_picture")
                                                file.outputStream().use { outputStream ->
                                                    inputStream.copyTo(outputStream)
                                                }
                                                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                                                profilePicturePart = MultipartBody.Part.createFormData(
                                                    "profilePicture",
                                                    "profile_picture.jpg",
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
                                        
                                        runOnUiThread {
                                            Toast.makeText(context, "Signup successful!", Toast.LENGTH_LONG).show()
                                            signUpStatus.value = SignUpStatus.Success
                                            signUpErrorMessage.value = ""
                                            val intent = Intent(context, LoginActivity::class.java)
                                            intent.putExtra("showSuccessChip", true)
                                            context.startActivity(intent)
                                            finish()
                                        }
                                    } catch (e: Exception) {
                                        Log.e("SignUpActivity", "Sign up error", e)
                                        runOnUiThread {
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
                            } catch (e: Exception) {
                                Log.e("SignUpActivity", "Error launching coroutine", e)
                                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        },
                        onGoogleSignInClick = {
                            // TODO: Implement Google sign in
                        },
                        onNavigateToLogin = {
                            finish()
                        },
                        signUpStatus = signUpStatus,
                        errorMessage = signUpErrorMessage
                    )
                }
            }
        }
    }
} 
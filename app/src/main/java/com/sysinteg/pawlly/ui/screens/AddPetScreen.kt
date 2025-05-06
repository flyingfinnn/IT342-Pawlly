package com.sysinteg.pawlly.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.sysinteg.pawlly.R
import com.sysinteg.pawlly.ui.theme.Inter
import com.sysinteg.pawlly.ui.theme.Purple
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.text.KeyboardOptions
import com.sysinteg.pawlly.ui.screens.StatusType
import com.sysinteg.pawlly.ui.screens.AnimatedStatusChip
import kotlinx.coroutines.delay
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import com.sysinteg.pawlly.userApi
import com.sysinteg.pawlly.getAuthToken
import com.sysinteg.pawlly.UserResponse
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.CircularProgressIndicator
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import java.io.ByteArrayOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPetScreen(
    onPetAdded: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var breed by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var contactNumber by remember { mutableStateOf("") }
    var userName by remember { mutableStateOf("") }
    var photoUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var genderExpanded by remember { mutableStateOf(false) }
    var statusExpanded by remember { mutableStateOf(false) }
    var typeExpanded by remember { mutableStateOf(false) }
    var statusType by remember { mutableStateOf(StatusType.None) }
    var showStatusChip by remember { mutableStateOf(false) }
    var showImageDialog by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val genderOptions = listOf("Male", "Female")
    val statusOptions = listOf("Available", "Not Available")
    val typeOptions = listOf("Dog", "Cat")
    val submissionData = "2024-06-01"
    val photoPickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null && photoUris.size < 4) {
            photoUris = photoUris + uri
        }
    }

    // --- Breed Auto-suggest ---
    val allBreeds = listOf(
        "Labrador Retriever",
        "German Shepherd",
        "Golden Retriever",
        "Bulldog",
        "Poodle (including standard, miniature, and toy)",
        "Beagle",
        "Rottweiler",
        "Yorkshire Terrier",
        "Boxer",
        "Dachshund",
        "Siberian Husky",
        "Great Dane",
        "Doberman Pinscher",
        "Australian Shepherd",
        "Shih Tzu",
        "Chihuahua",
        "Cavalier King Charles Spaniel",
        "French Bulldog",
        "Border Collie",
        "Pembroke Welsh Corgi",
        // Cat breeds (optional, can be expanded)
        "Persian", "Maine Coon", "Siamese", "Ragdoll", "Bengal", "Sphynx", "British Shorthair", "Abyssinian", "Birman", "Oriental"
    )
    var breedSuggestions by remember { mutableStateOf(listOf<String>()) }
    var breedDropdownExpanded by remember { mutableStateOf(false) }

    // --- Autofill user info ---
    LaunchedEffect(Unit) {
        try {
            val token = getAuthToken(context)
            val user: UserResponse? = userApi.getMe(token)
            user?.let {
                address = it.address ?: ""
                contactNumber = it.phoneNumber ?: ""
                userName = it.username ?: ""
            }
        } catch (_: Exception) {}
    }

    LaunchedEffect(breed) {
        if (breed.length >= 4) {
            breedSuggestions = allBreeds.filter { it.contains(breed, ignoreCase = true) && it != breed }
            breedDropdownExpanded = breedSuggestions.isNotEmpty()
        } else {
            breedSuggestions = emptyList()
            breedDropdownExpanded = false
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

    var isSubmitting by remember { mutableStateOf(false) }
    var submitError by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            // No TopAppBar, title, or subtitle
        },
        containerColor = Color(0xFFFAF9F6)
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            // Status chip in top right
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, end = 16.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                AnimatedStatusChip(visible = showStatusChip, status = statusType)
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 0.dp)
                    .verticalScroll(rememberScrollState())
                    .statusBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Add Pet Title
                Text(
                    text = "Add Pet",
                    fontWeight = FontWeight.Bold,
                    fontFamily = Inter,
                    color = Purple,
                    fontSize = 28.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 0.dp, bottom = 8.dp)
                )
                // Photo Upload Carousel
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Upload Photos (Max 4)",
                        fontWeight = FontWeight.Bold,
                        fontFamily = Inter,
                        fontSize = 16.sp,
                        color = Purple,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Photo slots
                        repeat(4) { index ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .background(Color.White, RoundedCornerShape(12.dp))
                                    .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                                    .clickable {
                                        if (index < photoUris.size) {
                                            selectedImageUri = photoUris[index]
                                            showImageDialog = true
                                        } else if (photoUris.size < 4) {
                                            photoPickerLauncher.launch("image/*")
                                        }
                                    }
                            ) {
                                if (index < photoUris.size) {
                                    AsyncImage(
                                        model = photoUris[index],
                                        contentDescription = "Pet Photo ${index + 1}",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                    // Delete button overlay
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(4.dp)
                                            .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                            .clickable {
                                                photoUris = photoUris.toMutableList().apply {
                                                    removeAt(index)
                                                }
                                            }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Delete Photo",
                                            tint = Color.White,
                                            modifier = Modifier
                                                .size(24.dp)
                                                .padding(4.dp)
                                        )
                                    }
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add Photo",
                                        tint = Color.Gray,
                                        modifier = Modifier
                                            .size(32.dp)
                                            .align(Alignment.Center)
                                    )
                                }
                            }
                        }
                    }
                    // Fullscreen image overlay
                    if (showImageDialog && selectedImageUri != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.6f))
                                .clickable { showImageDialog = false },
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .clickable(enabled = false) {}, // Prevents closing when clicking the image
                                contentAlignment = Alignment.Center
                            ) {
                                AsyncImage(
                                    model = selectedImageUri,
                                    contentDescription = "Full Image",
                                    modifier = Modifier
                                        .fillMaxWidth(0.9f)
                                        .aspectRatio(1f),
                                    contentScale = ContentScale.Fit
                                )
                            }
                        }
                    }
                    if (photoUris.isNotEmpty()) {
                        Text(
                            "${photoUris.size}/4 photos uploaded",
                            color = Color.Gray,
                            fontFamily = Inter,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }

                // Pet Details Section
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        "Pet Details",
                        fontWeight = FontWeight.Bold,
                        fontFamily = Inter,
                        fontSize = 16.sp,
                        color = Purple,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = Purple,
                            unfocusedBorderColor = Color.Gray
                        )
                    )
                    ExposedDropdownMenuBox(
                        expanded = breedDropdownExpanded,
                        onExpandedChange = { expanded ->
                            if (breed.length >= 4 && breedSuggestions.isNotEmpty()) {
                                breedDropdownExpanded = expanded
                            }
                        }
                    ) {
                        OutlinedTextField(
                            value = breed,
                            onValueChange = {
                                breed = it
                            },
                            label = { Text("Breed") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = breedDropdownExpanded)
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                focusedBorderColor = Purple,
                                unfocusedBorderColor = Color.Gray
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = breedDropdownExpanded,
                            onDismissRequest = { breedDropdownExpanded = false }
                        ) {
                            breedSuggestions.forEach { suggestion ->
                                DropdownMenuItem(
                                    text = { Text(suggestion) },
                                    onClick = {
                                        breed = suggestion
                                        breedDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    // Gender Dropdown
                    ExposedDropdownMenuBox(
                        expanded = genderExpanded,
                        onExpandedChange = { genderExpanded = !genderExpanded }
                    ) {
                        OutlinedTextField(
                            value = gender,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Gender") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = genderExpanded) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                focusedBorderColor = Purple,
                                unfocusedBorderColor = Color.Gray
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = genderExpanded,
                            onDismissRequest = { genderExpanded = false }
                        ) {
                            genderOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        gender = option
                                        genderExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    OutlinedTextField(
                        value = weight,
                        onValueChange = { weight = it },
                        label = { Text("Weight") },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            Text("kg", color = Color.Gray, fontSize = 14.sp)
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = Purple,
                            unfocusedBorderColor = Color.Gray
                        )
                    )
                    OutlinedTextField(
                        value = color,
                        onValueChange = { color = it },
                        label = { Text("Color") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = Purple,
                            unfocusedBorderColor = Color.Gray
                        )
                    )
                    // Height in ft'in format
                    OutlinedTextField(
                        value = height,
                        onValueChange = {
                            // Only allow 1 digit, single quote, 2 digits
                            val filtered = it.filterIndexed { idx, c ->
                                when (idx) {
                                    0 -> c.isDigit()
                                    1 -> c == '\''
                                    2, 3 -> c.isDigit()
                                    else -> false
                                }
                            }.take(4)
                            height = filtered
                        },
                        label = { Text("Height (ft'in)") },
                        placeholder = { Text("5'07") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        trailingIcon = {
                            Text("feet", color = Color.Gray, fontSize = 14.sp)
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = Purple,
                            unfocusedBorderColor = Color.Gray
                        )
                    )
                    OutlinedTextField(
                        value = age,
                        onValueChange = { age = it },
                        label = { Text("Age") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = Purple,
                            unfocusedBorderColor = Color.Gray
                        )
                    )
                    // Type Dropdown
                    ExposedDropdownMenuBox(
                        expanded = typeExpanded,
                        onExpandedChange = { typeExpanded = !typeExpanded }
                    ) {
                        OutlinedTextField(
                            value = type,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Type") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                focusedBorderColor = Purple,
                                unfocusedBorderColor = Color.Gray
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = typeExpanded,
                            onDismissRequest = { typeExpanded = false }
                        ) {
                            typeOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        type = option
                                        typeExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    // Status Dropdown
                    ExposedDropdownMenuBox(
                        expanded = statusExpanded,
                        onExpandedChange = { statusExpanded = !statusExpanded }
                    ) {
                        OutlinedTextField(
                            value = status,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Status") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                focusedBorderColor = Purple,
                                unfocusedBorderColor = Color.Gray
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = statusExpanded,
                            onDismissRequest = { statusExpanded = false }
                        ) {
                            statusOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        status = option
                                        statusExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = Purple,
                            unfocusedBorderColor = Color.Gray
                        )
                    )
                }

                // Add Pet Button
                Button(
                    onClick = {
                        coroutineScope.launch {
                            isSubmitting = true
                            submitError = ""
                            try {
                                val context = context
                                // Convert fields to RequestBody
                                val nameBody = name.toRequestBody("text/plain".toMediaTypeOrNull())
                                val typeBody = type.toRequestBody("text/plain".toMediaTypeOrNull())
                                val breedBody = breed.toRequestBody("text/plain".toMediaTypeOrNull())
                                val ageBody = age.toRequestBody("text/plain".toMediaTypeOrNull())
                                val genderBody = gender.toRequestBody("text/plain".toMediaTypeOrNull())
                                val descriptionBody = description.toRequestBody("text/plain".toMediaTypeOrNull())
                                val statusBody = status.toRequestBody("text/plain".toMediaTypeOrNull())
                                val userNameBody = userName.toRequestBody("text/plain".toMediaTypeOrNull())
                                val addressBody = address.toRequestBody("text/plain".toMediaTypeOrNull())
                                val contactNumberBody = contactNumber.toRequestBody("text/plain".toMediaTypeOrNull())

                                // Prepare up to 4 images as MultipartBody.Part, using temp files and asRequestBody (like SignUpScreen)
                                fun uriToPart(uri: Uri?, partName: String): MultipartBody.Part? {
                                    if (uri == null) return null
                                    val contentResolver = context.contentResolver
                                    val inputStream = contentResolver.openInputStream(uri) ?: return null
                                    val tempFile = File.createTempFile("pet_photo", null, context.cacheDir)
                                    inputStream.use { input ->
                                        tempFile.outputStream().use { output ->
                                            input.copyTo(output)
                                        }
                                    }
                                    val mimeType = contentResolver.getType(uri) ?: "image/*"
                                    val requestFile = tempFile.asRequestBody(mimeType.toMediaTypeOrNull())
                                    return MultipartBody.Part.createFormData(partName, tempFile.name, requestFile)
                                }
                                val photoParts = (0 until 4).map { idx ->
                                    uriToPart(photoUris.getOrNull(idx), "photo${idx + 1}")
                                }

                                if (photoParts[0] == null) {
                                    isSubmitting = false
                                    submitError = "At least 1 photo is required."
                                    return@launch
                                }

                                val weightToSend = if (weight.isNotBlank() && !weight.trim().endsWith("kg")) weight.trim() + " kg" else weight.trim()
                                val heightToSend = if (height.isNotBlank() && !height.trim().endsWith("feet")) height.trim() + " feet" else height.trim()

                                val response = withContext(Dispatchers.IO) {
                                    userApi.addPet(
                                        name = nameBody,
                                        type = typeBody,
                                        breed = breedBody,
                                        age = ageBody,
                                        gender = genderBody,
                                        description = descriptionBody,
                                        photo1 = photoParts[0],
                                        photo2 = photoParts[1],
                                        photo3 = photoParts[2],
                                        photo4 = photoParts[3],
                                        status = statusBody,
                                        userName = userNameBody,
                                        address = addressBody,
                                        contactNumber = contactNumberBody,
                                        weight = weightToSend.takeIf { it.isNotBlank() }?.toRequestBody(),
                                        color = color.takeIf { it.isNotBlank() }?.toRequestBody(),
                                        height = heightToSend.takeIf { it.isNotBlank() }?.toRequestBody()
                                    )
                                }
                                isSubmitting = false
                                onPetAdded()
                            } catch (e: Exception) {
                                isSubmitting = false
                                submitError = e.message ?: "Failed to add pet."
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Purple),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(
                            "Add Pet",
                            color = Color.White,
                            fontFamily = Inter,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }
                if (submitError.isNotEmpty()) {
                    Text(submitError, color = Color.Red, fontSize = 14.sp, modifier = Modifier.padding(top = 8.dp))
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
} 
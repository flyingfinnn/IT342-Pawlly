package com.sysinteg.pawlly.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sysinteg.pawlly.R
import com.sysinteg.pawlly.ui.theme.Purple
import com.sysinteg.pawlly.ui.theme.White
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.verticalScroll
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.SideEffect
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import com.sysinteg.pawlly.userApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import com.sysinteg.pawlly.PetResponse
import com.sysinteg.pawlly.UpdatePetRequest
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.border
import androidx.compose.material.icons.filled.Add
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.icons.filled.Close
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.graphics.asImageBitmap
import androidx.activity.compose.rememberLauncherForActivityResult

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PetDetailScreen(
    petId: Int,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var pet by remember { mutableStateOf<PetResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf("") }
    var editMode by remember { mutableStateOf(false) }

    // Edit fields
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
    var photoUris by remember { mutableStateOf<List<String>>(emptyList()) }
    var photoThumb by remember { mutableStateOf<String?>(null) }
    var editPhotoUris by remember { mutableStateOf(mutableListOf<String>()) }

    // Fetch pet details
    LaunchedEffect(petId) {
        isLoading = true
        error = ""
        try {
            val result = withContext(Dispatchers.IO) { userApi.getPetById(petId) }
            pet = result
            // Prefill edit fields
            name = result.name ?: ""
            age = result.age ?: ""
            breed = result.breed ?: ""
            description = result.description ?: ""
            gender = result.gender ?: ""
            status = result.status ?: ""
            type = result.type ?: ""
            address = result.address ?: ""
            contactNumber = result.contactNumber ?: ""
            userName = result.userName ?: ""
            photoUris = listOfNotNull(result.photo1, result.photo2, result.photo3, result.photo4).filter { it.isNotEmpty() }
            photoThumb = result.photo1Thumb
            weight = result.weight ?: ""
            color = result.color ?: ""
            height = result.height ?: ""
        } catch (e: Exception) {
            error = e.message ?: "Failed to load pet details."
        }
        isLoading = false
    }

    // Fetch owner profile picture (base64 or URL)
    var ownerProfilePicture by remember { mutableStateOf<String?>(null) }
    var ownerProfileBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    LaunchedEffect(userName) {
        if (userName.isNotBlank()) {
            try {
                val owner = withContext(Dispatchers.IO) { userApi.getUserByUsername(userName) }
                ownerProfilePicture = owner.profilePicture
                // Debug: Log the profile picture value
                println("[DEBUG] owner.profilePicture: ${owner.profilePicture}")
                // Try to decode base64 if present
                ownerProfileBitmap = try {
                    if (!owner.profilePicture.isNullOrBlank() && owner.profilePicture.startsWith("data:image")) {
                        val base64Data = owner.profilePicture.substringAfter(",", "")
                        val imageBytes = android.util.Base64.decode(base64Data, android.util.Base64.DEFAULT)
                        android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    } else null
                } catch (_: Exception) { null }
            } catch (_: Exception) {
                ownerProfilePicture = null
                ownerProfileBitmap = null
            }
        }
    }

    if (isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
    if (error.isNotEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Error: $error", color = Color.Red)
        }
        return
    }
    if (pet == null) return

    if (!editMode) {
        // --- VIEW MODE ---
        val petImages = photoUris
        var showImageDialog by remember { mutableStateOf(false) }
        var selectedImage by remember { mutableStateOf<String?>(null) }
        val pagerState = rememberPagerState(pageCount = { petImages.size.coerceAtLeast(1) })
    Scaffold(
        containerColor = White,
            topBar = {
                TopAppBar(
                    title = { Text("Pet Details", color = Purple) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Purple)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = White)
                )
            },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(White)
                    .padding(bottom = 48.dp),
                contentAlignment = Alignment.Center
            ) {
                Button(
                        onClick = {
                            editPhotoUris = photoUris.toMutableList()
                            editMode = true
                        },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Purple)
                ) {
                        Text("Edit Pet Details", color = White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .background(White)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(8.dp))
                    // Single Image (no pager)
                Box(
                    modifier = Modifier
                            .fillMaxWidth(0.92f)
                            .padding(horizontal = 0.dp)
                        .height(240.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.LightGray)
                ) {
                        if (petImages.isNotEmpty()) {
                            HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
                                val imageUrl = petImages[page]
                                AsyncImage(
                                    model = imageUrl,
                                    contentDescription = "Pet Image",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clickable {
                                            selectedImage = imageUrl
                                            showImageDialog = true
                                        },
                                    contentScale = ContentScale.Crop
                                )
                            }
                        } else {
                            // Placeholder
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Pets, contentDescription = "No Image", tint = Color.LightGray, modifier = Modifier.size(64.dp))
                            }
                        }
                        // Fullscreen image popout dialog
                        if (showImageDialog && selectedImage != null) {
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.95f))
                                    .clickable { showImageDialog = false },
                                contentAlignment = Alignment.Center
                            ) {
                                AsyncImage(
                                    model = selectedImage,
                                    contentDescription = "Full Image",
                            modifier = Modifier
                                .fillMaxSize()
                                        .background(Color.Black),
                                    contentScale = ContentScale.Fit
                                )
                                IconButton(
                                    onClick = { showImageDialog = false },
                                    modifier = Modifier.align(Alignment.TopEnd).padding(24.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(32.dp))
                                }
                            }
                    }
                }
                Spacer(Modifier.height(24.dp))
                // Pet Name
                Text(
                        name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = Purple,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                )
                Spacer(Modifier.height(8.dp))
                // Attribute Bullets Row
                val attributeBullets = listOf(
                        Triple(Icons.Default.Pets, breed, "Breed"),
                        Triple(Icons.Default.Female, gender, "Gender"),
                        Triple(Icons.Default.Scale, weight, "Weight"),
                        Triple(Icons.Default.Palette, color, "Color"),
                        Triple(Icons.Default.Height, height, "Height")
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    attributeBullets.forEach { (icon, value, label) ->
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = Color.Transparent,
                            border = BorderStroke(1.dp, Purple),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Icon(icon, contentDescription = label, tint = Purple, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text(value, color = Purple, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                // Pet Details List
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                        val petAttributes = listOf(
                            Triple(Icons.Default.Pets, age, "Age"),
                            Triple(Icons.Default.Pets, type, "Type"),
                            Triple(Icons.Default.Pets, status, "Status"),
                            Triple(Icons.Default.LocationOn, address, "Address"),
                            Triple(Icons.Default.Person, contactNumber, "Contact Number"),
                            Triple(Icons.Default.Person, pet?.submissionDate ?: "", "Submission Date")
                        )
                    petAttributes.forEach { (icon, value, label) ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(icon, contentDescription = label, tint = Purple, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("$label: $value", color = Color(0xFF444444), fontSize = 15.sp)
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                // Description
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text("Description", fontWeight = FontWeight.Bold, color = Purple, fontSize = 18.sp)
                    Spacer(Modifier.height(4.dp))
                        Text(description, color = Color(0xFF444444), fontSize = 15.sp)
                }
                Spacer(Modifier.height(32.dp))
                // Pet Owner Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text("Pet Owner", fontWeight = FontWeight.Bold, color = Purple, fontSize = 18.sp)
                    Spacer(Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                            if (ownerProfileBitmap != null) {
                        Image(
                                    bitmap = ownerProfileBitmap!!.asImageBitmap(),
                                    contentDescription = "Owner Profile Picture",
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(24.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            } else if (!ownerProfilePicture.isNullOrBlank()) {
                                AsyncImage(
                                    model = ownerProfilePicture,
                            contentDescription = "Owner Profile Picture",
                            modifier = Modifier
                                .size(48.dp)
                                        .clip(RoundedCornerShape(24.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(Icons.Default.Person, contentDescription = "Owner", tint = Purple, modifier = Modifier.size(48.dp))
                            }
                            Spacer(Modifier.width(16.dp))
                            Text(userName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF222222))
                        }
                    }
                    Spacer(Modifier.height(48.dp))
                }
            }
        }
    } else {
        // --- EDIT MODE ---
        // Inline AddPetScreen form logic, but prefill fields and change button text to 'Save Changes'.
        // Add a 'Discard' button beside 'Save Changes'.
        val genderOptions = listOf("Male", "Female")
        val statusOptions = listOf("Available", "Not Available")
        val typeOptions = listOf("Dog", "Cat")
        var genderExpanded by remember { mutableStateOf(false) }
        var statusExpanded by remember { mutableStateOf(false) }
        var typeExpanded by remember { mutableStateOf(false) }
        var breedSuggestions by remember { mutableStateOf(listOf<String>()) }
        var breedDropdownExpanded by remember { mutableStateOf(false) }
        val allBreeds = listOf(
            "Labrador Retriever", "German Shepherd", "Golden Retriever", "Bulldog", "Poodle (including standard, miniature, and toy)",
            "Beagle", "Rottweiler", "Yorkshire Terrier", "Boxer", "Dachshund", "Siberian Husky", "Great Dane", "Doberman Pinscher",
            "Australian Shepherd", "Shih Tzu", "Chihuahua", "Cavalier King Charles Spaniel", "French Bulldog", "Border Collie",
            "Pembroke Welsh Corgi", "Persian", "Maine Coon", "Siamese", "Ragdoll", "Bengal", "Sphynx", "British Shorthair",
            "Abyssinian", "Birman", "Oriental"
        )
        LaunchedEffect(breed) {
            if (breed.length >= 4) {
                breedSuggestions = allBreeds.filter { it.contains(breed, ignoreCase = true) && it != breed }
                breedDropdownExpanded = breedSuggestions.isNotEmpty()
            } else {
                breedSuggestions = emptyList()
                breedDropdownExpanded = false
            }
        }
        var isSubmitting by remember { mutableStateOf(false) }
        var submitError by remember { mutableStateOf("") }
        // --- Editable photo carousel ---
        // Show up to 4 photo slots: current pet photos (URLs) and new uploads
        val maxPhotos = 4
        var addPhotoIndex by remember { mutableStateOf<Int?>(null) }
        val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                addPhotoIndex?.let { idx ->
                    if (editPhotoUris.size < maxPhotos) {
                        editPhotoUris = (editPhotoUris + uri.toString()).toMutableList()
                    }
                }
            }
            addPhotoIndex = null
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(maxPhotos) { index ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                        .clickable {
                            if (editPhotoUris.size < maxPhotos && index == editPhotoUris.size) {
                                addPhotoIndex = index
                                imagePickerLauncher.launch("image/*")
                            }
                        }
                ) {
                    if (index < editPhotoUris.size) {
                        AsyncImage(
                            model = editPhotoUris[index],
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
                                    editPhotoUris = editPhotoUris.toMutableList().apply { removeAt(index) }
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(Color(0xFFFAF9F6))
                .padding(16.dp)
                .systemBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header text below status bar
            Text(
                text = "Edit Pet Details",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = Purple,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp) // Small gap below status bar
            )
            // Pet Details Section (fields)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
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
                        onValueChange = { breed = it },
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
                OutlinedTextField(
                    value = height,
                    onValueChange = { height = it },
                    label = { Text("Height (ft'in)") },
                    placeholder = { Text("5'07") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Text),
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
            if (submitError.isNotEmpty()) {
                Text(submitError, color = Color.Red, fontSize = 14.sp, modifier = Modifier.padding(top = 8.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            isSubmitting = true
                            submitError = ""
                            try {
                                val weightToSend = if (weight.isNotBlank() && !weight.trim().endsWith("kg")) weight.trim() + " kg" else weight.trim()
                                val heightToSend = if (height.isNotBlank() && !height.trim().endsWith("feet")) height.trim() + " feet" else height.trim()
                                // --- Send the current editPhotoUris as photo1-4 ---
                                val updateRequest = UpdatePetRequest(
                                    name = name,
                                    breed = breed,
                                    age = age,
                                    type = type,
                                    gender = gender,
                                    description = description,
                                    status = status,
                                    userName = userName,
                                    address = address,
                                    contactNumber = contactNumber,
                                    photo1 = editPhotoUris.getOrNull(0),
                                    photo1Thumb = pet?.photo1Thumb,
                                    photo2 = editPhotoUris.getOrNull(1),
                                    photo3 = editPhotoUris.getOrNull(2),
                                    photo4 = editPhotoUris.getOrNull(3),
                                    weight = weightToSend,
                                    color = color,
                                    height = heightToSend
                                )
                                val updatedPet = userApi.updatePetDetails(petId, updateRequest)
                                // Update local state with new values
                                pet = updatedPet
                                // Also update photoUris and editPhotoUris to reflect changes
                                photoUris = listOfNotNull(updatedPet.photo1, updatedPet.photo2, updatedPet.photo3, updatedPet.photo4).filter { it.isNotEmpty() }
                                editPhotoUris = photoUris.toMutableList()
                                editMode = false
                            } catch (e: Exception) {
                                submitError = e.message ?: "Failed to update pet."
                            }
                            isSubmitting = false
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Purple),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(
                            "Save Changes",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
                OutlinedButton(
                    onClick = {
                        // Discard changes: revert fields to original pet details and exit edit mode
                        pet?.let {
                            name = it.name ?: ""
                            age = it.age ?: ""
                            breed = it.breed ?: ""
                            description = it.description ?: ""
                            gender = it.gender ?: ""
                            status = it.status ?: ""
                            type = it.type ?: ""
                            address = it.address ?: ""
                            contactNumber = it.contactNumber ?: ""
                            userName = it.userName ?: ""
                            photoUris = listOfNotNull(it.photo1, it.photo2, it.photo3, it.photo4).filter { s -> s.isNotEmpty() }
                            photoThumb = it.photo1Thumb
                            editPhotoUris = photoUris.toMutableList()
                            weight = it.weight ?: ""
                            color = it.color ?: ""
                            height = it.height ?: ""
                        }
                        editMode = false
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Discard", color = Purple, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
} 
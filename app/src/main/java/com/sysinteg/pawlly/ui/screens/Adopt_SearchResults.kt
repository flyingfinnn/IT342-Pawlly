package com.sysinteg.pawlly.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.sysinteg.pawlly.model.Pet
import com.sysinteg.pawlly.ui.components.PetCard
import com.sysinteg.pawlly.ui.theme.Purple
import com.sysinteg.pawlly.ui.theme.White
import com.sysinteg.pawlly.userApi
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdoptSearchResultsScreen(
    pets: List<Pet> = emptyList(),
    onPetClick: (Int) -> Unit = {},
    onBack: () -> Unit = {},
    onFilter: (String) -> Unit = {},
    onNavHome: () -> Unit = {},
    onNavNotifications: () -> Unit = {},
    onNavProfile: () -> Unit = {},
    onAdoptPetDetail: (Int, String) -> Unit = { _, _ -> }
) {
    val systemUiController = rememberSystemUiController()
    val useDarkIcons = true
    val scope = rememberCoroutineScope()
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = useDarkIcons
        )
    }
    var searchQuery by remember { mutableStateOf("") }
    var showFilterSheet by remember { mutableStateOf(false) }
    var allPets by remember { mutableStateOf<List<Pet>>(emptyList()) }
    var filteredPets by remember { mutableStateOf<List<Pet>>(emptyList()) }
    var petsLoading by remember { mutableStateOf(true) }
    var petsError by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }

    // Search filter function for frontend filtering
    fun filterPets(query: String, pets: List<Pet>): List<Pet> {
        if (query.isBlank()) return pets
        return pets.filter { pet ->
            pet.name.lowercase().contains(query.lowercase()) ||
            pet.breed.lowercase().contains(query.lowercase()) ||
            pet.type?.lowercase()?.contains(query.lowercase()) == true
        }
    }

    // Function to search pets by name using the backend
    suspend fun searchPetsByName(query: String) {
        if (query.isBlank()) {
            filteredPets = allPets
            return
        }
        isSearching = true
        petsLoading = true
        try {
            val response = userApi.getAllPets() // Temporarily use getAllPets until searchPetsByName is implemented
            filteredPets = response.map { pet ->
                Pet(
                    id = pet.pid,
                    name = pet.name ?: "",
                    breed = pet.breed ?: "",
                    age = pet.age ?: "",
                    type = pet.type,
                    location = pet.address ?: "",
                    photo1 = pet.photo1,
                    photo2 = pet.photo2,
                    photo3 = pet.photo3,
                    photo4 = pet.photo4,
                    weight = pet.weight,
                    color = pet.color,
                    height = pet.height,
                    user_name = pet.userName
                )
            }.filter { pet ->
                pet.name.lowercase().contains(query.lowercase())
            }
        } catch (e: Exception) {
            petsError = e.message ?: "Failed to search pets."
        }
        petsLoading = false
        isSearching = false
    }

    // Update filtered pets when search query changes
    LaunchedEffect(searchQuery) {
        if (!isSearching) {
            filteredPets = filterPets(searchQuery, allPets)
        }
    }

    // Filter state
    var animalType by remember { mutableStateOf("Dog") }
    var gender by remember { mutableStateOf("Male") }
    var age by remember { mutableStateOf(2f) }

    // Helper functions for filtering
    fun getTypeFromBreed(breed: String): String = when {
        breed.contains("cat", ignoreCase = true) -> "Cat"
        else -> "Dog"
    }

    fun getAgeInYears(ageStr: String): Float = when {
        ageStr.contains("yr") -> ageStr.split(" ")[0].toFloatOrNull() ?: 0f
        ageStr.contains("mo") -> (ageStr.split(" ")[0].toFloatOrNull() ?: 0f) / 12f
        else -> 0f
    }

    fun getGenderFromName(name: String): String = when (name) {
        "Milo", "Buddy", "Max" -> "Male"
        "Luna", "Bella", "Coco" -> "Female"
        else -> "Male"
    }

    // Apply filters function
    fun applyFilters(pets: List<Pet>): List<Pet> {
        return pets.filter { pet ->
            val matchesType = getTypeFromBreed(pet.breed) == animalType
            val matchesGender = getGenderFromName(pet.name) == gender
            val matchesAge = getAgeInYears(pet.age) <= age
            matchesType && matchesGender && matchesAge
        }
    }

    // Update filtered pets when filters change
    LaunchedEffect(animalType, gender, age) {
        val searchFiltered = filterPets(searchQuery, allPets)
        filteredPets = applyFilters(searchFiltered)
    }

    // Fetch pets from Supabase on load
    LaunchedEffect(Unit) {
        petsLoading = true
        petsError = ""
        try {
            val response = userApi.getAllPets()
            allPets = response.map {
                Pet(
                    id = it.pid,
                    name = it.name ?: "",
                    breed = it.breed ?: "",
                    age = it.age ?: "",
                    type = it.type,
                    location = it.address ?: "",
                    photo1 = it.photo1,
                    photo2 = it.photo2,
                    photo3 = it.photo3,
                    photo4 = it.photo4,
                    weight = it.weight,
                    color = it.color,
                    height = it.height,
                    user_name = it.userName
                )
            }
            filteredPets = allPets
        } catch (e: Exception) {
            petsError = e.message ?: "Failed to load pets."
        }
        petsLoading = false
    }

    // Pagination state
    var currentPage by remember { mutableStateOf(1) }
    val petsPerPage = 8
    val totalPages = (filteredPets.size + petsPerPage - 1) / petsPerPage
    val pagedPets = filteredPets.drop((currentPage - 1) * petsPerPage).take(petsPerPage)

    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences(com.sysinteg.pawlly.utils.Constants.PAWLLY_PREFS, android.content.Context.MODE_PRIVATE) }
    var currentUsername by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        val token = prefs.getString(com.sysinteg.pawlly.utils.Constants.KEY_JWT_TOKEN, null)
        if (token != null) {
            try {
                val me = com.sysinteg.pawlly.userApi.getMe("Bearer $token")
                currentUsername = me.username ?: ""
            } catch (_: Exception) {}
        }
    }

    Scaffold(
        containerColor = White,
        // Removed topBar, will add search row in scrollable content
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(White)
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search"
                            )
                        },
                        placeholder = { Text("Search...") },
                        shape = CircleShape,
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 4.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            containerColor = Color.White,
                            focusedBorderColor = Purple,
                            unfocusedBorderColor = Color(0xFFCCCCCC)
                        ),
                        textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Search
                        ),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                scope.launch {
                                    searchPetsByName(searchQuery)
                                }
                            }
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box {
                        Button(
                            onClick = { showFilterSheet = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Purple),
                            shape = RoundedCornerShape(50),
                            modifier = Modifier.height(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "",
                                tint = Color.White
                            )
                        }
                        // Green dot indicator when filters are active
                        if (animalType != "Dog" || gender != "Male" || age != 2f) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(Color.White, CircleShape)
                                    .align(Alignment.TopEnd)
                                    .offset(x = 2.dp, y = 2.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(Color.Green, CircleShape)
                                        .align(Alignment.Center)
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                if (petsLoading) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (petsError.isNotEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Error: $petsError", color = Color.Red)
                    }
                } else if (filteredPets.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No pets found. Try adjusting your filters!",
                            color = Color.Gray,
                            fontSize = 18.sp
                        )
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 800.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(pagedPets) { pet ->
                            PetCard(
                                pet = pet,
                                currentUsername = currentUsername,
                                onOwnerClick = { onPetClick(pet.id) },
                                onPublicClick = { onAdoptPetDetail(pet.id, pet.name) }
                            )
                        }
                    }
                    // Pagination UI
                    if (totalPages > 1) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Previous button
                            IconButton(
                                onClick = { if (currentPage > 1) currentPage-- },
                                enabled = currentPage > 1
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ChevronLeft,
                                    contentDescription = "Previous",
                                    tint = if (currentPage > 1) Purple else Color.LightGray
                                )
                            }
                            // Page numbers (show first, last, current, and ... as needed)
                            val pageNumbers = mutableListOf<Int>()
                            if (totalPages <= 6) {
                                for (i in 1..totalPages) pageNumbers.add(i)
                            } else {
                                pageNumbers.add(1)
                                if (currentPage > 3) pageNumbers.add(-1) // ...
                                val start = maxOf(2, currentPage - 1)
                                val end = minOf(totalPages - 1, currentPage + 1)
                                for (i in start..end) pageNumbers.add(i)
                                if (currentPage < totalPages - 2) pageNumbers.add(-2) // ...
                                pageNumbers.add(totalPages)
                            }
                            pageNumbers.forEach { page ->
                                when (page) {
                                    -1, -2 -> {
                                        Text("...", modifier = Modifier.padding(horizontal = 8.dp), color = Color.Gray, fontSize = 18.sp)
                                    }
                                    else -> {
                                        Button(
                                            onClick = { currentPage = page },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (currentPage == page) Purple else Color.White
                                            ),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier
                                                .padding(horizontal = 4.dp)
                                                .size(40.dp, 40.dp),
                                            contentPadding = PaddingValues(0.dp)
                                        ) {
                                            Text(
                                                text = page.toString(),
                                                color = if (currentPage == page) Color.White else Color.Black,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                            // Next button
                            IconButton(
                                onClick = { if (currentPage < totalPages) currentPage++ },
                                enabled = currentPage < totalPages
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ChevronRight,
                                    contentDescription = "Next",
                                    tint = if (currentPage < totalPages) Purple else Color.LightGray
                                )
                            }
                        }
                    }
                }
            }
            // Modal Bottom Sheet for Filters
            if (showFilterSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showFilterSheet = false },
                    containerColor = Purple,
                    contentColor = Color(0xFFEDE7F6)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Filter Pets", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp, textAlign = TextAlign.Center)
                        Spacer(Modifier.height(16.dp))
                        // Animal Type
                        Text("Type", color = Color(0xFFEDE7F6), fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            listOf("Dog", "Cat").forEach { type ->
                                FilterChip(
                                    selected = animalType == type,
                                    onClick = { animalType = type },
                                    label = { Text(type, color = Color.White) },
                                    shape = RoundedCornerShape(50),
                                    colors = FilterChipDefaults.filterChipColors(
                                        containerColor = if (animalType == type) Color.White.copy(alpha = 0.2f) else Color.Transparent
                                    )
                                )
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                        // Gender
                        Text("Gender", color = Color(0xFFEDE7F6), fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            listOf("Male", "Female").forEach { g ->
                                FilterChip(
                                    selected = gender == g,
                                    onClick = { gender = g },
                                    label = { Text(g, color = Color.White) },
                                    shape = RoundedCornerShape(50),
                                    colors = FilterChipDefaults.filterChipColors(
                                        containerColor = if (gender == g) Color.White.copy(alpha = 0.2f) else Color.Transparent
                                    )
                                )
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                        // Age
                        Text("Age", color = Color(0xFFEDE7F6), fontWeight = FontWeight.Bold)
                        Slider(
                            value = age,
                            onValueChange = { age = it },
                            valueRange = 0f..20f,
                            steps = 19,
                            colors = SliderDefaults.colors(
                                thumbColor = Color.White,
                                activeTrackColor = Color.White,
                                inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                            )
                        )
                        val years = age.toInt()
                        val months = ((age - years) * 12).toInt()
                        val ageLabel = when {
                            years == 0 && months > 0 -> "$months month${if (months > 1) "s" else ""}"
                            years > 0 && months == 0 -> "$years year${if (years > 1) "s" else ""}"
                            years > 0 && months > 0 -> "$years year${if (years > 1) "s" else ""} $months month${if (months > 1) "s" else ""}"
                            else -> "0 months"
                        }
                        Text(ageLabel, color = Color.White)
                        Spacer(Modifier.height(24.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(
                                onClick = {
                                    filteredPets = applyFilters(allPets)
                                    showFilterSheet = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                shape = RoundedCornerShape(50),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Apply Filter", color = Purple, fontWeight = FontWeight.Bold)
                            }
                            Spacer(Modifier.width(16.dp))
                            OutlinedButton(
                                onClick = {
                                    // Reset all filters to default values
                                    animalType = "Dog"
                                    gender = "Male"
                                    age = 2f
                                    filteredPets = allPets
                                    showFilterSheet = false
                                },
                                border = ButtonDefaults.outlinedButtonBorder,
                                shape = RoundedCornerShape(50),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Discard", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
            // Navigation Bar (bottom)
            Box(
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                PawllyNavBar(
                    selectedScreen = "Adopt",
                    onNavHome = onNavHome,
                    onNavNotifications = onNavNotifications,
                    onNavProfile = onNavProfile
                )
            }
        }
    }
} 
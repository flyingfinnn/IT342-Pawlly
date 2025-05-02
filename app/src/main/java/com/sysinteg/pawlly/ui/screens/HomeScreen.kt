package com.sysinteg.pawlly.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sysinteg.pawlly.R
import com.sysinteg.pawlly.ui.theme.Purple
import com.sysinteg.pawlly.ui.theme.White
import kotlin.random.Random
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import com.sysinteg.pawlly.model.Pet
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.ui.draw.blur
import com.sysinteg.pawlly.ui.components.PetCard

// Sample pet data
private val samplePets = listOf(
    Pet(1, "Milo", "Golden Retriever", "2 yrs", "Lagos", R.drawable.smile_dog),
    Pet(2, "Luna", "Siamese Cat", "1 yr", "Abuja", R.drawable.smile_cat),
    Pet(3, "Buddy", "Rabbit", "6 mo", "Ibadan", R.drawable.smile_dog),
    Pet(4, "Bella", "Persian Cat", "3 yrs", "Kano", R.drawable.smile_cat),
    Pet(5, "Max", "Labrador", "4 yrs", "Enugu", R.drawable.smile_dog),
    Pet(6, "Coco", "Poodle", "2 yrs", "Port Harcourt", R.drawable.smile_dog)
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AdoptScreen(
    onBrowseAll: () -> Unit = {},
    onPetClick: (Int) -> Unit = {},
    onFilterClick: (String) -> Unit = {},
    onLostFoundClick: () -> Unit = {},
    onNavHome: () -> Unit = {},
    onNavNotifications: () -> Unit = {},
    onNavProfile: () -> Unit = {}
) {
    val filters = listOf("Dog", "Cat", "Small", "Nearby", "All")
    var selectedFilter by remember { mutableStateOf("All") }
    val scrollState = rememberScrollState()
    val cardCorner = 24.dp
    val cardSpacing = 16.dp
    val cardWidth = (LocalConfiguration.current.screenWidthDp.dp - 16.dp * 2 - cardSpacing) / 2
    val cardHeight = cardWidth * 1.15f

    Scaffold(
        containerColor = Color.White,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 4.dp)
                    .statusBarsPadding()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logopurple),
                    contentDescription = "Pawlly Logo",
                    modifier = Modifier
                        .height(40.dp)
                        .align(Alignment.CenterStart)
                )
            }
        },
        bottomBar = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                PawllyNavBar(
                    selectedScreen = "Home",
                    onNavHome = onNavHome,
                    onNavNotifications = onNavNotifications,
                    onNavProfile = onNavProfile
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(innerPadding)
                .background(Color.White)
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(4.dp))
            // Banner
            Box(
                modifier = Modifier
                    .width(cardWidth * 2 + cardSpacing)
                    .height(180.dp)
                    .clip(RoundedCornerShape(cardCorner))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.adopt_banner),
                    contentDescription = "Adopt Banner",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(Modifier.height(8.dp))
            // Quick Filters
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 16.dp)
            ) {
                filters.forEach { filter ->
                    val isSelected = filter == selectedFilter
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedFilter = filter
                            onFilterClick(filter)
                        },
                        label = {
                            Text(
                                filter,
                                color = if (isSelected) Color.White else Purple,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        shape = RoundedCornerShape(50),
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = if (isSelected) Purple else Color.White
                        )
                    )
                }
            }
            Spacer(Modifier.height(4.dp))
            // Featured Pets Section
            Text(
                text = "Featured Pets",
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                color = Color(0xFF222222),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                textAlign = TextAlign.Start
            )
            Spacer(modifier = Modifier.height(24.dp))
            // Pet Cards Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(cardHeight * 3 + cardSpacing * 2)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                userScrollEnabled = false
            ) {
                items(samplePets) { pet ->
                    PetCard(
                        pet = pet,
                        onClick = { onPetClick(pet.id) }
                    )
                }
            }
            // Browse All CTA
            Button(
                onClick = onBrowseAll,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(cardCorner),
                colors = ButtonDefaults.buttonColors(containerColor = Purple)
            ) {
                Text("Browse All", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.height(2.dp))
            // Go to Lost and Found button
            Button(
                onClick = onLostFoundClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(cardCorner),
                colors = ButtonDefaults.buttonColors(containerColor = Purple)
            ) {
                Text("Go to Lost and Found", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
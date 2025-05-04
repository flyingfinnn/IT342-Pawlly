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
import com.google.accompanist.pager.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.verticalScroll
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.SideEffect
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun AdoptPetDetailScreen(
    petId: Int = 1,
    onAdoptNow: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val systemUiController = rememberSystemUiController()
    val useDarkIcons = true
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = useDarkIcons
        )
    }
    // Placeholder pet data
    val petImages = listOf(R.drawable.logoiconpurple, R.drawable.logoiconwhite).take(4)
    val petName = "Magie"
    val petBreed = "Shiba Inu"
    val petAge = "14 months"
    val petGender = "Female"
    val petWeight = "12 lb"
    val petColor = "Red"
    val petHeight = "91 cm"
    val petLocation = "California (12 km away)"
    val petDescription = "Magie is a playful and loving Shiba Inu who enjoys long walks and cuddles. She is looking for a forever home where she can share her affection and energy. Magie is house-trained, vaccinated, and gets along well with other pets."
    val petStatus = "Available"
    val petType = "Dog"
    val petAddress = "123 Main St, City"
    val petContactNumber = "555-1234"
    val petSubmissionDate = "2024-06-01"
    val petUserName = "John Doe"
    val petUserProfilePic = R.drawable.logoiconpurple // Placeholder
    val petAttributes = listOf(
        Triple(Icons.Default.Pets, petAge, "Age"),
        Triple(Icons.Default.Pets, petType, "Type"),
        Triple(Icons.Default.Pets, petStatus, "Status"),
        Triple(Icons.Default.LocationOn, petAddress, "Address"),
        Triple(Icons.Default.Person, petContactNumber, "Contact Number"),
        Triple(Icons.Default.Person, petSubmissionDate, "Submission Date")
    )
    val petTraits = listOf(
        "Good with children",
        "House-trained",
        "Vaccinated",
        "Microchipped"
    )
    val petStory = "Magie is a playful and loving Shiba Inu who enjoys long walks and cuddles. She is looking for a forever home where she can share her affection and energy. Magie is house-trained, vaccinated, and gets along well with other pets."

    var expanded by remember { mutableStateOf(false) }
    val pagerState = rememberPagerState()
    // State for full screen image view
    var fullScreenImageIndex by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        containerColor = White,
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(White)
                    .padding(bottom = 48.dp),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = onAdoptNow,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Purple)
                ) {
                    Text("Adopt Now", color = White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
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
                // Image Carousel
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(240.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.LightGray)
                ) {
                    HorizontalPager(
                        count = petImages.size,
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable { fullScreenImageIndex = page }
                        ) {
                            Image(
                                painter = painterResource(id = petImages[page]),
                                contentDescription = "Pet Image",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
                Spacer(Modifier.height(24.dp))
                // Pet Name
                Text(
                    petName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = Purple,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                )
                Spacer(Modifier.height(8.dp))
                // Attribute Bullets Row
                val attributeBullets = listOf(
                    Triple(Icons.Default.Pets, petBreed, "Breed"),
                    Triple(Icons.Default.Female, petGender, "Gender"),
                    Triple(Icons.Default.Scale, petWeight, "Weight"),
                    Triple(Icons.Default.Palette, petColor, "Color"),
                    Triple(Icons.Default.Height, petHeight, "Height")
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
                    Text(petDescription, color = Color(0xFF444444), fontSize = 15.sp)
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
                        Image(
                            painter = painterResource(id = petUserProfilePic),
                            contentDescription = "Owner Profile Picture",
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(Purple.copy(alpha = 0.1f))
                        )
                        Spacer(Modifier.width(16.dp))
                        Text(petUserName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF222222))
                    }
                }
                Spacer(Modifier.height(48.dp)) // Gap before Adopt Now button
            }
        }
    }

    // Full screen image overlay OUTSIDE Scaffold
    if (fullScreenImageIndex != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f))
                .clickable { fullScreenImageIndex = null },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { /* prevent close when clicking image */ },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = petImages[fullScreenImageIndex!!]),
                    contentDescription = "Full Image",
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .aspectRatio(1f)
                )
            }
        }
    }
} 
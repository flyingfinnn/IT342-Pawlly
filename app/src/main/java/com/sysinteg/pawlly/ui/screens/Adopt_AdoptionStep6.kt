package com.sysinteg.pawlly.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sysinteg.pawlly.ui.theme.Purple
import com.sysinteg.pawlly.ui.theme.White
import com.sysinteg.pawlly.ui.theme.Inter
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdoptAdoptionStep6Screen(
    onContinue: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    var hasAllergies by remember { mutableStateOf<Boolean?>(null) }
    var hasOtherPets by remember { mutableStateOf<Boolean?>(null) }
    var otherPetsDetails by remember { mutableStateOf("") }
    var neutered by remember { mutableStateOf("") }
    var vaccinated by remember { mutableStateOf("") }
    var experience by remember { mutableStateOf("") }

    val yesNoNa = listOf("Yes", "No", "N/A")

    Scaffold(
        containerColor = White,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, start = 8.dp, end = 8.dp, bottom = 8.dp)
            ) {
                Text(
                    "Step 6 of 7",
                    color = Purple,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    fontFamily = Inter,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f)) {
                    Text("Back", fontFamily = Inter, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = onContinue,
                    colors = ButtonDefaults.buttonColors(containerColor = Purple),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Continue", color = White, fontFamily = Inter, fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Other Animals & Experience", fontSize = 22.sp, color = Purple, fontFamily = Inter, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Any allergies in household?", fontSize = 16.sp, fontFamily = Inter, fontWeight = FontWeight.Bold)
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                RadioButton(
                    selected = hasAllergies == true,
                    onClick = { hasAllergies = true }
                )
                Text("Yes", fontFamily = Inter, fontWeight = FontWeight.Bold)
                RadioButton(
                    selected = hasAllergies == false,
                    onClick = { hasAllergies = false }
                )
                Text("No", fontFamily = Inter, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Other pets?", fontSize = 16.sp, fontFamily = Inter, fontWeight = FontWeight.Bold)
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                RadioButton(
                    selected = hasOtherPets == true,
                    onClick = { hasOtherPets = true }
                )
                Text("Yes", fontFamily = Inter, fontWeight = FontWeight.Bold)
                RadioButton(
                    selected = hasOtherPets == false,
                    onClick = { hasOtherPets = false }
                )
                Text("No", fontFamily = Inter, fontWeight = FontWeight.Bold)
            }
            if (hasOtherPets == true) {
                OutlinedTextField(
                    value = otherPetsDetails,
                    onValueChange = { otherPetsDetails = it },
                    label = { Text("Species, age, gender", fontFamily = Inter, fontWeight = FontWeight.Bold) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Neutered?", fontSize = 16.sp, fontFamily = Inter, fontWeight = FontWeight.Bold)
            var neuteredExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = neuteredExpanded,
                onExpandedChange = { neuteredExpanded = !neuteredExpanded }
            ) {
                OutlinedTextField(
                    value = neutered,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Neutered?", fontFamily = Inter, fontWeight = FontWeight.Bold) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = neuteredExpanded,
                    onDismissRequest = { neuteredExpanded = false }
                ) {
                    yesNoNa.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option, fontFamily = Inter, fontWeight = FontWeight.Bold) },
                            onClick = {
                                neutered = option
                                neuteredExpanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Vaccinated?", fontSize = 16.sp, fontFamily = Inter, fontWeight = FontWeight.Bold)
            var vaccinatedExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = vaccinatedExpanded,
                onExpandedChange = { vaccinatedExpanded = !vaccinatedExpanded }
            ) {
                OutlinedTextField(
                    value = vaccinated,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Vaccinated?", fontFamily = Inter, fontWeight = FontWeight.Bold) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = vaccinatedExpanded,
                    onDismissRequest = { vaccinatedExpanded = false }
                ) {
                    yesNoNa.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option, fontFamily = Inter, fontWeight = FontWeight.Bold) },
                            onClick = {
                                vaccinated = option
                                vaccinatedExpanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = experience,
                onValueChange = { experience = it },
                label = { Text("Describe your experience with animals", fontFamily = Inter, fontWeight = FontWeight.Bold) },
                modifier = Modifier.fillMaxWidth().height(100.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
} 
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
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdoptAdoptionStep3Screen(
    onContinue: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    var hasGarden by remember { mutableStateOf<Boolean?>(null) }
    var homeType by remember { mutableStateOf("") }
    var householdSetting by remember { mutableStateOf("") }
    var activityLevel by remember { mutableStateOf("") }

    val homeTypes = listOf("House", "Apartment", "Bungalow", "Other")
    val householdSettings = listOf("Quiet", "Moderate", "Busy")
    val activityLevels = listOf("Low", "Medium", "High")

    Scaffold(
        containerColor = White,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .systemBarsPadding()
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Step 3 of 7",
                        color = Purple,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        fontFamily = Inter,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
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
            Text("Home Details", fontSize = 22.sp, color = Purple, fontFamily = Inter, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(24.dp))
            Text("Do you have a garden?", fontSize = 16.sp, fontFamily = Inter, fontWeight = FontWeight.Bold, color = Color.Black)
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                RadioButton(
                    selected = hasGarden == true,
                    onClick = { hasGarden = true },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Purple,
                        unselectedColor = Color.Gray
                    )
                )
                Text("Yes", fontFamily = Inter, fontWeight = FontWeight.Bold, color = Color.Black)
                RadioButton(
                    selected = hasGarden == false,
                    onClick = { hasGarden = false },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Purple,
                        unselectedColor = Color.Gray
                    )
                )
                Text("No", fontFamily = Inter, fontWeight = FontWeight.Bold, color = Color.Black)
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Home type dropdown
            var homeTypeExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = homeTypeExpanded,
                onExpandedChange = { homeTypeExpanded = !homeTypeExpanded }
            ) {
                OutlinedTextField(
                    value = homeType,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Home Type", fontFamily = Inter, fontWeight = FontWeight.Bold, color = Color.Black) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black, fontFamily = Inter),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Purple,
                        unfocusedBorderColor = Color.Gray
                    )
                )
                ExposedDropdownMenu(
                    expanded = homeTypeExpanded,
                    onDismissRequest = { homeTypeExpanded = false }
                ) {
                    homeTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type, fontFamily = Inter, fontWeight = FontWeight.Bold) },
                            onClick = {
                                homeType = type
                                homeTypeExpanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Household setting dropdown
            var householdSettingExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = householdSettingExpanded,
                onExpandedChange = { householdSettingExpanded = !householdSettingExpanded }
            ) {
                OutlinedTextField(
                    value = householdSetting,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Household Setting", fontFamily = Inter, fontWeight = FontWeight.Bold, color = Color.Black) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black, fontFamily = Inter),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Purple,
                        unfocusedBorderColor = Color.Gray
                    )
                )
                ExposedDropdownMenu(
                    expanded = householdSettingExpanded,
                    onDismissRequest = { householdSettingExpanded = false }
                ) {
                    householdSettings.forEach { setting ->
                        DropdownMenuItem(
                            text = { Text(setting, fontFamily = Inter, fontWeight = FontWeight.Bold) },
                            onClick = {
                                householdSetting = setting
                                householdSettingExpanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Activity level dropdown
            var activityLevelExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = activityLevelExpanded,
                onExpandedChange = { activityLevelExpanded = !activityLevelExpanded }
            ) {
                OutlinedTextField(
                    value = activityLevel,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Activity Level", fontFamily = Inter, fontWeight = FontWeight.Bold, color = Color.Black) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black, fontFamily = Inter),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Purple,
                        unfocusedBorderColor = Color.Gray
                    )
                )
                ExposedDropdownMenu(
                    expanded = activityLevelExpanded,
                    onDismissRequest = { activityLevelExpanded = false }
                ) {
                    activityLevels.forEach { level ->
                        DropdownMenuItem(
                            text = { Text(level, fontFamily = Inter, fontWeight = FontWeight.Bold) },
                            onClick = {
                                activityLevel = level
                                activityLevelExpanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
} 
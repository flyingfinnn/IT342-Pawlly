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
fun AdoptAdoptionStep5Screen(
    onContinue: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    var numAdults by remember { mutableStateOf(0) }
    var numChildren by remember { mutableStateOf(0) }
    var youngestChildAge by remember { mutableStateOf("") }
    var visitingChildren by remember { mutableStateOf<Boolean?>(null) }
    var visitingChildAge by remember { mutableStateOf("") }
    var hasFlatmates by remember { mutableStateOf<Boolean?>(null) }

    val childAges = (0..18).map { "$it" }

    Scaffold(
        containerColor = White,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, start = 8.dp, end = 8.dp, bottom = 8.dp)
            ) {
                Text(
                    "Step 5 of 7",
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
            Text("People in Home", fontSize = 22.sp, color = Purple, fontFamily = Inter, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("# of Adults:", modifier = Modifier.weight(1f), fontFamily = Inter, fontWeight = FontWeight.Bold)
                IconButton(onClick = { if (numAdults > 0) numAdults-- }) { Text("-", fontFamily = Inter, fontWeight = FontWeight.Bold) }
                Text(numAdults.toString(), fontFamily = Inter, fontWeight = FontWeight.Bold)
                IconButton(onClick = { numAdults++ }) { Text("+", fontFamily = Inter, fontWeight = FontWeight.Bold) }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("# of Children:", modifier = Modifier.weight(1f), fontFamily = Inter, fontWeight = FontWeight.Bold)
                IconButton(onClick = { if (numChildren > 0) numChildren-- }) { Text("-", fontFamily = Inter, fontWeight = FontWeight.Bold) }
                Text(numChildren.toString(), fontFamily = Inter, fontWeight = FontWeight.Bold)
                IconButton(onClick = { numChildren++ }) { Text("+", fontFamily = Inter, fontWeight = FontWeight.Bold) }
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (numChildren > 0) {
                var ageDropdownExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = ageDropdownExpanded,
                    onExpandedChange = { ageDropdownExpanded = !ageDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = youngestChildAge,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Age of Youngest Child", fontFamily = Inter, fontWeight = FontWeight.Bold) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = ageDropdownExpanded,
                        onDismissRequest = { ageDropdownExpanded = false }
                    ) {
                        childAges.forEach { age ->
                            DropdownMenuItem(
                                text = { Text(age, fontFamily = Inter, fontWeight = FontWeight.Bold) },
                                onClick = {
                                    youngestChildAge = age
                                    ageDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Visiting children?", fontSize = 16.sp, fontFamily = Inter, fontWeight = FontWeight.Bold)
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                RadioButton(
                    selected = visitingChildren == true,
                    onClick = { visitingChildren = true }
                )
                Text("Yes", fontFamily = Inter, fontWeight = FontWeight.Bold)
                RadioButton(
                    selected = visitingChildren == false,
                    onClick = { visitingChildren = false }
                )
                Text("No", fontFamily = Inter, fontWeight = FontWeight.Bold)
            }
            if (visitingChildren == true) {
                var visitingAgeDropdownExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = visitingAgeDropdownExpanded,
                    onExpandedChange = { visitingAgeDropdownExpanded = !visitingAgeDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = visitingChildAge,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Age of Visiting Child", fontFamily = Inter, fontWeight = FontWeight.Bold) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = visitingAgeDropdownExpanded,
                        onDismissRequest = { visitingAgeDropdownExpanded = false }
                    ) {
                        childAges.forEach { age ->
                            DropdownMenuItem(
                                text = { Text(age, fontFamily = Inter, fontWeight = FontWeight.Bold) },
                                onClick = {
                                    visitingChildAge = age
                                    visitingAgeDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Any flatmates/lodgers?", fontSize = 16.sp, fontFamily = Inter, fontWeight = FontWeight.Bold)
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                RadioButton(
                    selected = hasFlatmates == true,
                    onClick = { hasFlatmates = true }
                )
                Text("Yes", fontFamily = Inter, fontWeight = FontWeight.Bold)
                RadioButton(
                    selected = hasFlatmates == false,
                    onClick = { hasFlatmates = false }
                )
                Text("No", fontFamily = Inter, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
} 
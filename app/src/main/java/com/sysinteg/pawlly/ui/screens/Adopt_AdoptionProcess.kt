package com.sysinteg.pawlly.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sysinteg.pawlly.ui.theme.Purple
import com.sysinteg.pawlly.ui.theme.White
import com.sysinteg.pawlly.userApi
import com.sysinteg.pawlly.utils.Constants.PAWLLY_PREFS
import com.sysinteg.pawlly.utils.Constants.KEY_JWT_TOKEN
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdoptionProcessScreen(
    onBack: () -> Unit,
    onSubmit: (
        personal: PersonalInfoState,
        household: HouseholdInfoState,
        lifestyle: LifestyleExperienceState
    ) -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var showError by rememberSaveable { mutableStateOf(false) }

    // Personal Info State
    var personalState by rememberSaveable(stateSaver = PersonalInfoStateSaver) { mutableStateOf(PersonalInfoState()) }
    var isLoading by remember { mutableStateOf(false) }
    var fetchError by remember { mutableStateOf("") }

    // Household State
    var householdState by rememberSaveable(stateSaver = HouseholdInfoStateSaver) { mutableStateOf(HouseholdInfoState()) }

    // Lifestyle State
    var lifestyleState by rememberSaveable(stateSaver = LifestyleExperienceStateSaver) { mutableStateOf(LifestyleExperienceState()) }

    // Fetch user info for personal section
    LaunchedEffect(Unit) {
        isLoading = true
        fetchError = ""
        try {
            val prefs = context.getSharedPreferences(PAWLLY_PREFS, 0)
            val token = prefs.getString(KEY_JWT_TOKEN, null)
            if (token != null) {
                val user = withContext(Dispatchers.IO) { userApi.getMe("Bearer $token") }
                personalState = personalState.copy(
                    fullName = listOfNotNull(user.firstName, user.lastName).joinToString(" ").trim(),
                    address = user.address ?: "",
                    contactNumber = user.phoneNumber ?: "",
                    email = user.email ?: ""
                )
            }
        } catch (e: Exception) {
            fetchError = e.message ?: "Failed to fetch user info"
        }
        isLoading = false
    }

    fun allValid(): Boolean =
        personalState.isValid() && householdState.isValid() && lifestyleState.isValid()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
            .verticalScroll(scrollState)
            .statusBarsPadding()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(Modifier.height(8.dp))
        // --- Personal Info ---
        Text(
            "Confirm your details",
            style = MaterialTheme.typography.titleLarge,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = personalState.fullName,
            onValueChange = {},
            label = { Text("Full Name", color = Color.Black) },
            textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black),
            readOnly = true,
            isError = showError && personalState.fullName.isBlank(),
            modifier = Modifier.fillMaxWidth()
        )
        if (showError && personalState.fullName.isBlank()) {
            Text("Full Name is required", color = Color.Red, fontSize = 13.sp)
        }
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = personalState.address,
            onValueChange = {},
            label = { Text("Address", color = Color.Black) },
            textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black),
            readOnly = true,
            isError = showError && personalState.address.isBlank(),
            modifier = Modifier.fillMaxWidth()
        )
        if (showError && personalState.address.isBlank()) {
            Text("Address is required", color = Color.Red, fontSize = 13.sp)
        }
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = personalState.contactNumber,
            onValueChange = {},
            label = { Text("Contact Number", color = Color.Black) },
            textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black),
            readOnly = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            isError = showError && personalState.contactNumber.isBlank(),
            modifier = Modifier.fillMaxWidth()
        )
        if (showError && personalState.contactNumber.isBlank()) {
            Text("Contact Number is required", color = Color.Red, fontSize = 13.sp)
        }
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = personalState.email,
            onValueChange = {},
            label = { Text("Email Address", color = Color.Black) },
            textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black),
            readOnly = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = showError && personalState.email.isBlank(),
            modifier = Modifier.fillMaxWidth()
        )
        if (showError && personalState.email.isBlank()) {
            Text("Email Address is required", color = Color.Red, fontSize = 13.sp)
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = "If any of these details are incorrect, please update your profile before proceeding.",
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp)
        )
        Spacer(Modifier.height(32.dp))
        // --- Household Info ---
        Text("Tell us about your household", style = MaterialTheme.typography.titleLarge, color = Color.Black)
        Text("Do you own or rent?", style = MaterialTheme.typography.bodyLarge, color = Color.Black)
        Row(
            Modifier.padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            RadioButton(
                selected = householdState.ownOrRent == "Own",
                onClick = { householdState = householdState.copy(ownOrRent = "Own") }
            )
            Text("Own", Modifier.align(Alignment.CenterVertically).padding(end = 16.dp), color = Color.Black)
            RadioButton(
                selected = householdState.ownOrRent == "Rent",
                onClick = { householdState = householdState.copy(ownOrRent = "Rent") }
            )
            Text("Rent", Modifier.align(Alignment.CenterVertically), color = Color.Black)
        }
        if (showError && householdState.ownOrRent.isBlank()) {
            Text("Ownership is required", color = Color.Red, fontSize = 13.sp)
        }
        Spacer(Modifier.height(16.dp))
        var residenceDropdownExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = residenceDropdownExpanded,
            onExpandedChange = { residenceDropdownExpanded = !residenceDropdownExpanded }
        ) {
            OutlinedTextField(
                value = householdState.residenceType,
                onValueChange = {},
                readOnly = true,
                label = { Text("Type of Residence", color = Color.Black) },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                isError = showError && householdState.residenceType.isBlank(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedBorderColor = Purple,
                    unfocusedBorderColor = Color.Gray,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )
            ExposedDropdownMenu(
                expanded = residenceDropdownExpanded,
                onDismissRequest = { residenceDropdownExpanded = false },
                modifier = Modifier.background(Color.White)
            ) {
                listOf("Apartment", "House", "Farm", "Other").forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option, color = Color.Black) },
                        onClick = {
                            householdState = householdState.copy(residenceType = option)
                            residenceDropdownExpanded = false
                        }
                    )
                }
            }
        }
        if (showError && householdState.residenceType.isBlank()) {
            Text("Type of Residence is required", color = Color.Red, fontSize = 13.sp)
        }
        Spacer(Modifier.height(16.dp))
        Text("Do you have a fenced yard?", style = MaterialTheme.typography.bodyLarge, color = Color.Black)
        Row(
            Modifier.padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            RadioButton(
                selected = householdState.fencedYard == true,
                onClick = { householdState = householdState.copy(fencedYard = true) }
            )
            Text("Yes", Modifier.align(Alignment.CenterVertically).padding(end = 16.dp), color = Color.Black)
            RadioButton(
                selected = householdState.fencedYard == false,
                onClick = { householdState = householdState.copy(fencedYard = false) }
            )
            Text("No", Modifier.align(Alignment.CenterVertically), color = Color.Black)
        }
        if (showError && householdState.fencedYard == null) {
            Text("Fenced yard selection is required", color = Color.Red, fontSize = 13.sp)
        }
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = householdState.numAdults,
            onValueChange = { if (it.all { c -> c.isDigit() }) householdState = householdState.copy(numAdults = it) },
            label = { Text("Number of adults in household", color = Color.Black) },
            isError = showError && householdState.numAdults.isBlank(),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedBorderColor = Purple,
                unfocusedBorderColor = Color.Gray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
        if (showError && householdState.numAdults.isBlank()) {
            Text("Number of adults is required", color = Color.Red, fontSize = 13.sp)
        }
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = householdState.numChildren,
            onValueChange = { if (it.all { c -> c.isDigit() }) householdState = householdState.copy(numChildren = it) },
            label = { Text("Number of children in household", color = Color.Black) },
            isError = showError && householdState.numChildren.isBlank(),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedBorderColor = Purple,
                unfocusedBorderColor = Color.Gray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
        if (showError && householdState.numChildren.isBlank()) {
            Text("Number of children is required", color = Color.Red, fontSize = 13.sp)
        }
        Spacer(Modifier.height(16.dp))
        Text("Any pet allergies?", style = MaterialTheme.typography.bodyLarge, color = Color.Black)
        Row(
            Modifier.padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            RadioButton(
                selected = householdState.petAllergies == true,
                onClick = { householdState = householdState.copy(petAllergies = true) }
            )
            Text("Yes", Modifier.align(Alignment.CenterVertically).padding(end = 16.dp), color = Color.Black)
            RadioButton(
                selected = householdState.petAllergies == false,
                onClick = { householdState = householdState.copy(petAllergies = false) }
            )
            Text("No", Modifier.align(Alignment.CenterVertically), color = Color.Black)
        }
        if (showError && householdState.petAllergies == null) {
            Text("Pet allergies selection is required", color = Color.Red, fontSize = 13.sp)
        }
        Spacer(Modifier.height(32.dp))
        // --- Lifestyle & Experience ---
        Text("Tell us about your lifestyle & experience", style = MaterialTheme.typography.titleLarge, color = Color.Black)
        Text("Have you owned this pet type before?", style = MaterialTheme.typography.bodyLarge, color = Color.Black)
        Row(Modifier.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = lifestyleState.ownedBefore == true,
                onClick = { lifestyleState = lifestyleState.copy(ownedBefore = true) }
            )
            Text("Yes", Modifier.padding(end = 16.dp), color = Color.Black)
            RadioButton(
                selected = lifestyleState.ownedBefore == false,
                onClick = { lifestyleState = lifestyleState.copy(ownedBefore = false) }
            )
            Text("No", color = Color.Black)
        }
        if (showError && lifestyleState.ownedBefore == null) {
            Text("Owned before selection is required", color = Color.Red, fontSize = 13.sp)
        }
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = lifestyleState.hoursAlone,
            onValueChange = { if (it.all { c -> c.isDigit() }) lifestyleState = lifestyleState.copy(hoursAlone = it) },
            label = { Text("How many hours per day will the pet be alone?", color = Color.Black) },
            isError = showError && lifestyleState.hoursAlone.isBlank(),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedBorderColor = Purple,
                unfocusedBorderColor = Color.Gray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
        if (showError && lifestyleState.hoursAlone.isBlank()) {
            Text("Hours alone is required", color = Color.Red, fontSize = 13.sp)
        }
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = lifestyleState.whereDay,
            onValueChange = { lifestyleState = lifestyleState.copy(whereDay = it) },
            label = { Text("Where will the pet stay during the day?", color = Color.Black) },
            isError = showError && lifestyleState.whereDay.isBlank(),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedBorderColor = Purple,
                unfocusedBorderColor = Color.Gray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
        if (showError && lifestyleState.whereDay.isBlank()) {
            Text("Where the pet stays during the day is required", color = Color.Red, fontSize = 13.sp)
        }
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = lifestyleState.whereNight,
            onValueChange = { lifestyleState = lifestyleState.copy(whereNight = it) },
            label = { Text("Where will the pet sleep at night?", color = Color.Black) },
            isError = showError && lifestyleState.whereNight.isBlank(),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedBorderColor = Purple,
                unfocusedBorderColor = Color.Gray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
        if (showError && lifestyleState.whereNight.isBlank()) {
            Text("Where the pet sleeps at night is required", color = Color.Red, fontSize = 13.sp)
        }
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = lifestyleState.exercisePlans,
            onValueChange = { lifestyleState = lifestyleState.copy(exercisePlans = it) },
            label = { Text("Plans for exercise/stimulation", color = Color.Black) },
            isError = showError && lifestyleState.exercisePlans.isBlank(),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedBorderColor = Purple,
                unfocusedBorderColor = Color.Gray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
        if (showError && lifestyleState.exercisePlans.isBlank()) {
            Text("Exercise plans are required", color = Color.Red, fontSize = 13.sp)
        }
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = lifestyleState.reasonForAdoption,
            onValueChange = { lifestyleState = lifestyleState.copy(reasonForAdoption = it) },
            label = { Text("Why do you want to adopt?", color = Color.Black) },
            isError = showError && lifestyleState.reasonForAdoption.isBlank(),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedBorderColor = Purple,
                unfocusedBorderColor = Color.Gray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
        if (showError && lifestyleState.reasonForAdoption.isBlank()) {
            Text("Reason for adoption is required", color = Color.Red, fontSize = 13.sp)
        }
        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
            Text("Do you have other pets?", style = MaterialTheme.typography.bodyLarge, color = Color.Black)
            Spacer(Modifier.width(16.dp))
            Switch(
                checked = lifestyleState.hasOtherPets,
                onCheckedChange = { lifestyleState = lifestyleState.copy(hasOtherPets = it) }
            )
        }
        Spacer(Modifier.height(32.dp))
        // --- Buttons ---
        Row(
            Modifier.fillMaxWidth().padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = onBack,
                enabled = true,
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White,
                    contentColor = Purple
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, Purple)
            ) { Text("Back", color = Purple) }
            Button(
                onClick = {
                    onSubmit(personalState, householdState, lifestyleState)
                },
                enabled = allValid(),
                colors = ButtonDefaults.buttonColors(containerColor = Purple)
            ) { Text("Submit", color = White) }
        }
    }
}

// --- State Classes and Savers ---
data class PersonalInfoState(
    val fullName: String = "",
    val address: String = "",
    val contactNumber: String = "",
    val email: String = ""
) {
    fun isValid(): Boolean = fullName.isNotBlank() && address.isNotBlank() && contactNumber.isNotBlank() && email.isNotBlank()
}

val PersonalInfoStateSaver = androidx.compose.runtime.saveable.listSaver<PersonalInfoState, Any>(
    save = { listOf(it.fullName, it.address, it.contactNumber, it.email) },
    restore = {
        PersonalInfoState(
            fullName = it[0] as String,
            address = it[1] as String,
            contactNumber = it[2] as String,
            email = it[3] as String
        )
    }
)

data class HouseholdInfoState(
    val ownOrRent: String = "",
    val residenceType: String = "",
    val residenceDropdownExpanded: Boolean = false,
    val fencedYard: Boolean? = null,
    val numAdults: String = "",
    val numChildren: String = "",
    val petAllergies: Boolean? = null
) {
    fun isValid(): Boolean = ownOrRent.isNotBlank() && residenceType.isNotBlank() && fencedYard != null && numAdults.isNotBlank() && numChildren.isNotBlank() && petAllergies != null
}

val HouseholdInfoStateSaver = androidx.compose.runtime.saveable.listSaver<HouseholdInfoState, Any>(
    save = {
        listOf(
            it.ownOrRent,
            it.residenceType,
            it.residenceDropdownExpanded,
            it.fencedYard ?: false,
            it.numAdults,
            it.numChildren,
            it.petAllergies ?: false
        )
    },
    restore = {
        HouseholdInfoState(
            ownOrRent = it[0] as String,
            residenceType = it[1] as String,
            residenceDropdownExpanded = it[2] as Boolean,
            fencedYard = it[3] as Boolean,
            numAdults = it[4] as String,
            numChildren = it[5] as String,
            petAllergies = it[6] as Boolean
        )
    }
)

data class LifestyleExperienceState(
    val ownedBefore: Boolean? = null,
    val hoursAlone: String = "",
    val whereDay: String = "",
    val whereNight: String = "",
    val exercisePlans: String = "",
    val reasonForAdoption: String = "",
    val hasOtherPets: Boolean = false
) {
    fun isValid(): Boolean = ownedBefore != null && hoursAlone.isNotBlank() && whereDay.isNotBlank() && whereNight.isNotBlank() && exercisePlans.isNotBlank() && reasonForAdoption.isNotBlank()
}

val LifestyleExperienceStateSaver = androidx.compose.runtime.saveable.listSaver<LifestyleExperienceState, Any>(
    save = {
        listOf(
            it.ownedBefore ?: false,
            it.hoursAlone,
            it.whereDay,
            it.whereNight,
            it.exercisePlans,
            it.reasonForAdoption,
            it.hasOtherPets
        )
    },
    restore = {
        LifestyleExperienceState(
            ownedBefore = it[0] as Boolean,
            hoursAlone = it[1] as String,
            whereDay = it[2] as String,
            whereNight = it[3] as String,
            exercisePlans = it[4] as String,
            reasonForAdoption = it[5] as String,
            hasOtherPets = it[6] as Boolean
        )
    }
)

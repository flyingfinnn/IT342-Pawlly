package com.sysinteg.pawlly.ui.screens

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.sysinteg.pawlly.UserApi
import com.sysinteg.pawlly.AdoptionApplicationRequest
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

@HiltViewModel
class AdoptionViewModel @Inject constructor(
    private val userApi: UserApi
) : ViewModel() {
    private val _personalInfo = MutableStateFlow(PersonalInfoState())
    val personalInfo: StateFlow<PersonalInfoState> = _personalInfo

    private val _householdInfo = MutableStateFlow(HouseholdInfoState())
    val householdInfo: StateFlow<HouseholdInfoState> = _householdInfo

    private val _lifestyle = MutableStateFlow(LifestyleExperienceState())
    val lifestyle: StateFlow<LifestyleExperienceState> = _lifestyle

    fun setPersonalInfo(state: PersonalInfoState) { _personalInfo.value = state }
    fun setHouseholdInfo(state: HouseholdInfoState) { _householdInfo.value = state }
    fun setLifestyle(state: LifestyleExperienceState) { _lifestyle.value = state }

    fun submitAdoptionApplication(
        userId: Long,
        petId: Int,
        petName: String?,
        onResult: (Boolean) -> Unit
    ) {
        val personal = _personalInfo.value
        val household = _householdInfo.value
        val lifestyle = _lifestyle.value
        viewModelScope.launch {
            try {
                android.util.Log.d("AdoptionViewModel", "Submitting: ownOrRent=${household.ownOrRent}, residenceType=${household.residenceType}, numAdults=${household.numAdults}, numChildren=${household.numChildren}, otherPets=${lifestyle.hasOtherPets}, ownedBefore=${lifestyle.ownedBefore}, hoursAlone=${lifestyle.hoursAlone}, whereDay=${lifestyle.whereDay}, whereNight=${lifestyle.whereNight}, exercisePlans=${lifestyle.exercisePlans}, reasonForAdoption=${lifestyle.reasonForAdoption}")
                val request = AdoptionApplicationRequest(
                    userId = userId,
                    petId = petId,
                    petName = petName ?: "",
                    householdType = household.residenceType.ifBlank { "Unknown" },
                    householdOwnership = household.ownOrRent.ifBlank { "Unknown" },
                    numAdults = household.numAdults.toIntOrNull() ?: 1,
                    numChildren = household.numChildren.toIntOrNull() ?: 0,
                    otherPets = lifestyle.hasOtherPets,
                    experienceWithPets = if (lifestyle.ownedBefore == true) "Yes" else "No",
                    dailyRoutine = "Day: ${lifestyle.whereDay}, Night: ${lifestyle.whereNight}, Exercise: ${lifestyle.exercisePlans}",
                    hoursAlonePerDay = lifestyle.hoursAlone.toIntOrNull(),
                    reasonForAdoption = lifestyle.reasonForAdoption
                )
                val response = userApi.submitAdoptionApplication(request)
                onResult(response.isSuccessful)
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }
} 
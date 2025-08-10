package com.saatco.murshadik.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saatco.murshadik.PestIdentificationService
import com.saatco.murshadik.api.APIHelper
import com.saatco.murshadik.models.Disease
import com.saatco.murshadik.models.Plant
import com.saatco.murshadik.ui.DiseaseDatabaseState
import kotlinx.coroutines.launch

class DiseaseDatabaseViewModel : ViewModel() {

    private val service = PestIdentificationService()

    private val _uiState = MutableLiveData<DiseaseDatabaseState>()
    val uiState: LiveData<DiseaseDatabaseState> = _uiState

    private val _plants = MutableLiveData<List<Plant>>()
    val plants: LiveData<List<Plant>> = _plants

    private val _diseases = MutableLiveData<List<Disease>>()
    val diseases: LiveData<List<Disease>> = _diseases

    init {
        fetchPlants()
    }

    private fun fetchPlants() {
        _uiState.value = DiseaseDatabaseState.Loading
        viewModelScope.launch {
            try {
                val response = APIHelper.safeApiCall { service.getPlants() }
                val placeholder = Plant(-1, "Select Plant", "اختر النبات", "", "", "")
                _plants.value = listOf(placeholder) + response.items
                _uiState.value = DiseaseDatabaseState.Idle
            } catch (e: Exception) {
                _uiState.value = DiseaseDatabaseState.Error("Failed to load plants")
                _plants.value = emptyList()
            }
        }
    }

    fun onPlantSelected(plantId: Int) {
        if (plantId == -1) {
            _diseases.value = emptyList()
            return
        }
        _uiState.value = DiseaseDatabaseState.Loading
        viewModelScope.launch {
            try {
                val response = APIHelper.safeApiCall { service.getDiseasesForPlant(plantId) }
                val placeholder = Disease(
                    -1, "Select Disease", "اختر المرض",
                    "", "", "", "", emptyList(), "", ""
                )
                _diseases.value = listOf(placeholder) + response.items
                _uiState.value = DiseaseDatabaseState.Idle
            } catch (e: Exception) {
                _uiState.value = DiseaseDatabaseState.Error("Failed to load diseases for this plant")
                _diseases.value = emptyList()
            }
        }
    }
}
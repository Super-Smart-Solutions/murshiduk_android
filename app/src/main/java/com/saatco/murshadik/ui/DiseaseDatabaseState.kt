package com.saatco.murshadik.ui

sealed class DiseaseDatabaseState {
    object Loading : DiseaseDatabaseState()
    object Idle : DiseaseDatabaseState()
    data class Error(val message: String) : DiseaseDatabaseState()
}
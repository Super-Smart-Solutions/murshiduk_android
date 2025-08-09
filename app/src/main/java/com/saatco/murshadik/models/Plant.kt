package com.saatco.murshadik.models

import com.google.gson.annotations.SerializedName

data class Plant(
    val id: Int,
    @SerializedName("english_name")
    val englishName: String,
    @SerializedName("arabic_name")
    val arabicName: String
)

data class PlantListResponse(
    val items: List<Plant>
)
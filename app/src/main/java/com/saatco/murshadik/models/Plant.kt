package com.saatco.murshadik.models

import com.google.gson.annotations.SerializedName

data class Plant(
    val id: Int,
    @SerializedName("english_name")
    val englishName: String,
    @SerializedName("arabic_name")
    val arabicName: String,

    @SerializedName("scientific_name")
    val scientificName: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String
)

data class PlantListResponse(
    val items: List<Plant>
)
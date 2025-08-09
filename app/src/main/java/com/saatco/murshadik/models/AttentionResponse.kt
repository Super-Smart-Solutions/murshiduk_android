package com.saatco.murshadik.models

import com.google.gson.annotations.SerializedName

data class AttentionResponse(
    @SerializedName("attention_map_url")
    val attentionMapUrl: String?
)
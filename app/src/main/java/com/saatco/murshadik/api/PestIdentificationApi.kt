package com.saatco.murshadik.api

import com.saatco.murshadik.models.Disease
import com.saatco.murshadik.models.PlantListResponse
import com.saatco.murshadik.models.InferenceResponse
import com.saatco.murshadik.models.UploadImageResponse
import com.saatco.murshadik.models.AttentionResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface PestIdentificationApi {

    @Multipart
    @POST("images/uploads")
    suspend fun uploadImage(
        @Part imageFile: MultipartBody.Part,
        @Part name: MultipartBody.Part,
        @Part plantId: MultipartBody.Part,
        @Part farmId: MultipartBody.Part,
        @Part annotated: MultipartBody.Part
    ): Response<UploadImageResponse>

    @POST("inferences")
    suspend fun createInference(
        @Query("image_id") imageId: Int
    ): Response<InferenceResponse>

    @POST("inferences/{id}/validate")
    suspend fun validateInference(
        @Path("id") id: Int
    ): Response<InferenceResponse>

    @POST("inferences/{id}/detect")
    suspend fun detectDisease(
        @Path("id") id: Int
    ): Response<InferenceResponse>

    @GET("diseases/{id}")
    suspend fun getDiseaseById(
        @Path("id") id: Int
    ): Response<Disease>

    @GET("plants")
    suspend fun getPlants(
        @Query("pageSize") pageSize: Int = 10
    ): Response<PlantListResponse>

    @POST("inferences/{id}/attention")
    suspend fun getAttentionMap(
        @Path("id") id: Int
    ): Response<AttentionResponse>
}
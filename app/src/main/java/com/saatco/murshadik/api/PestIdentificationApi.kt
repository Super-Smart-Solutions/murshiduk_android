package com.saatco.murshadik.api

import com.saatco.murshadik.models.Disease
import com.saatco.murshadik.models.InferenceResponse
import com.saatco.murshadik.models.UploadImageResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface PestIdentificationApi {

    @Multipart
    @POST("images/uploads")
    suspend fun uploadImage(
        @Header("Authorization") authHeader: String,
        @Part imageFile: MultipartBody.Part,
        @Part name: MultipartBody.Part,
        @Part plantId: MultipartBody.Part,
        @Part farmId: MultipartBody.Part,
        @Part annotated: MultipartBody.Part
    ): Response<UploadImageResponse>

    @POST("inferences")
    suspend fun createInference(
        @Header("Authorization") authHeader: String,
        @Query("image_id") imageId: Int
    ): Response<InferenceResponse>

    @POST("inferences/{id}/validate")
    suspend fun validateInference(
        @Header("Authorization") authHeader: String,
        @Path("id") id: Int
    ): Response<InferenceResponse>

    @POST("inferences/{id}/detect")
    suspend fun detectDisease(
        @Header("Authorization") authHeader: String,
        @Path("id") id: Int
    ): Response<InferenceResponse>

    @GET("diseases/{id}")
    suspend fun getDiseaseById(
        @Header("Authorization") authHeader: String,
        @Path("id") id: Int
    ): Response<Disease>
}
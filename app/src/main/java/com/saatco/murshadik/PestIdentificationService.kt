package com.saatco.murshadik

import android.content.Context
import android.net.Uri
import com.saatco.murshadik.api.APIClient
import com.saatco.murshadik.api.PestIdentificationApi
import com.saatco.murshadik.models.Disease
import com.saatco.murshadik.models.PlantListResponse
import com.saatco.murshadik.models.InferenceResponse
import com.saatco.murshadik.models.UploadImageResponse
import com.saatco.murshadik.models.AttentionResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import java.io.File

class PestIdentificationService {
    private val api: PestIdentificationApi = APIClient.getPestIdentificationClient().create(PestIdentificationApi::class.java)

    suspend fun uploadImage(
        file: File,
        originalUri: Uri,
        plantId: Int,
        context: Context
    ): Response<UploadImageResponse> {
        val originalFileName = file.name
        val fileNameWithoutExt = originalFileName.substringBeforeLast('.')
        val fileExt = originalFileName.substringAfterLast('.')
        val newFileName = "uploads/${fileNameWithoutExt}_${System.currentTimeMillis()}.$fileExt"

        val mimeType = context.contentResolver.getType(originalUri) ?: "image/jpeg"

        val imagePart = MultipartBody.Part.createFormData(
            "image_file",
            newFileName,
            file.asRequestBody(mimeType.toMediaTypeOrNull())
        )
        val namePart = MultipartBody.Part.createFormData("name", newFileName)
        val plantIdPart = MultipartBody.Part.createFormData("plant_id", plantId.toString())
        val farmIdPart = MultipartBody.Part.createFormData("farm_id", "1")
        val annotatedPart = MultipartBody.Part.createFormData("annotated", "false")

        return api.uploadImage(imagePart, namePart, plantIdPart, farmIdPart, annotatedPart)
    }

    suspend fun createInference(
        imageId: Int
    ): Response<InferenceResponse> {
        return api.createInference(imageId)
    }

    suspend fun validateInference(
        inferenceId: Int
    ): Response<InferenceResponse> {
        return api.validateInference(inferenceId)
    }

    suspend fun detectDisease(
        inferenceId: Int
    ): Response<InferenceResponse> {
        return api.detectDisease(inferenceId)
    }

    suspend fun getDiseaseById(
        diseaseId: Int
    ): Response<Disease> {
        return api.getDiseaseById(diseaseId)
    }

    suspend fun getPlants(
    ): Response<PlantListResponse> {
        return api.getPlants( )
    }

    suspend fun getAttentionMap(
        inferenceId: Int): Response<AttentionResponse> {
        return api.getAttentionMap(inferenceId)
    }
}
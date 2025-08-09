// In file: com/saatco/murshadik/viewmodels/PestIdentificationViewModel.kt
package com.saatco.murshadik.viewmodels

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saatco.murshadik.PestIdentificationService
import com.saatco.murshadik.R
import com.saatco.murshadik.api.APIHelper
import com.saatco.murshadik.ui.PestIdentificationState
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class PestIdentificationViewModel : ViewModel() {

    private val _uiState = MutableLiveData<PestIdentificationState>()
    val uiState: LiveData<PestIdentificationState> = _uiState

    private val pestIdentificationService = PestIdentificationService()
    // TODO: This will be replaced with a secure method
    private val authToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI3MzE3NDU2NS1hYWJjLTRjMTItYWQ1Yi1iOWUyNWRmYzY2MjUiLCJhdWQiOlsiZmFzdGFwaS11c2VyczphdXRoIl0sInR5cGUiOiJhY2Nlc3MiLCJleHAiOjE3NTQ3NDAxODh9.yBbJRmYjRvxSfHoUBvsJpENJZl71yvJ-4JcM5ANpq8g"

    init {
        // Set the initial state when the ViewModel is created
        _uiState.value = PestIdentificationState.Input() // <-- FIX 1: Added ()
    }

    fun startDiseaseDetection(imageUri: Uri, plantId: Int, context: Context) {
        viewModelScope.launch {
            try {
                // State: Compressing Image
                _uiState.value = PestIdentificationState.Loading(context.getString(R.string.checking_image))
                val compressedFile = compressImage(imageUri, context)

                // Step 1: Upload Image
                val uploadResponse = APIHelper.safeApiCall {
                    pestIdentificationService.uploadImage(authToken, compressedFile, imageUri, plantId, context)
                }
                val imageId = uploadResponse.id

                // Step 2: Create Inference
                val inferenceResponse = APIHelper.safeApiCall {
                    pestIdentificationService.createInference(authToken, imageId)
                }
                val inferenceId = inferenceResponse.id

                // Step 3: Validate Inference
                _uiState.value = PestIdentificationState.Loading(context.getString(R.string.detecting_disease))
                val validationResponse = APIHelper.safeApiCall {
                    pestIdentificationService.validateInference(authToken, inferenceId)
                }
                if (validationResponse.status != 1) { // Not IMAGE_VALID
                    throw IOException(context.getString(R.string.image_not_valid))
                }

                // Step 4: Detect Disease
                val detectionResponse = APIHelper.safeApiCall {
                    pestIdentificationService.detectDisease(authToken, inferenceId)
                }

                when (detectionResponse.status) {
                    2 -> { // DETECTION_COMPLETED
                        _uiState.value = PestIdentificationState.Loading(context.getString(R.string.getting_disease_details))
                        val disease = APIHelper.safeApiCall {
                            pestIdentificationService.getDiseaseById(authToken, detectionResponse.diseaseId)
                        }
                        _uiState.value = PestIdentificationState.Success(disease, detectionResponse.confidenceLevel, null)
                    }
                    -2 -> _uiState.value = PestIdentificationState.Inconclusive() // <-- FIX 2: Added ()
                    else -> throw IOException(context.getString(R.string.detection_failed))
                }

            } catch (e: Exception) {
                // Handle any error from the entire chain
                _uiState.value = PestIdentificationState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }

    fun resetState() {
        _uiState.value = PestIdentificationState.Input()
    }

    @Throws(IOException::class)
    private fun compressImage(imageUri: Uri, context: Context): File {
        val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
        val maxDim = 1024
        val width = bitmap.width
        val height = bitmap.height
        val scaledBitmap = if (width > maxDim || height > maxDim) {
            val scale = minOf(maxDim.toFloat() / width, maxDim.toFloat() / height)
            Bitmap.createScaledBitmap(bitmap, (scale * width).toInt(), (scale * height).toInt(), true)
        } else {
            bitmap
        }
        val compressedFile = File.createTempFile("compressed_", ".jpg", context.cacheDir)
        FileOutputStream(compressedFile).use { out ->
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out)
        }
        return compressedFile
    }
}
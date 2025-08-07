// In file: com/saatco/murshadik/viewmodels/PestIdentificationViewModel.java
package com.saatco.murshadik.viewmodels;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.saatco.murshadik.PestIdentificationService;
import com.saatco.murshadik.R;
import com.saatco.murshadik.models.Disease;
import com.saatco.murshadik.models.InferenceResponse;
import com.saatco.murshadik.models.UploadImageResponse;
import com.saatco.murshadik.ui.PestIdentificationState;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PestIdentificationViewModel extends ViewModel {

    private final MutableLiveData<PestIdentificationState> _uiState = new MutableLiveData<>();
    public LiveData<PestIdentificationState> uiState = _uiState;

    private final PestIdentificationService pestIdentificationService;
    private final String authToken; // This will be removed in a later step

    public PestIdentificationViewModel() {
        _uiState.setValue(new PestIdentificationState.Input());
        this.pestIdentificationService = new PestIdentificationService();
        // TODO: This will be replaced with a secure method
        this.authToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI3MzE3NDU2NS1hYWJjLTRjMTItYWQ1Yi1iOWUyNWRmYzY2MjUiLCJhdWQiOlsiZmFzdGFwaS11c2VyczphdXRoIl19.PVWELPTrzW7Y50Jw4GXTrBf7skvwJ1KkJ0iomqdXuqQ";
    }

    public void startDiseaseDetection(Uri imageUri, int plantId, Context context) {
        // Start by posting the Loading state
        _uiState.setValue(new PestIdentificationState.Loading(context.getString(R.string.checking_image)));

        File compressedFile;
        try {
            compressedFile = compressImage(imageUri, context);
        } catch (IOException e) {
            _uiState.setValue(new PestIdentificationState.Error("Image compression failed: " + e.getMessage()));
            return;
        }

        uploadImage(compressedFile, imageUri, plantId, context);
    }

    private void uploadImage(File file, Uri originalUri, int plantId, Context context) {
        String originalFileName = file.getName();
        String fileNameWithoutExt = originalFileName.substring(0, originalFileName.lastIndexOf('.'));
        String fileExt = originalFileName.substring(originalFileName.lastIndexOf('.') + 1);
        long timestamp = System.currentTimeMillis();
        String newFileName = "uploads/" + fileNameWithoutExt + "_" + timestamp + "." + fileExt;

        String mimeType = context.getContentResolver().getType(originalUri);
        if (mimeType == null || mimeType.isEmpty()) {
            mimeType = "image/jpeg"; // Fallback
        }

        RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), file);
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image_file", newFileName, requestFile);
        MultipartBody.Part namePart = MultipartBody.Part.createFormData("name", null, RequestBody.create(MediaType.parse("text/plain"), newFileName));
        MultipartBody.Part plantIdPart = MultipartBody.Part.createFormData("plant_id", null, RequestBody.create(MediaType.parse("text/plain"), String.valueOf(plantId)));
        MultipartBody.Part farmIdPart = MultipartBody.Part.createFormData("farm_id", null, RequestBody.create(MediaType.parse("text/plain"), "1"));
        MultipartBody.Part annotatedPart = MultipartBody.Part.createFormData("annotated", null, RequestBody.create(MediaType.parse("text/plain"), "false"));

        pestIdentificationService.uploadImage(authToken, imagePart, namePart, plantIdPart, farmIdPart, annotatedPart, new Callback<UploadImageResponse>() {
            @Override
            public void onResponse(Call<UploadImageResponse> call, Response<UploadImageResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    createInference(response.body().getId(), context);
                } else {
                    _uiState.setValue(new PestIdentificationState.Error("Image upload failed: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<UploadImageResponse> call, Throwable t) {
                _uiState.setValue(new PestIdentificationState.Error("Image upload error: " + t.getMessage()));
            }
        });
    }

    private void createInference(int imageId, Context context) {
        pestIdentificationService.createInference(authToken, imageId, new Callback<InferenceResponse>() {
            @Override
            public void onResponse(Call<InferenceResponse> call, Response<InferenceResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    validateInference(response.body().getId(), context);
                } else {
                    _uiState.setValue(new PestIdentificationState.Error("Inference creation failed: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<InferenceResponse> call, Throwable t) {
                _uiState.setValue(new PestIdentificationState.Error("Inference creation error: " + t.getMessage()));
            }
        });
    }

    private void validateInference(int inferenceId, Context context) {
        pestIdentificationService.validateInference(authToken, inferenceId, new Callback<InferenceResponse>() {
            @Override
            public void onResponse(Call<InferenceResponse> call, Response<InferenceResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getStatus() == 1) { // IMAGE_VALID
                        _uiState.setValue(new PestIdentificationState.Loading(context.getString(R.string.detecting_disease)));
                        detectDisease(inferenceId, context);
                    } else {
                        _uiState.setValue(new PestIdentificationState.Error(context.getString(R.string.image_not_valid)));
                    }
                } else {
                    _uiState.setValue(new PestIdentificationState.Error("Inference validation failed: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<InferenceResponse> call, Throwable t) {
                _uiState.setValue(new PestIdentificationState.Error("Inference validation error: " + t.getMessage()));
            }
        });
    }

    private void detectDisease(int inferenceId, Context context) {
        pestIdentificationService.detectDisease(authToken, inferenceId, new Callback<InferenceResponse>() {
            @Override
            public void onResponse(Call<InferenceResponse> call, Response<InferenceResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int status = response.body().getStatus();
                    if (status == 2) { // DETECTION_COMPLETED
                        _uiState.setValue(new PestIdentificationState.Loading(context.getString(R.string.getting_disease_details)));
                        getDiseaseDetails(response.body().getDiseaseId(), response.body().getConfidenceLevel(), context);
                    } else if (status == -2) { // DETECTION_INCONCLUSIVE
                        _uiState.setValue(new PestIdentificationState.Inconclusive());
                    } else {
                        _uiState.setValue(new PestIdentificationState.Error(context.getString(R.string.detection_failed)));
                    }
                } else {
                    _uiState.setValue(new PestIdentificationState.Error("Disease detection failed: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<InferenceResponse> call, Throwable t) {
                _uiState.setValue(new PestIdentificationState.Error("Disease detection error: " + t.getMessage()));
            }
        });
    }

    private void getDiseaseDetails(int diseaseId, double confidence, Context context) {
        pestIdentificationService.getDiseaseById(authToken, diseaseId, new Callback<Disease>() {
            @Override
            public void onResponse(Call<Disease> call, Response<Disease> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Finally, post the Success state
                    _uiState.setValue(new PestIdentificationState.Success(response.body(), confidence, null));
                } else {
                    _uiState.setValue(new PestIdentificationState.Error("Failed to get disease details: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<Disease> call, Throwable t) {
                _uiState.setValue(new PestIdentificationState.Error("Failed to get disease details error: " + t.getMessage()));
            }
        });
    }

    private File compressImage(Uri imageUri, Context context) throws IOException {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
        int maxDim = 1024;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (width > maxDim || height > maxDim) {
            float scale = Math.min((float) maxDim / width, (float) maxDim / height);
            bitmap = Bitmap.createScaledBitmap(bitmap, Math.round(scale * width), Math.round(scale * height), true);
        }
        File compressedFile = File.createTempFile("compressed_", ".jpg", context.getCacheDir());
        try (FileOutputStream out = new FileOutputStream(compressedFile)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
        }
        return compressedFile;
    }
}
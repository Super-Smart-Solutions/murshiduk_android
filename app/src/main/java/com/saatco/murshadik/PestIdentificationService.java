
package com.saatco.murshadik;

import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.api.APIInterface;
import com.saatco.murshadik.models.InferenceResponse;
import com.saatco.murshadik.models.UploadImageResponse;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;

public class PestIdentificationService {

    private final APIInterface apiInterface;

    public PestIdentificationService() {
        this.apiInterface = APIClient.getPestIdentificationClient().create(APIInterface.class);
    }

    public void uploadImage(String authToken, MultipartBody.Part imageFile, MultipartBody.Part name, MultipartBody.Part plantId, MultipartBody.Part farmId, MultipartBody.Part annotated, Callback<UploadImageResponse> callback) {
        Call<UploadImageResponse> call = apiInterface.uploadImage("Bearer " + authToken, imageFile, name, plantId, farmId, annotated);
        call.enqueue(callback);
    }

    public void createInference(String authToken, int imageId, Callback<InferenceResponse> callback) {
        Call<InferenceResponse> call = apiInterface.createInference("Bearer " + authToken, imageId);
        call.enqueue(callback);
    }

    public void validateInference(String authToken, int inferenceId, Callback<InferenceResponse> callback) {
        Call<InferenceResponse> call = apiInterface.validateInference("Bearer " + authToken, inferenceId);
        call.enqueue(callback);
    }

    public void detectDisease(String authToken, int inferenceId, Callback<InferenceResponse> callback) {
        Call<InferenceResponse> call = apiInterface.detectDisease("Bearer " + authToken, inferenceId);
        call.enqueue(callback);
    }

    public void getDiseaseById(String authToken, int diseaseId, Callback<com.saatco.murshadik.models.Disease> callback) {
        Call<com.saatco.murshadik.models.Disease> call = apiInterface.getDiseaseById("Bearer " + authToken, diseaseId);
        call.enqueue(callback);
    }
}

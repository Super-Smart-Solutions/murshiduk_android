package com.saatco.murshadik;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.api.APIInterface;
import com.saatco.murshadik.models.InferenceResponse;
import com.saatco.murshadik.models.Plant;
import com.saatco.murshadik.models.UploadImageResponse;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PestIdentificationActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_CODE_PERMISSIONS = 101;

    private Spinner spinnerPestType;
    private ImageView imagePicker;
    private TextView textTapToAddImage;
    private ImageButton buttonClearImage;
    private Uri selectedImageUri;
    private Button buttonSubmit;
    private int selectedPlantId = -1;

    private ProgressBar progressBar;
    private TextView loadingMessageTextView;

    private Map<String, Integer> plantIdMap;

    private PestIdentificationService pestIdentificationService;
    private String authToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pest_identification);

        pestIdentificationService = new PestIdentificationService();
        // TODO: Replace this with a secure method of obtaining the auth token
        authToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI3MzE3NDU2NS1hYWJjLTRjMTItYWQ1Yi1iOWUyNWRmYzY2MjUiLCJhdWQiOlsiZmFzdGFwaS11c2VyczphdXRoIl19.PVWELPTrzW7Y50Jw4GXTrBf7skvwJ1KkJ0iomqdXuqQ";

        spinnerPestType = findViewById(R.id.spinner_pest_type);
        imagePicker = findViewById(R.id.image_picker);
        textTapToAddImage = findViewById(R.id.text_tap_to_add_image);
        buttonClearImage = findViewById(R.id.button_clear_image);
        buttonSubmit = findViewById(R.id.button_submit);
        progressBar = findViewById(R.id.progress_bar);
        loadingMessageTextView = findViewById(R.id.text_loading_message);

        setupPlantIdMap();

        // Setup Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.pest_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPestType.setAdapter(adapter);

        spinnerPestType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedPlantName = parent.getItemAtPosition(position).toString();
                if (plantIdMap.containsKey(selectedPlantName)) {
                    selectedPlantId = plantIdMap.get(selectedPlantName);
                } else {
                    selectedPlantId = -1; // Or handle error appropriately
                }
                Toast.makeText(PestIdentificationActivity.this, "Selected: " + selectedPlantName + " (ID: " + selectedPlantId + ")", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedPlantId = -1;
            }
        });

        // Setup Image Picker
        imagePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndRequestPermissions();
            }
        });

        // Setup Clear Image Button
        buttonClearImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearImageSelection();
            }
        });

        // Setup Submit Button
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPlantId != -1 && selectedImageUri != null) {
                    uploadImage();
                } else if (selectedPlantId == -1) {
                    Toast.makeText(PestIdentificationActivity.this, "Please select a plant type.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PestIdentificationActivity.this, "Please select an image.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Hide the result view initially
        findViewById(R.id.inference_result_container).setVisibility(View.GONE);
    }

    private void showLoading(String message) {
        progressBar.setVisibility(View.VISIBLE);
        loadingMessageTextView.setText(message);
        loadingMessageTextView.setVisibility(View.VISIBLE);
        buttonSubmit.setEnabled(false);
        spinnerPestType.setEnabled(false);
        imagePicker.setEnabled(false);
        buttonClearImage.setEnabled(false);
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
        loadingMessageTextView.setVisibility(View.GONE);
        buttonSubmit.setEnabled(true);
        spinnerPestType.setEnabled(true);
        imagePicker.setEnabled(true);
        buttonClearImage.setEnabled(true);
    }

    private void setupPlantIdMap() {
        plantIdMap = new HashMap<>();
        plantIdMap.put(getString(R.string.plant_grape), 18);
        plantIdMap.put(getString(R.string.plant_citrus), 15);
        plantIdMap.put(getString(R.string.plant_honey_bees), 19);
        plantIdMap.put(getString(R.string.plant_roses), 21);
        plantIdMap.put(getString(R.string.plant_pomegranate), 20);
        plantIdMap.put(getString(R.string.plant_fish), 26);
        plantIdMap.put(getString(R.string.plant_coffee), 27);
        plantIdMap.put(getString(R.string.plant_animals), 28);
    }

    private void checkAndRequestPermissions() {
        String[] permissions;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions = new String[]{Manifest.permission.READ_MEDIA_IMAGES};
        } else {
            permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
        }

        boolean allPermissionsGranted = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                allPermissionsGranted = false;
                break;
            }
        }

        if (allPermissionsGranted) {
            openFileChooser();
        } else {
            requestPermissions(permissions, REQUEST_CODE_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            boolean allPermissionsGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }
            if (allPermissionsGranted) {
                openFileChooser();
            } else {
                Toast.makeText(this, "Permissions denied. Cannot select image.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void clearImageSelection() {
        imagePicker.setImageResource(android.R.drawable.ic_menu_camera); // Set default image
        selectedImageUri = null;
        buttonClearImage.setVisibility(View.GONE);
        textTapToAddImage.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            imagePicker.setImageURI(selectedImageUri);
            buttonClearImage.setVisibility(View.VISIBLE);
            textTapToAddImage.setVisibility(View.GONE);
        }
    }

    private void uploadImage() {
        showLoading(getString(R.string.checking_image));
        if (selectedImageUri == null) {
            Toast.makeText(this, "No image selected to upload.", Toast.LENGTH_SHORT).show();
            hideLoading();
            return;
        }

        File file = new File(getRealPathFromURI(selectedImageUri));
        String originalFileName = file.getName();
        String fileNameWithoutExt = originalFileName.substring(0, originalFileName.lastIndexOf('.'));
        String fileExt = originalFileName.substring(originalFileName.lastIndexOf('.') + 1);
        long timestamp = System.currentTimeMillis();
        String newFileName = "uploads/" + fileNameWithoutExt + "_" + timestamp + "." + fileExt;

        RequestBody requestFile = RequestBody.create(MediaType.parse(getContentResolver().getType(selectedImageUri)), file);
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image_file", newFileName, requestFile);

        MultipartBody.Part namePart = MultipartBody.Part.createFormData("name", null, RequestBody.create(MediaType.parse("text/plain"), newFileName));
        MultipartBody.Part plantIdPart = MultipartBody.Part.createFormData("plant_id", null, RequestBody.create(MediaType.parse("text/plain"), String.valueOf(selectedPlantId)));
        MultipartBody.Part farmIdPart = MultipartBody.Part.createFormData("farm_id", null, RequestBody.create(MediaType.parse("text/plain"), "1")); // Assuming farm_id is always 1
        MultipartBody.Part annotatedPart = MultipartBody.Part.createFormData("annotated", null, RequestBody.create(MediaType.parse("text/plain"), "false"));

        pestIdentificationService.uploadImage(authToken, imagePart, namePart, plantIdPart, farmIdPart, annotatedPart, new Callback<UploadImageResponse>() {
            @Override
            public void onResponse(Call<UploadImageResponse> call, Response<UploadImageResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int imageId = response.body().getId();
                    createInference(imageId);
                } else {
                    hideLoading();
                    Toast.makeText(PestIdentificationActivity.this, "Image upload failed: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UploadImageResponse> call, Throwable t) {
                hideLoading();
                Toast.makeText(PestIdentificationActivity.this, "Image upload error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createInference(int imageId) {
        showLoading(getString(R.string.checking_image));
        pestIdentificationService.createInference(authToken, imageId, new Callback<InferenceResponse>() {
            @Override
            public void onResponse(Call<InferenceResponse> call, Response<InferenceResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int inferenceId = response.body().getId();
                    validateInference(inferenceId);
                } else {
                    hideLoading();
                    Toast.makeText(PestIdentificationActivity.this, "Inference creation failed: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<InferenceResponse> call, Throwable t) {
                hideLoading();
                Toast.makeText(PestIdentificationActivity.this, "Inference creation failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void validateInference(int inferenceId) {
        showLoading(getString(R.string.checking_image));
        pestIdentificationService.validateInference(authToken, inferenceId, new Callback<InferenceResponse>() {
            @Override
            public void onResponse(Call<InferenceResponse> call, Response<InferenceResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int status = response.body().getStatus();
                    if (status == 1) { // IMAGE_VALID
                        detectDisease(inferenceId);
                    } else {
                        hideLoading();
                        Toast.makeText(PestIdentificationActivity.this, getString(R.string.image_not_valid), Toast.LENGTH_LONG).show();
                    }
                } else {
                    hideLoading();
                    Toast.makeText(PestIdentificationActivity.this, "Inference validation failed: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<InferenceResponse> call, Throwable t) {
                hideLoading();
                Toast.makeText(PestIdentificationActivity.this, "Inference validation failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void detectDisease(int inferenceId) {
        showLoading(getString(R.string.detecting_disease));
        pestIdentificationService.detectDisease(authToken, inferenceId, new Callback<InferenceResponse>() {
            @Override
            public void onResponse(Call<InferenceResponse> call, Response<InferenceResponse> response) {
                hideLoading();
                if (response.isSuccessful() && response.body() != null) {
                    int status = response.body().getStatus();
                    if (status == 2) { // DETECTION_COMPLETED
                        int diseaseId = response.body().getDiseaseId();
                        getDiseaseDetails(diseaseId);
                    } else if (status == -2) { // DETECTION_INCONCLUSIVE
                        showInconclusiveResult();
                    } else {
                        Toast.makeText(PestIdentificationActivity.this, getString(R.string.detection_failed), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(PestIdentificationActivity.this, "Disease detection failed: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<InferenceResponse> call, Throwable t) {
                hideLoading();
                Toast.makeText(PestIdentificationActivity.this, "Disease detection failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getDiseaseDetails(int diseaseId) {
        showLoading(getString(R.string.getting_disease_details));
        pestIdentificationService.getDiseaseById(authToken, diseaseId, new Callback<com.saatco.murshadik.models.Disease>() {
            @Override
            public void onResponse(Call<com.saatco.murshadik.models.Disease> call, Response<com.saatco.murshadik.models.Disease> response) {
                hideLoading();
                if (response.isSuccessful() && response.body() != null) {
                    com.saatco.murshadik.models.Disease disease = response.body();
                    displaySuccessfulResult(disease);
                } else {
                    Toast.makeText(PestIdentificationActivity.this, "Failed to get disease details: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<com.saatco.murshadik.models.Disease> call, Throwable t) {
                hideLoading();
                Toast.makeText(PestIdentificationActivity.this, "Failed to get disease details: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displaySuccessfulResult(com.saatco.murshadik.models.Disease disease) {
        View resultView = findViewById(R.id.inference_result_container);
        resultView.setVisibility(View.VISIBLE);

        ImageView resultImage = findViewById(R.id.result_image);
        // TODO: Load the actual image of the disease
        resultImage.setImageURI(selectedImageUri);

        TextView resultTitle = findViewById(R.id.result_title);
        TextView resultScientificName = findViewById(R.id.result_scientific_name);
        TextView resultSymptoms = findViewById(R.id.result_symptoms);
        TextView resultTreatment = findViewById(R.id.result_treatment);

        // Check the current language and display the appropriate name
        String language = java.util.Locale.getDefault().getLanguage();
        if (language.equals("ar")) {
            resultTitle.setText(disease.getArabicName());
        } else {
            resultTitle.setText(disease.getEnglishName());
        }

        resultScientificName.setText(disease.getScientificName());
        resultSymptoms.setText(disease.getSymptoms());
        resultTreatment.setText(disease.getTreatments());
    }

    private void showInconclusiveResult() {
        View resultView = findViewById(R.id.inference_result_container);
        resultView.setVisibility(View.VISIBLE);

        ImageView resultImage = findViewById(R.id.result_image);
        resultImage.setImageURI(selectedImageUri);

        TextView resultTitle = findViewById(R.id.result_title);
        resultTitle.setText(R.string.detection_inconclusive);

        TextView resultScientificName = findViewById(R.id.result_scientific_name);
        resultScientificName.setText("");

        TextView resultSymptoms = findViewById(R.id.result_symptoms);
        resultSymptoms.setText(R.string.detection_inconclusive_message);

        TextView resultTreatment = findViewById(R.id.result_treatment);
        resultTreatment.setText("");
    }

    private String getRealPathFromURI(Uri contentUri) {
        String result;
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            result = contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }
}
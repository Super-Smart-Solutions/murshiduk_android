package com.saatco.murshadik;
import androidx.appcompat.app.AppCompatActivity;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;


import android.os.Environment;
import androidx.core.content.FileProvider;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.os.Environment;
import androidx.core.content.FileProvider;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;
import androidx.core.content.FileProvider;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.AlertDialog;
import android.graphics.Bitmap;
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
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.api.APIInterface;
import com.saatco.murshadik.models.InferenceResponse;
import com.saatco.murshadik.models.Plant;
import com.saatco.murshadik.models.UploadImageResponse;

import java.io.ByteArrayOutputStream;
import android.content.Context;
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

import android.graphics.Bitmap;
import android.provider.MediaStore;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class PestIdentificationActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private String currentPhotoPath;

    private static final int REQUEST_CODE_PERMISSIONS = 101;

    private Spinner spinnerPestType;
    private ImageView imagePicker;
    private ImageButton buttonClearImage;
    private Uri selectedImageUri;
    private Button buttonSubmit;
    private Button buttonResetImage;
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

        // Set up the custom toolbar
        TextView toolbarTitle = findViewById(R.id.appBar).findViewById(R.id.toolbar_title);
        toolbarTitle.setText(R.string.ai_disease_detection);

        ImageView backButton = findViewById(R.id.appBar).findViewById(R.id.btn_back);
        backButton.setOnClickListener(v -> onBackPressed());

        pestIdentificationService = new PestIdentificationService();
        // TODO: Replace this with a secure method of obtaining the auth token
        authToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI3MzE3NDU2NS1hYWJjLTRjMTItYWQ1Yi1iOWUyNWRmYzY2MjUiLCJhdWQiOlsiZmFzdGFwaS11c2VyczphdXRoIl19.PVWELPTrzW7Y50Jw4GXTrBf7skvwJ1KkJ0iomqdXuqQ";

        spinnerPestType = findViewById(R.id.spinner_pest_type);
        TextView selectCategoryText = findViewById(R.id.text_select_category);
        selectCategoryText.setText(R.string.select_category);
        imagePicker = findViewById(R.id.image_picker);
        buttonClearImage = findViewById(R.id.button_clear_image);
        buttonSubmit = findViewById(R.id.button_submit);
        buttonResetImage = findViewById(R.id.button_reset_image); // Initialize here
        progressBar = findViewById(R.id.progress_bar);
        loadingMessageTextView = findViewById(R.id.text_loading_message);

        setupPlantIdMap();

        // Setup Spinner
        List<String> pestTypes = new ArrayList<>(plantIdMap.keySet());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, pestTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPestType.setAdapter(adapter);

        spinnerPestType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedPlantName = parent.getItemAtPosition(position).toString();
                if (plantIdMap.containsKey(selectedPlantName)) {
                    selectedPlantId = plantIdMap.get(selectedPlantName);
                } else {
                    selectedPlantId = -1; // Should not happen with correct data
                }
//                if (selectedPlantId != -1) {
//                    Toast.makeText(PestIdentificationActivity.this, "Selected: " + selectedPlantName + " (ID: " + selectedPlantId + ")", Toast.LENGTH_SHORT).show();
//                }
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
                selectImage();
            }
        });

        // Setup Clear Image Button
        buttonClearImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearImageSelection();
            }
        });

        // Setup Reset Image Button
        buttonResetImage.setOnClickListener(new View.OnClickListener() {
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



    private void selectImage() {
        final CharSequence[] options = {getString(R.string.take_photo), getString(R.string.choose_from_gallery), getString(R.string.cancel)};
        AlertDialog.Builder builder = new AlertDialog.Builder(PestIdentificationActivity.this);
        builder.setTitle(getString(R.string.select_image_source));
        builder.setItems(options, (dialog, item) -> {
            if (options[item].equals(getString(R.string.take_photo))) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        Toast.makeText(PestIdentificationActivity.this, "Could not create image file", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(PestIdentificationActivity.this, "com.saatco.murshadik.fileprovider", photoFile);

                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }
                }

            } else if (options[item].equals(getString(R.string.choose_from_gallery))) {
                Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhotoIntent, PICK_IMAGE_REQUEST);

            } else if (options[item].equals(getString(R.string.cancel))) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

//    private void clearImageSelection() {
//        imagePicker.setImageResource(android.R.drawable.ic_menu_camera); // Set default image
//        selectedImageUri = null;
//        buttonClearImage.setVisibility(View.GONE);
//        buttonResetImage.setVisibility(View.GONE);
//        findViewById(R.id.inference_result_container).setVisibility(View.GONE);
//        buttonResetImage.setText(R.string.reset_image);
//    }
    private void clearImageSelection() {
        imagePicker.setImageResource(android.R.drawable.ic_menu_camera);
        selectedImageUri = null;

        // Hide clear/reset buttons
        buttonClearImage.setVisibility(View.GONE);
        buttonResetImage.setVisibility(View.GONE);
        buttonResetImage.setText(R.string.reset_image);

        // Hide result container
        View resultView = findViewById(R.id.inference_result_container);
        resultView.setVisibility(View.GONE);

        // Reset all result fields (to avoid showing stale data)
        findViewById(R.id.disease_name_container).setVisibility(View.GONE);
        findViewById(R.id.disease_details_container).setVisibility(View.GONE);

        ((TextView) findViewById(R.id.result_title)).setText("");
        ((TextView) findViewById(R.id.disease_name)).setText("");
        ((TextView) findViewById(R.id.confidence_score)).setText("");

        ((TextView) findViewById(R.id.result_arabic_name)).setText("");
        ((TextView) findViewById(R.id.result_scientific_name)).setText("");
        ((TextView) findViewById(R.id.result_symptoms)).setText("");
        ((TextView) findViewById(R.id.result_treatment)).setText("");

        // Hide all headers and fields
        findViewById(R.id.english_name_header).setVisibility(View.GONE);
        findViewById(R.id.arabic_name_header).setVisibility(View.GONE);
        findViewById(R.id.result_arabic_name).setVisibility(View.GONE);
        findViewById(R.id.result_scientific_name).setVisibility(View.GONE);
        findViewById(R.id.result_symptoms_title).setVisibility(View.GONE);
        findViewById(R.id.result_symptoms).setVisibility(View.GONE);
        findViewById(R.id.result_treatment_title).setVisibility(View.GONE);
        findViewById(R.id.result_treatment).setVisibility(View.GONE);
        findViewById(R.id.button_show_details).setVisibility(View.GONE);
    }


    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                storageDir      // directory
        );

        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
        selectedImageUri = data.getData();
//        Glide.with(this).load(selectedImageUri).into(imagePicker);
        Glide.with(this)
                .load(selectedImageUri)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(imagePicker);

        buttonClearImage.setVisibility(View.VISIBLE);
        buttonResetImage.setVisibility(View.VISIBLE);
    } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
        File file = new File(currentPhotoPath);
        selectedImageUri = Uri.fromFile(file);
//        Glide.with(this).load(selectedImageUri).into(imagePicker);
        Glide.with(this)
                .load(selectedImageUri)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(imagePicker);

        buttonClearImage.setVisibility(View.VISIBLE);
        buttonResetImage.setVisibility(View.VISIBLE);
    }

}
private File compressImage(Uri imageUri) throws IOException {
    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

    // Resize if needed (optional: scale to max width/height)
    int maxDim = 1024;
    int width = bitmap.getWidth();
    int height = bitmap.getHeight();
    if (width > maxDim || height > maxDim) {
        float scale = Math.min((float) maxDim / width, (float) maxDim / height);
        bitmap = Bitmap.createScaledBitmap(bitmap, Math.round(scale * width), Math.round(scale * height), true);
    }

    // Compress the image to JPEG with quality 80
    File compressedFile = File.createTempFile("compressed_", ".jpg", getCacheDir());
    FileOutputStream out = new FileOutputStream(compressedFile);
    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
    out.flush();
    out.close();

    return compressedFile;
}


private void uploadImage() {
    showLoading(getString(R.string.checking_image));
    if (selectedImageUri == null) {
        Toast.makeText(this, "No image selected to upload.", Toast.LENGTH_SHORT).show();
        hideLoading();
        return;
    }

//        File file = new File(getRealPathFromURI(selectedImageUri));
    File file;
    try {
        file = compressImage(selectedImageUri);
    } catch (IOException e) {
        Toast.makeText(this, "Image compression failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        hideLoading();
        return;
    }

    String originalFileName = file.getName();
    String fileNameWithoutExt = originalFileName.substring(0, originalFileName.lastIndexOf('.'));
    String fileExt = originalFileName.substring(originalFileName.lastIndexOf('.') + 1);
    long timestamp = System.currentTimeMillis();
    String newFileName = "uploads/" + fileNameWithoutExt + "_" + timestamp + "." + fileExt;

    String mimeType = getContentResolver().getType(selectedImageUri);
    if (mimeType == null || mimeType.isEmpty()) {
        mimeType = "image/jpeg"; // Fallback for camera images
    }

    MediaType mediaType = MediaType.parse(mimeType);
    if (mediaType == null) {
        Toast.makeText(this, "Unsupported image MIME type", Toast.LENGTH_SHORT).show();
        hideLoading();
        return;
    }

    RequestBody requestFile = RequestBody.create(mediaType, file);



//    RequestBody requestFile = RequestBody.create(MediaType.parse(getContentResolver().getType(selectedImageUri)), file);
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
                        double confidence = response.body().getConfidenceLevel();
                        getDiseaseDetails(diseaseId, confidence);
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

    private void getDiseaseDetails(int diseaseId, double confidence) {
        showLoading(getString(R.string.getting_disease_details));
        pestIdentificationService.getDiseaseById(authToken, diseaseId, new Callback<com.saatco.murshadik.models.Disease>() {
            @Override
            public void onResponse(Call<com.saatco.murshadik.models.Disease> call, Response<com.saatco.murshadik.models.Disease> response) {
                hideLoading();
                if (response.isSuccessful() && response.body() != null) {
                    com.saatco.murshadik.models.Disease disease = response.body();
                    displaySuccessfulResult(disease, confidence);
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

    private void displaySuccessfulResult(com.saatco.murshadik.models.Disease disease, double confidence) {
        View resultView = findViewById(R.id.inference_result_container);
        resultView.setVisibility(View.VISIBLE);
        resultView.requestLayout();

        LinearLayout diseaseNameContainer = findViewById(R.id.disease_name_container);
        diseaseNameContainer.setVisibility(View.VISIBLE);

        TextView diseaseName = findViewById(R.id.disease_name);
        diseaseName.setText(disease.getArabicName());

        TextView confidenceScore = findViewById(R.id.confidence_score);
        confidenceScore.setText(String.format("%.2f%%", confidence * 100));

        Button showDetailsButton = findViewById(R.id.button_show_details);
        LinearLayout diseaseDetailsContainer = findViewById(R.id.disease_details_container);

        showDetailsButton.setOnClickListener(v -> {
            diseaseDetailsContainer.setVisibility(View.VISIBLE);
            showDetailsButton.setVisibility(View.GONE);
        });

        TextView englishNameHeader = findViewById(R.id.english_name_header);
        TextView resultTitle = findViewById(R.id.result_title);
        TextView arabicNameHeader = findViewById(R.id.arabic_name_header);
        TextView resultArabicName = findViewById(R.id.result_arabic_name);
        TextView resultScientificName = findViewById(R.id.result_scientific_name);
        TextView resultSymptomsTitle = findViewById(R.id.result_symptoms_title);
        TextView resultSymptoms = findViewById(R.id.result_symptoms);
        TextView resultTreatmentTitle = findViewById(R.id.result_treatment_title);
        TextView resultTreatment = findViewById(R.id.result_treatment);

        englishNameHeader.setVisibility(View.VISIBLE);
        resultTitle.setText(disease.getEnglishName());
        resultTitle.setVisibility(View.VISIBLE);

        arabicNameHeader.setVisibility(View.VISIBLE);
        resultArabicName.setText(disease.getArabicName());
        resultArabicName.setVisibility(View.VISIBLE);

        resultScientificName.setText(disease.getScientificName());
        resultScientificName.setVisibility(View.VISIBLE);

        resultSymptomsTitle.setVisibility(View.VISIBLE);
        resultSymptoms.setText(disease.getSymptoms());
        resultSymptoms.setVisibility(View.VISIBLE);

        resultTreatmentTitle.setVisibility(View.VISIBLE);
        resultTreatment.setText(disease.getTreatments());
        resultTreatment.setVisibility(View.VISIBLE);

        buttonResetImage.setText(R.string.try_different_image);
    }

    private void showInconclusiveResult() {
        // Make the root container visible
        findViewById(R.id.inference_result_container).setVisibility(View.VISIBLE);

        // Show the details container (it contains result_symptoms)
        findViewById(R.id.disease_details_container).setVisibility(View.VISIBLE);

        // Show and set the result title
        TextView resultTitle = findViewById(R.id.result_title);
        resultTitle.setText(R.string.detection_inconclusive);
        resultTitle.setVisibility(View.VISIBLE);

        // Hide disease name section
        findViewById(R.id.disease_name_container).setVisibility(View.GONE);

        // Hide other details not needed for inconclusive case
        findViewById(R.id.confidence_score).setVisibility(View.GONE);
        findViewById(R.id.english_name_header).setVisibility(View.GONE);
        findViewById(R.id.arabic_name_header).setVisibility(View.GONE);
        findViewById(R.id.result_arabic_name).setVisibility(View.GONE);
        findViewById(R.id.result_scientific_name).setVisibility(View.GONE);
        findViewById(R.id.result_treatment_title).setVisibility(View.GONE);
        findViewById(R.id.result_treatment).setVisibility(View.GONE);
        findViewById(R.id.button_show_details).setVisibility(View.GONE);

        // Show the symptoms TextView as a general message area
        TextView resultSymptoms = findViewById(R.id.result_symptoms);
        resultSymptoms.setText(R.string.detection_inconclusive_message);
        resultSymptoms.setVisibility(View.VISIBLE);

        // Show the reset button with appropriate text
        buttonResetImage.setText(R.string.try_different_image);
        buttonResetImage.setVisibility(View.VISIBLE);
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
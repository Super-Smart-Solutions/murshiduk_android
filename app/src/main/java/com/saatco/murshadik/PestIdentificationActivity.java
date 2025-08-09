package com.saatco.murshadik;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.saatco.murshadik.models.Disease;
import com.saatco.murshadik.ui.PestIdentificationState;
import com.saatco.murshadik.viewmodels.PestIdentificationViewModel;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.widget.ViewFlipper;

public class PestIdentificationActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int REQUEST_CODE_PERMISSIONS = 101;

    private String currentPhotoPath;
    private Uri selectedImageUri;
    private int selectedPlantId = -1;

    private PestIdentificationViewModel viewModel;

    // UI Components
    private Spinner spinnerPestType;
    private ImageView imagePicker;
    private ImageButton buttonClearImage;
    private Button buttonSubmit;
    private Button buttonResetImage;
    private ProgressBar progressBar;
    private TextView loadingMessageTextView;
    private Map<String, Integer> plantIdMap;

    private ViewFlipper viewFlipper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pest_identification);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(PestIdentificationViewModel.class);

        // Setup Views
        setupViews();
        setupPlantIdMap();
        setupSpinner();

        // Observe UI state changes from the ViewModel
        observeViewModel();
    }

    private void setupViews() {
        viewFlipper = findViewById(R.id.view_flipper);

        TextView toolbarTitle = findViewById(R.id.appBar).findViewById(R.id.toolbar_title);
        toolbarTitle.setText(R.string.ai_disease_detection);

        ImageView backButton = findViewById(R.id.appBar).findViewById(R.id.btn_back);
        backButton.setOnClickListener(v -> onBackPressed());

        spinnerPestType = findViewById(R.id.spinner_pest_type);
        TextView selectCategoryText = findViewById(R.id.text_select_category);
        selectCategoryText.setText(R.string.select_category);
        imagePicker = findViewById(R.id.image_picker);
        buttonClearImage = findViewById(R.id.button_clear_image);
        buttonSubmit = findViewById(R.id.button_submit);
        buttonResetImage = findViewById(R.id.button_reset_image);
        progressBar = findViewById(R.id.progress_bar);
        loadingMessageTextView = findViewById(R.id.text_loading_message);

        imagePicker.setOnClickListener(v -> selectImage());
        buttonClearImage.setOnClickListener(v -> clearImageSelection());
        buttonResetImage.setOnClickListener(v -> clearImageSelection());

        buttonSubmit.setOnClickListener(v -> {
            if (selectedPlantId != -1 && selectedImageUri != null) {
                viewModel.startDiseaseDetection(selectedImageUri, selectedPlantId, getApplicationContext());
            } else if (selectedPlantId == -1) {
                Toast.makeText(PestIdentificationActivity.this, "Please select a plant type.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(PestIdentificationActivity.this, "Please select an image.", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.inference_result_container).setVisibility(View.GONE);
    }

    private void observeViewModel() {
        viewModel.getUiState().observe(this, state -> {
            if (state instanceof PestIdentificationState.Input) {
                viewFlipper.setDisplayedChild(0); // Show Input View
            } else if (state instanceof PestIdentificationState.Loading) {
                loadingMessageTextView.setText(((PestIdentificationState.Loading) state).message);
                viewFlipper.setDisplayedChild(1); // Show Loading View
            } else if (state instanceof PestIdentificationState.Success) {
                PestIdentificationState.Success successState = (PestIdentificationState.Success) state;
                displaySuccessfulResult(successState.disease, successState.confidence);
                viewFlipper.setDisplayedChild(2); // Show Result View
            } else if (state instanceof PestIdentificationState.Healthy) { // Add this block
                showHealthyResult();
                viewFlipper.setDisplayedChild(2); // Show Result View
            }
            else if (state instanceof PestIdentificationState.Inconclusive) {
                showInconclusiveResult();
                viewFlipper.setDisplayedChild(2); // Show Result View
            } else if (state instanceof PestIdentificationState.Error) {
                Toast.makeText(this, ((PestIdentificationState.Error) state).message, Toast.LENGTH_LONG).show();
                viewFlipper.setDisplayedChild(0); // Go back to Input View on error
            }
        });
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

    private void setupSpinner() {
        List<String> pestTypes = new ArrayList<>(plantIdMap.keySet());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, pestTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPestType.setAdapter(adapter);
        spinnerPestType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedPlantName = parent.getItemAtPosition(position).toString();
                selectedPlantId = plantIdMap.getOrDefault(selectedPlantName, -1);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedPlantId = -1;
            }
        });
    }

    private void selectImage() {
        final CharSequence[] options = {getString(R.string.take_photo), getString(R.string.choose_from_gallery), getString(R.string.cancel)};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.select_image_source));
        builder.setItems(options, (dialog, item) -> {
            if (options[item].equals(getString(R.string.take_photo))) {
                dispatchTakePictureIntent();
            } else if (options[item].equals(getString(R.string.choose_from_gallery))) {
                Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhotoIntent, PICK_IMAGE_REQUEST);
            } else {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(this, "Could not create image file.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.saatco.murshadik.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }


    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
                selectedImageUri = data.getData();
            } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                selectedImageUri = Uri.fromFile(new File(currentPhotoPath));
            }

            if (selectedImageUri != null) {
                Glide.with(this)
                        .load(selectedImageUri)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(imagePicker);
                buttonClearImage.setVisibility(View.VISIBLE);
                buttonResetImage.setVisibility(View.VISIBLE);
            }
        }
    }

    private void clearImageSelection() {
        imagePicker.setImageResource(android.R.drawable.ic_menu_camera);
        selectedImageUri = null;
        buttonClearImage.setVisibility(View.GONE);
        spinnerPestType.setSelection(0);
        viewModel.resetState();
    }


    private void displaySuccessfulResult(Disease disease, double confidence) {
        View resultView = findViewById(R.id.inference_result_container);
        resultView.setVisibility(View.VISIBLE);
        findViewById(R.id.disease_name_container).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.disease_name)).setText(disease.getArabicName());
        ((TextView) findViewById(R.id.confidence_score)).setText(String.format(java.util.Locale.US, "%.2f%%", confidence * 100));
        Button showDetailsButton = findViewById(R.id.button_show_details);
        LinearLayout diseaseDetailsContainer = findViewById(R.id.disease_details_container);
        showDetailsButton.setOnClickListener(v -> {
            diseaseDetailsContainer.setVisibility(View.VISIBLE);
            showDetailsButton.setVisibility(View.GONE);
        });
        ((TextView) findViewById(R.id.result_title)).setText(disease.getEnglishName());
        ((TextView) findViewById(R.id.result_arabic_name)).setText(disease.getArabicName());
        ((TextView) findViewById(R.id.result_scientific_name)).setText(disease.getScientificName());
        ((TextView) findViewById(R.id.result_symptoms)).setText(disease.getSymptoms());
        ((TextView) findViewById(R.id.result_treatment)).setText(disease.getTreatments());
        findViewById(R.id.english_name_header).setVisibility(View.VISIBLE);
        findViewById(R.id.result_title).setVisibility(View.VISIBLE);
        findViewById(R.id.arabic_name_header).setVisibility(View.VISIBLE);
        findViewById(R.id.result_arabic_name).setVisibility(View.VISIBLE);
        findViewById(R.id.result_scientific_name).setVisibility(View.VISIBLE);
        findViewById(R.id.result_symptoms_title).setVisibility(View.VISIBLE);
        findViewById(R.id.result_symptoms).setVisibility(View.VISIBLE);
        findViewById(R.id.result_treatment_title).setVisibility(View.VISIBLE);
        findViewById(R.id.result_treatment).setVisibility(View.VISIBLE);
        buttonResetImage.setText(R.string.try_different_image);
    }

    private void showInconclusiveResult() {
        findViewById(R.id.inference_result_container).setVisibility(View.VISIBLE);
        findViewById(R.id.disease_details_container).setVisibility(View.VISIBLE);
        TextView resultTitle = findViewById(R.id.result_title);
        resultTitle.setText(R.string.detection_inconclusive);
        resultTitle.setVisibility(View.VISIBLE);
        findViewById(R.id.disease_name_container).setVisibility(View.GONE);
        findViewById(R.id.confidence_score).setVisibility(View.GONE);
        findViewById(R.id.english_name_header).setVisibility(View.GONE);
        findViewById(R.id.arabic_name_header).setVisibility(View.GONE);
        findViewById(R.id.result_arabic_name).setVisibility(View.GONE);
        findViewById(R.id.result_scientific_name).setVisibility(View.GONE);
        findViewById(R.id.result_treatment_title).setVisibility(View.GONE);
        findViewById(R.id.result_treatment).setVisibility(View.GONE);
        findViewById(R.id.button_show_details).setVisibility(View.GONE);
        TextView resultSymptoms = findViewById(R.id.result_symptoms);
        resultSymptoms.setText(R.string.detection_inconclusive_message);
        resultSymptoms.setVisibility(View.VISIBLE);
        buttonResetImage.setText(R.string.try_different_image);
        buttonResetImage.setVisibility(View.VISIBLE);
    }

    private void showHealthyResult() {
        findViewById(R.id.inference_result_container).setVisibility(View.VISIBLE);
        findViewById(R.id.disease_name_container).setVisibility(View.GONE);
        findViewById(R.id.disease_details_container).setVisibility(View.VISIBLE);

        TextView resultTitle = findViewById(R.id.result_title);
        resultTitle.setText(R.string.detection_healthy);
        resultTitle.setVisibility(View.VISIBLE);

        findViewById(R.id.confidence_score).setVisibility(View.GONE);
        findViewById(R.id.english_name_header).setVisibility(View.GONE);
        findViewById(R.id.arabic_name_header).setVisibility(View.GONE);
        findViewById(R.id.result_arabic_name).setVisibility(View.GONE);
        findViewById(R.id.result_scientific_name).setVisibility(View.GONE);
        findViewById(R.id.result_symptoms_title).setVisibility(View.GONE);
        findViewById(R.id.result_symptoms).setVisibility(View.GONE);
        findViewById(R.id.result_treatment_title).setVisibility(View.GONE);
        findViewById(R.id.result_treatment).setVisibility(View.GONE);
        findViewById(R.id.button_show_details).setVisibility(View.GONE);

        buttonResetImage.setText(R.string.try_different_image);
        buttonResetImage.setVisibility(View.VISIBLE);
    }
    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
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
}
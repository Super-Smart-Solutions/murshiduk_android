package com.saatco.murshadik;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.saatco.murshadik.models.Disease;
import com.saatco.murshadik.models.Plant;
import com.saatco.murshadik.ui.DiseaseDatabaseState;
import com.saatco.murshadik.viewmodels.DiseaseDatabaseViewModel;

import java.util.ArrayList;
import java.util.List;

public class DiseaseDatabaseActivity extends AppCompatActivity {

    private DiseaseDatabaseViewModel viewModel;
    private Spinner spinnerPlants;
    private Spinner spinnerDiseases;
    private ProgressBar progressBar;
    private ScrollView detailsScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disease_database);

        viewModel = new ViewModelProvider(this).get(DiseaseDatabaseViewModel.class);

        setupViews();
        setupObservers();
    }

    private void setupViews() {
        // Setup Toolbar
        TextView toolbarTitle = findViewById(R.id.appBar).findViewById(R.id.toolbar_title);
        toolbarTitle.setText(R.string.disease_database);
        findViewById(R.id.appBar).findViewById(R.id.btn_back).setOnClickListener(v -> onBackPressed());

        spinnerPlants = findViewById(R.id.spinner_plants);
        spinnerDiseases = findViewById(R.id.spinner_diseases);
        progressBar = findViewById(R.id.progress_bar);
        detailsScrollView = findViewById(R.id.details_scroll_view);
    }

    private void setupObservers() {
        viewModel.getUiState().observe(this, state -> {
            if (state instanceof DiseaseDatabaseState.Loading) {
                progressBar.setVisibility(View.VISIBLE);
            } else {
                progressBar.setVisibility(View.GONE);
            }

            if (state instanceof DiseaseDatabaseState.Error) {
                Toast.makeText(this, ((DiseaseDatabaseState.Error) state).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getPlants().observe(this, this::setupPlantSpinner);

        viewModel.getDiseases().observe(this, this::setupDiseaseSpinner);
    }

    private void setupPlantSpinner(List<Plant> plants) {
        List<String> plantNames = new ArrayList<>();
        for (Plant plant : plants) {
            plantNames.add(plant.getArabicName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, plantNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPlants.setAdapter(adapter);

        spinnerPlants.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                detailsScrollView.setVisibility(View.GONE); // Hide details on new plant selection
                int selectedPlantId = plants.get(position).getId();
                if (selectedPlantId != -1) {
                    spinnerDiseases.setEnabled(true);
                    viewModel.onPlantSelected(selectedPlantId);
                } else {
                    spinnerDiseases.setEnabled(false);
                    spinnerDiseases.setAdapter(null); // Clear disease spinner
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupDiseaseSpinner(List<Disease> diseases) {
        if (diseases == null || diseases.isEmpty()) {
            spinnerDiseases.setAdapter(null);
            detailsScrollView.setVisibility(View.GONE);
            return;
        }

        List<String> diseaseNames = new ArrayList<>();
        for (Disease disease : diseases) {
            diseaseNames.add(disease.getArabicName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, diseaseNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDiseases.setAdapter(adapter);

        spinnerDiseases.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Disease selectedDisease = diseases.get(position);
                if (selectedDisease.getId() != -1) {
                    displayDiseaseDetails(selectedDisease);
                } else {
                    detailsScrollView.setVisibility(View.GONE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void displayDiseaseDetails(Disease disease) {
        detailsScrollView.setVisibility(View.VISIBLE);

        findViewById(R.id.disease_name_container).setVisibility(View.GONE);
        findViewById(R.id.button_show_details).setVisibility(View.GONE);

        ((TextView) findViewById(R.id.result_title)).setText(disease.getEnglishName());
        ((TextView) findViewById(R.id.result_arabic_name)).setText(disease.getArabicName());
        ((TextView) findViewById(R.id.result_scientific_name)).setText(disease.getScientificName());
        ((TextView) findViewById(R.id.result_symptoms)).setText(disease.getSymptoms());
        ((TextView) findViewById(R.id.result_treatment)).setText(disease.getTreatments());
    }
}
package com.saatco.murshadik;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.saatco.murshadik.Helpers.TokenHelper;
import com.saatco.murshadik.Helpers.ToolbarHelper;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.api.APIInterface;
import com.saatco.murshadik.databinding.ActivityLabDetailBinding;
import com.saatco.murshadik.model.LabReport;
import com.saatco.murshadik.views.EditWithBorderTitle;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LabDetailActivity extends AppCompatActivity {

    ProgressBar progressBar;
    LinearLayout layoutLab;
    int appointmentId;


    ArrayList<LabReport> labReports = new ArrayList<>();



    private static final DecimalFormat df = new DecimalFormat("0.0");

    ActivityLabDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLabDetailBinding.inflate(getLayoutInflater(), null, false);
        setContentView(binding.getRoot());

         progressBar = binding.progressBar;
         layoutLab = binding.layoutLab;

        ToolbarHelper.setToolBar(this,"", findViewById(R.id.toolbarTrans));

        appointmentId = getIntent().getIntExtra("APPOINTMENT_ID",0);



        getLabReport();

    }

    private void getLabReport(){

        progressBar.setVisibility(View.VISIBLE);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<List<LabReport>> call = apiInterface.getLabReport("Bearer " + TokenHelper.getToken(),appointmentId);
        call.enqueue(new Callback<List<LabReport>>() {
            @Override
            public void onResponse(@NonNull Call<List<LabReport>> call, @NonNull Response<List<LabReport>> response) {

                progressBar.setVisibility(View.GONE);

                try {


                    labReports = (ArrayList<LabReport>) response.body();

                    if(labReports != null)
                    {

                        for (int i= 0;i < labReports.size();i++){

                            final View itemLab = getLayoutInflater().inflate(R.layout.layout_lab_report, null);
                            layoutLab.addView(itemLab);

                            final EditWithBorderTitle tvSampleNumber = itemLab.findViewById(R.id.tvSampleNumbr);
                            final EditWithBorderTitle tvElecConductivity = itemLab.findViewById(R.id.tvElecConductivity);
                            final EditWithBorderTitle tvPH = itemLab.findViewById(R.id.tvPH);
                            final EditWithBorderTitle tvCarbonate = itemLab.findViewById(R.id.tvCarbonate);
                            final EditWithBorderTitle tvPotassium = itemLab.findViewById(R.id.tvPotassium);
                            final EditWithBorderTitle tvPhosphorous = itemLab.findViewById(R.id.tvPhosphorous);
                            final EditWithBorderTitle tvSand = itemLab.findViewById(R.id.tvSand);
                            final EditWithBorderTitle tvCelt = itemLab.findViewById(R.id.tvCelt);
                            final EditWithBorderTitle tvClay = itemLab.findViewById(R.id.tvClay);
                            final EditWithBorderTitle tvTextureType = itemLab.findViewById(R.id.tvTextureType);
                            final EditWithBorderTitle tvNotes = itemLab.findViewById(R.id.tvNotes);
                            final EditWithBorderTitle tvRecommendations = itemLab.findViewById(R.id.tvRecommendations);
                            final EditWithBorderTitle tvWaterPh = itemLab.findViewById(R.id.tvWaterPh);
                            final EditWithBorderTitle tvElecWater = itemLab.findViewById(R.id.tvElecWater);
                            final EditWithBorderTitle tvWaterCarbonate = itemLab.findViewById(R.id.tvWaterCarbonate);
                            final EditWithBorderTitle tvWaterChloride = itemLab.findViewById(R.id.tvWaterChloride);
                            final EditWithBorderTitle tvWaterSalt = itemLab.findViewById(R.id.tvWaterSalt);
                            final EditWithBorderTitle tvWaterNotes = itemLab.findViewById(R.id.tvWaterNotes);
                            final EditWithBorderTitle tvWaterRecommend = itemLab.findViewById(R.id.tvWaterRecommend);
                            final FrameLayout btnPdf = itemLab.findViewById(R.id.btnPdf);

                            LabReport labReport = labReports.get(i);
                            tvSampleNumber.setText("رقم العينة:"+labReport.sample_no);
                            tvElecConductivity.setText(df.format(labReport.soil_electric_conductivity));
                            tvPH.setText(df.format(labReport.soil_ph));
                            tvCarbonate.setText(df.format(labReport.soil_calcium_carbonate));
                            tvPotassium.setText(df.format(labReport.soil_potasium_ppm));
                            tvPhosphorous.setText(df.format(labReport.soil_phosphorus_ppm));
                            tvSand.setText(df.format(labReport.soil_sand));
                            tvCelt.setText(df.format(labReport.soil_salt));
                            tvClay.setText(df.format(labReport.soil_clay));
                            tvTextureType.setText(""+labReport.soil_texture_type);
                            tvNotes.setText(""+labReport.soil_notes);
                            tvRecommendations.setText(""+labReport.soil_recommendations);
                            tvWaterPh.setText(df.format(labReport.water_ph));
                            tvElecWater.setText(df.format(labReport.water_electric_conductivity));
                            tvWaterCarbonate.setText(df.format(labReport.water_carbonate));
                            tvWaterChloride.setText(df.format(labReport.water_chloride));
                            tvWaterSalt.setText(df.format(labReport.water_salts));
                            tvWaterNotes.setText(""+labReport.water_notes);
                            tvWaterRecommend.setText(labReport.water_recommendation);

                            btnPdf.setOnClickListener(view -> {
                                String pdf_url = APIClient.baseUrl + "Media/PDF/Lab_Reports/" +labReport.extra1;

                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(pdf_url));
                                startActivity(browserIntent);
//

                            });
                        }


                    }

                }catch (Exception ignored){}

            }

            @Override
            public void onFailure(@NonNull Call<List<LabReport>> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);

            }
        });

    }

    @Override
    protected void attachBaseContext (Context base){
        super.attachBaseContext(LocaleHelper.onAttach(base));

        Configuration config = new Configuration(base.getResources().getConfiguration());
        config.fontScale = 1.2f;
        applyOverrideConfiguration(config);
    }
}
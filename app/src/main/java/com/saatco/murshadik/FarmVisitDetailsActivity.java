package com.saatco.murshadik;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.carousel.CarouselLayoutManager;
import com.google.android.material.carousel.CarouselSnapHelper;
import com.google.android.material.carousel.FullScreenCarouselStrategy;
import com.saatco.murshadik.Helpers.ToolbarHelper;
import com.saatco.murshadik.adapters.DesignV3.InfinityImageIndecatorAdapter;
import com.saatco.murshadik.databinding.ActivityFarmVisitDetailsBinding;
import com.saatco.murshadik.model.FarmVisit.FarmVisit;
import com.saatco.murshadik.model.FarmVisit.MediaOfVisitFarm;
import com.saatco.murshadik.utils.Consts;
import com.saatco.murshadik.utils.DialogUtil;
import com.saatco.murshadik.utils.IntentUtils;
import com.saatco.murshadik.utils.MyCallbackHandler;
import com.saatco.murshadik.utils.Util;

import java.util.ArrayList;

public class FarmVisitDetailsActivity extends AppCompatActivity {

    private ActivityFarmVisitDetailsBinding binding;
    FarmVisit farmVisit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFarmVisitDetailsBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        ToolbarHelper.setToolBarTransWithTitle(this, getString(R.string.farm_visit_detailes), findViewById(R.id.toolbarTrans));
        LanguageUtil.changeLanguage(this);

        int[] colors = {getColor(R.color.f_light_brown)
                , getColor(R.color.f_light_gray)
                , getColor(R.color.f_light_green)
                , getColor(R.color.f_light_yellow)};


        farmVisit = (FarmVisit) getIntent().getSerializableExtra(Consts.FARM_DATA_EXTRA);
        if (farmVisit == null) return;

        binding.tvCity.setText(farmVisit.getCity());
        binding.tvRegion.setText(farmVisit.getRegion());
        binding.tvFarmName.setText(farmVisit.getFarmName());
        binding.tvVisitDate.setText(farmVisit.getVisitDateString().split("T")[0]);
        binding.tvReason.setText(farmVisit.getPurposeOfVisit());

        String ministryBranch = getString(R.string.ministry_branch_in) + farmVisit.getRegion();
        binding.tvMinistryBranch.setText(ministryBranch);

        if (farmVisit.getOrderStatus() != null) {
            binding.flRequestState.setBackgroundTintList(ColorStateList.valueOf(colors[farmVisit.getOrderStatus().getStatus()]));
            binding.tvRequestState.setText(farmVisit.getOrderStatus().getDescription());
        }

        binding.imgBtnFarmLocation.setOnClickListener(v -> {
            if (farmVisit.getLocation() == null) return;
            Uri uri = Uri.parse(farmVisit.getLocation());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });

        setSlider(farmVisit.getMedias());

        if (farmVisit.getReport() != null)
            if (!farmVisit.getReport().isEmpty()) {
                binding.flFarmReport.setBackgroundColor(getColor(R.color.light_green_navbar_bg));
            }
        binding.imgBtnFarmReport.setOnClickListener(view1 -> {
            if (farmVisit.getReport() != null)
                if (farmVisit.getReport().isEmpty()) {
                    Util.showToast(getString(R.string.no_data), this);
                } else {
                    openReports(farmVisit.getReport());
                }
        });

    }

    private void openReports(ArrayList<MediaOfVisitFarm> reports) {
        if (reports == null) return;
        if (reports.isEmpty()) return;
        if (reports.size() == 1) {
            openPdfFileFromDefaultApps(reports.get(0).getPath());
            return;
        }
        DialogUtil.DialogListOfItems<MediaOfVisitFarm> dialogListOfItems
                = new DialogUtil.DialogListOfItems<>(this, getString(R.string.reports), reports, new MyCallbackHandler<View>() {
            @Override
            public void onResponse(View msg) {

            }

            @Override
            public void onPosition(int position) {
                MyCallbackHandler.super.onPosition(position);
                openPdfFileFromDefaultApps(reports.get(position).getPath());
            }
        });
        dialogListOfItems.show();
    }

    private void openPdfFileFromDefaultApps(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW);
        browserIntent.setDataAndType(Uri.parse(url), Consts.MIME_PDF);

        Intent chooser = Intent.createChooser(browserIntent, getString(R.string.chooser_title));
        chooser.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // optional

        if (IntentUtils.isActivityForIntentAvailable(this, chooser))
            startActivity(chooser);
        else openPdfFileFromLocalViewer(url);
    }

    private void openPdfFileFromLocalViewer(String url) {
        Intent browserIntent = new Intent(this, PDFViewerActivity.class);
        browserIntent.putExtra("file_uri", url);
        browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(browserIntent);
    }

    private void setSlider(ArrayList<MediaOfVisitFarm> medias) {

        if (medias == null) return;

        ArrayList<String> imagesUrl = new ArrayList<>();

        for (MediaOfVisitFarm media : medias)
            imagesUrl.add(media.getPath());


        //********************* set slider ******************//
        binding.carouselRecyclerView.setAdapter(new InfinityImageIndecatorAdapter(getApplicationContext(), imagesUrl, imageUrl -> {
            Intent intent = new Intent(FarmVisitDetailsActivity.this, ViewImageActivity.class);
            intent.putExtra("URL", imageUrl);
            startActivity(intent);
        }));
        CarouselLayoutManager layoutManager = new CarouselLayoutManager(new FullScreenCarouselStrategy(), RecyclerView.HORIZONTAL);
        binding.carouselRecyclerView.setLayoutManager(layoutManager);

        CarouselSnapHelper snapHelper = new CarouselSnapHelper();
        snapHelper.attachToRecyclerView(binding.carouselRecyclerView);

    }
}
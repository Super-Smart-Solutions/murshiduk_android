package com.saatco.murshadik;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.ArrayMap;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;

import com.canhub.cropper.CropImageOptions;
import com.canhub.cropper.CropImageView;
import com.saatco.murshadik.Helpers.ToolbarHelper;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.api.APIInterface;
import com.saatco.murshadik.api.response.CoffeePlantResponse;
import com.saatco.murshadik.utils.Consts;
import com.saatco.murshadik.utils.ImageUtils;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CoffeePlantActivity extends AppCompatActivity {

    ImageView btnPickImage;


    View loader;

    ActivityResultLauncher<CropImageContractOptions> cropImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coffee_plant);

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        btnPickImage = findViewById(R.id.btnPickPhoto);

        ToolbarHelper.setToolBar(this, "", findViewById(R.id.toolbar));

        loader = findViewById(R.id.ll_progress_bar).getRootView();



        cropImageLauncher = registerForActivityResult(
                new CropImageContract(),
                result -> {
                    if (result.isSuccessful()) {
                        Uri resultUri = result.getUriContent();
                        Bitmap bitmap = null;
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if (bitmap != null) {
                            getCoffeePlantDisease(ImageUtils.encodeImage(bitmap));
                        } else
                            Toast.makeText(this, "No Image Chosen", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Error cropping image", Toast.LENGTH_LONG).show();
                    }
                }
        );

        CropImageOptions options = new CropImageOptions();
        options.guidelines = CropImageView.Guidelines.ON;
        options.cropShape = CropImageView.CropShape.RECTANGLE;
        options.activityTitle = getResources().getString(R.string.best_result_crop);
        options.allowRotation = false;
        options.allowFlipping = false;
        options.cropMenuCropButtonTitle = getResources().getString(R.string.crop);

        cropImageLauncher.launch(
                new CropImageContractOptions(
                     null,
                        options
                )
        );

    }



    private void getCoffeePlantDisease(String base64) {

        loader.setVisibility(View.VISIBLE);

        Map<String, Object> jsonParams = new ArrayMap<>();
        jsonParams.put("imageBase64Url", base64);
        RequestBody body = RequestBody.create((new JSONObject(jsonParams)).toString(), okhttp3.MediaType.parse("application/json; charset=utf-8"));

        APIInterface apiInterface = APIClient.getClientCoffee().create(APIInterface.class);
        Call<CoffeePlantResponse> call = apiInterface.getCoffeeDisease(body);
        call.enqueue(new Callback<CoffeePlantResponse>() {
            @Override
            public void onResponse(@NonNull Call<CoffeePlantResponse> call, @NonNull Response<CoffeePlantResponse> response) {

                loader.setVisibility(View.GONE);

                try {

                    if (response.body() != null) {

                        if (response.body().getType() != Consts.HEALTHY) {
                            Intent intent = new Intent(CoffeePlantActivity.this, CalendarDetailActivity.class);
                            intent.putExtra("CAT_ID", response.body().getClassId());
                            intent.putExtra("is_coffee", true);
                            intent.putExtra("value", response.body().getConfidence());
                            startActivity(intent);
                        } else {
                            showHealthyDialog();
                        }
                    }

                } catch (Exception ignored) {
                }

            }

            @Override
            public void onFailure(@NonNull Call<CoffeePlantResponse> call, @NonNull Throwable t) {
                loader.setVisibility(View.GONE);
            }
        });

    }

    private void showHealthyDialog() {
        final Dialog dialog = new Dialog(CoffeePlantActivity.this);
        dialog.setContentView(R.layout.dialog_healthy);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Button btnDone = dialog.findViewById(R.id.btnDone);
        btnDone.setOnClickListener(view -> dialog.dismiss());

        dialog.show();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));

        Configuration config = new Configuration(base.getResources().getConfiguration());
        config.fontScale = 1.2f;
        applyOverrideConfiguration(config);
    }
}
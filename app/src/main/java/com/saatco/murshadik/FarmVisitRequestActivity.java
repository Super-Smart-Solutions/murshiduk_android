package com.saatco.murshadik;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.saatco.murshadik.Helpers.PermissionsHelper;
import com.saatco.murshadik.Helpers.TokenHelper;
import com.saatco.murshadik.Helpers.ToolbarHelper;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.api.APIInterface;
import com.saatco.murshadik.api.response.RegionResponse;
import com.saatco.murshadik.databinding.ActivityFarmVisitRequestBinding;
import com.saatco.murshadik.model.City;
import com.saatco.murshadik.model.Item;
import com.saatco.murshadik.model.consultantvideos.NewAPIsResponse;
import com.saatco.murshadik.utils.DialogUtil;
import com.saatco.murshadik.utils.StorageUtil;
import com.saatco.murshadik.utils.Util;
import com.vanillaplacepicker.data.VanillaAddress;
import com.vanillaplacepicker.presentation.builder.VanillaPlacePicker;
import com.vanillaplacepicker.utils.KeyUtils;
import com.vanillaplacepicker.utils.MapType;
import com.vanillaplacepicker.utils.PickerLanguage;
import com.vanillaplacepicker.utils.PickerType;

import org.joda.time.DateTime;

import java.io.File;
import java.lang.reflect.Type;
import java.net.URLConnection;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FarmVisitRequestActivity extends AppCompatActivity {

    final static int MAX_IMAGES_COUNT = 5;

    private ActivityFarmVisitRequestBinding binding;
    private final String BASE_MAP_URL = "http://maps.google.com/maps?q=";
    private static final int GALLERY_PERMISSION_REQUEST_CODE = 555;
    private ActivityResultLauncher<Intent> activityResultLaunchForPickImg;
    private VanillaAddress vanillaAddress;
    private ArrayList<Item> regions;
    private Item region;
    private City city;
    private ArrayList<File> imagesUris;
    private String location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFarmVisitRequestBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        ToolbarHelper.setToolBarTransWithTitle(this, getString(R.string.new_farm_visit_request), findViewById(R.id.toolbarTrans));
        LanguageUtil.changeLanguage(this);

        initRegions();
        //*********************** api calls *********************** //
        if (regions == null)
            getRegions();


        ActivityResultLauncher<Intent> activityResultLaunch = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getData() == null) return;

                    vanillaAddress = VanillaPlacePicker.Companion.onActivityResult(
                            KeyUtils.REQUEST_PLACE_PICKER, result.getResultCode(), result.getData()
                    );
                    if (vanillaAddress == null) return;

                    String latAndLong = vanillaAddress.getLatitude() + "," + vanillaAddress.getLongitude();
                    location = BASE_MAP_URL + latAndLong + "&ll=" + latAndLong + "&z=17";
                    binding.imgBtnFarmLocation.setBackgroundColor(getColor(android.R.color.holo_green_light));
                });

        activityResultLaunchForPickImg = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    //getting images picked by user for farm problem
                    if (result.getData() == null)
                        return;
                    ArrayList<Uri> uris = new ArrayList<>();
                    ArrayList<File> fileUris = new ArrayList<>();
                    int count = result.getData().getClipData().getItemCount();
                    if (count > MAX_IMAGES_COUNT) {
                        count = MAX_IMAGES_COUNT;
                        String msg = getString(R.string.reach_max_num, MAX_IMAGES_COUNT + getString(R.string.image));
                        Util.showToast(msg, this);
                    }
                    Uri tempUri;
                    for (int i = 0; i < count; i++) {
                        tempUri = result.getData().getClipData().getItemAt(i).getUri();
                        uris.add(tempUri);
                        fileUris.add(new File(StorageUtil.getRealPathFromURIPath(tempUri, FarmVisitRequestActivity.this)));
                    }
                    imagesUris = fileUris;
                    showSelectedImages(uris);

                });

        binding.imgBtnImgProblem.setOnClickListener(v -> {
            if (PermissionsHelper.isMediaImagePermissionGranted(this))
                pickImages();
        });

        binding.imgBtnFarmLocation.setOnClickListener(v -> {
            Double latitude = 24.692703, longitude = 46.681580;
            if (vanillaAddress != null) {
                if (vanillaAddress.getLatitude() != null && vanillaAddress.getLongitude() != null) {
                    latitude = vanillaAddress.getLatitude();
                    longitude = vanillaAddress.getLongitude();
                }
            }
            Intent intent = new VanillaPlacePicker.Builder(this)
                    .with(PickerType.MAP_WITH_AUTO_COMPLETE) // Select Picker type to enable auto complete, map or both
                    .withLocation(latitude, longitude)
                    .setPickerLanguage(PickerLanguage.ARABIC) // Apply language to picker
                    .setCountry("SA") // Only for Autocomplete
                    /*
                     * Configuration for Map UI
                     */
                    .setMapType(MapType.NORMAL) // Choose map type (Only applicable for map screen)
                    .setMapPinDrawable(android.R.drawable.ic_menu_mylocation) // To give custom pin image for map marker
                    .build();

            activityResultLaunch.launch(intent);
        });

        binding.cetDate.setOnClickListener(v -> {
            long dayInMillis = 86400000;
            DatePickerDialog datePickerDialog = new DatePickerDialog(this);
            datePickerDialog.getDatePicker().setMinDate(DateTime.now().getMillis()+dayInMillis);
            datePickerDialog.show();
            datePickerDialog.setOnDateSetListener((datePicker, i, i1, i2) -> {
                String date = i + "-" + i1 + "-" + i2;
                binding.cetDate.setText(date);
            });
        });

        binding.cetRegion.setOnClickListener(v -> showRegionDialog(getString(R.string.select_a_region), regions));

        binding.cetCity.setOnClickListener(v -> {
            if (region != null)
                showCityDialog(getString(R.string.select_a_city), region.getCities());
            else Util.showErrorToast(getString(R.string.select_region), this);
        });

        binding.btnSendRequest.setOnClickListener(v -> {
            if (isFormFill())
                postFarmRequest();
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == GALLERY_PERMISSION_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            pickImages();
        } else if (requestCode == GALLERY_PERMISSION_REQUEST_CODE) {
            Util.showErrorToast(getString(R.string.app_need_permission), this);
        }
    }

    //pick image
    private void pickImages() {

        Intent galleryIntent = new Intent().setAction(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        String[] mimetypes = {"image/*"};
        galleryIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);


        //start gallery intent activity
        activityResultLaunchForPickImg.launch(galleryIntent);
    }

    private void showSelectedImages(ArrayList<Uri> imgUris) {
        binding.llPickedImages.removeAllViews();
        for (int i = 0; i < imgUris.size(); i++) {
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(Util.dpToPixels(this, 30), Util.dpToPixels(this, 30)));
            imageView.setImageURI(imgUris.get(i));

            binding.llPickedImages.addView(imageView);
        }
    }

    boolean isFormFill() {
        if (TextUtils.isEmpty(binding.cetFarmName.getText().toString().trim()) ||
                TextUtils.isEmpty(binding.cetCity.getText().toString().trim()) ||
                TextUtils.isEmpty(binding.cetReason.getText().trim()) ||
                TextUtils.isEmpty(binding.cetDate.getText().toString().trim()) ||
                TextUtils.isEmpty(binding.cetRegion.getText().toString().trim())) {
            Util.showToast(getResources().getString(R.string.fields_required), this);
            return false;
        }
        if (imagesUris == null) {
            Util.showToast(getResources().getString(R.string.you_should_add_image), this);
            return false;
        }
        if (location == null) {
            Util.showToast(getResources().getString(R.string.you_should_add_farm_location), this);
            return false;
        }
        return true;
    }

    private void showRegionDialog(String title, ArrayList<Item> regions) {

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_regions);

        ListView listView = dialog.findViewById(R.id.statusListView);
        TextView textView = dialog.findViewById(R.id.title);
        textView.setText(title);

        ArrayList<String> itemNameList = new ArrayList<>();


        for (Item item : regions)
            itemNameList.add(item.getNameAr());


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, itemNameList);


        // Assign adapter to ListView
        listView.setAdapter(adapter);

        // ListView Item Click Listener
        listView.setOnItemClickListener((parent, view, position, id) -> {

            // ListView Clicked item index
            String itemValue = (String) listView.getItemAtPosition(position);

            binding.cetRegion.setText(itemValue);
            region = this.regions.get(position);
            binding.cetCity.setText("");

            dialog.dismiss();

        });


        dialog.show();

    }

    private void showCityDialog(String title, ArrayList<City> cities) {

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_regions);

        ListView listView = dialog.findViewById(R.id.statusListView);
        TextView textView = dialog.findViewById(R.id.title);
        textView.setText(title);

        ArrayList<String> itemNameList = new ArrayList<>();


        for (City item : cities)
            itemNameList.add(item.getNameAr());


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, itemNameList);


        // Assign adapter to ListView
        listView.setAdapter(adapter);

        // ListView Item Click Listener
        listView.setOnItemClickListener((parent, view, position, id) -> {

            // ListView Clicked item index
            String itemValue = (String) listView.getItemAtPosition(position);

            binding.cetCity.setText(itemValue);
            city = region.getCities().get(position);


            dialog.dismiss();

        });


        dialog.show();

    }


    private void getRegions() {

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<RegionResponse> call = apiInterface.getRegionsWithCity();
        call.enqueue(new Callback<RegionResponse>() {
            @Override
            public void onResponse(@NonNull Call<RegionResponse> call, @NonNull Response<RegionResponse> response) {

                try {

                    assert response.body() != null;
                    regions = response.body().getRegionList();

                } catch (Exception ignored) {
                }
            }

            @Override
            public void onFailure(@NonNull Call<RegionResponse> call, @NonNull Throwable t) {

            }
        });


    }

    void initRegions() {
        ArrayList<Item> regionsList;
        Gson gson = new Gson();
        String positions = PrefUtil.getStringPref(App.getInstance(), "regions");
        Type type = new TypeToken<ArrayList<Item>>() {
        }.getType();
        regionsList = gson.fromJson(positions, type);

        if (regionsList != null) {
            regions = regionsList;
        }
    }

    public static MultipartBody.Part prepareFilePart(String partName, File file) {
        //   File file = new File(fileUri);
        String mimeType = URLConnection.guessContentTypeFromName(file.getName());
        Log.e("mimeType", mimeType);
        RequestBody requestFile = RequestBody.create(file, MediaType.parse(mimeType));
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

    private void postFarmRequest() {

        binding.llProgressBar.getRoot().setVisibility(View.VISIBLE);
        Util.disableInteraction(this);

        final MultipartBody.Part farmName = MultipartBody.Part.createFormData("FarmName", binding.cetFarmName.getText().toString());
        final MultipartBody.Part regionId = MultipartBody.Part.createFormData("RegionId", region.getId() + "");
        final MultipartBody.Part cityId = MultipartBody.Part.createFormData("CityId", city.getId() + "");
        final MultipartBody.Part purposeOfVisit = MultipartBody.Part.createFormData("PurposeOfVisit", binding.cetReason.getText());
        final MultipartBody.Part location = MultipartBody.Part.createFormData("Location", this.location);
        final MultipartBody.Part visitDate = MultipartBody.Part.createFormData("VisitDate", binding.cetDate.getText().toString());

        ArrayList<MultipartBody.Part> files = new ArrayList<>();

        for (int i = 0; i < imagesUris.size(); i++) {
            files.add(prepareFilePart("file_" + (i + 1), imagesUris.get(i)));
        }
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<NewAPIsResponse<String>> call = apiInterface.postFarmVisitRequest("Bearer " + TokenHelper.getToken(), farmName, regionId, cityId, purposeOfVisit, location, visitDate, files);
        call.enqueue(new Callback<NewAPIsResponse<String>>() {
            @Override
            public void onResponse(@NonNull Call<NewAPIsResponse<String>> call, @NonNull Response<NewAPIsResponse<String>> response) {
                assert response.body() != null;
                if (response.body().getStatus()) {
                    DialogUtil.showInfoDialogAndFinishActivity(FarmVisitRequestActivity.this, 1, "تم إضافة طلب زيارة إلى المزرعة", "إضافة");
                    Log.v("response status msg: ", response.body().getMessage());
                } else {
                    Log.v("response error: ", response.body().getMessage());
                }

                binding.llProgressBar.getRoot().setVisibility(View.GONE);
                Util.enableInteraction(FarmVisitRequestActivity.this);

            }

            @Override
            public void onFailure(@NonNull Call<NewAPIsResponse<String>> call, @NonNull Throwable t) {
                Util.showErrorToast("عذرا لم يتم إضافة طلب زيارة إلى المزرعة", FarmVisitRequestActivity.this);
                Log.e("Error msg: ", t.getMessage());

                binding.llProgressBar.getRoot().setVisibility(View.GONE);
                Util.enableInteraction(FarmVisitRequestActivity.this);
            }
        });

    }
}
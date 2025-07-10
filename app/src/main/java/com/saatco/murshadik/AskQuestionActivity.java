package com.saatco.murshadik;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.javiersantos.bottomdialogs.BottomDialog;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.saatco.murshadik.Helpers.PermissionsHelper;
import com.saatco.murshadik.Helpers.TokenHelper;
import com.saatco.murshadik.Helpers.ToolbarHelper;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.api.APIInterface;
import com.saatco.murshadik.api.response.BaseResponse;
import com.saatco.murshadik.databinding.ActivityAskQuestionBinding;
import com.saatco.murshadik.fragments.ModalBottomSheet;
import com.saatco.murshadik.model.Item;
import com.saatco.murshadik.model.Question;
import com.saatco.murshadik.utils.Consts;
import com.saatco.murshadik.utils.FileUtils;
import com.saatco.murshadik.utils.KeyboardUtils;
import com.saatco.murshadik.utils.StorageUtil;
import com.saatco.murshadik.utils.Util;


import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AskQuestionActivity extends AppCompatActivity {

    MaterialSpinner materialSpinner;
    AutoCompleteTextView autoCompleteKeywords;
    EditText etTitle;
    EditText etQuestion;
    LinearLayout filesLayout;
    Button btnSave;
    ProgressBar progressBar;
    Button btnAddKeyword;
    TextView tvTags;
    TextView tvQuestionTags;
    LinearLayout llProgressBar;


    private final static int GALLERY_PICK_CODE = 828;
    private static final int CAMERA_REQUEST = 4659;

    private ActivityResultLauncher<Intent> galleryActivityResultLauncher;


    ArrayList<File> fileUris = new ArrayList<>();
    ArrayList<String> keywords = new ArrayList<>();

    String keywordStr = "";
    int categoryId = 1;

    CityAdapter cityAdapter;

    ArrayList<Item> resultedKeyword = new ArrayList<>();
    ArrayList<Item> randomKeywords = new ArrayList<>();

    Button btnDone;

    ArrayList<Item> selectedItems = new ArrayList<>();

    ProgressBar progressBarKeyowords;

    File imageFile;

    Uri mMakePhotoUri;

    String search = "";
    private String currentPhotoPath;

    BottomDialog builder;

    private static final int ATTACHMENTS_LIMIT = 4;

    private ActivityAskQuestionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAskQuestionBinding.inflate(getLayoutInflater(), null, false);
        setContentView(binding.getRoot());

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        initViews();


        ToolbarHelper.setToolBar(this, getString(R.string.new_question), findViewById(R.id.toolbarTrans));

        randomKeywords = (ArrayList<Item>) getIntent().getSerializableExtra("keywords");
        if (randomKeywords != null)
            resultedKeyword.addAll(randomKeywords);

        btnAddKeyword.setVisibility(View.GONE);


        PrefUtil.writeBooleanValue(getApplicationContext(), "is_question_asked", false);

        //********************* save questions api ******************//

        btnSave.setOnClickListener(view -> {
            if (checkForm()) {
                btnSave.setEnabled(false);
                btnAddKeyword.setEnabled(false);
                etQuestion.setEnabled(false);
                materialSpinner.setEnabled(false);
                KeyboardUtils.hideKeyboard(etQuestion);
                postQuestion();
            }

        });

        //********************* show attachments ******************//
        binding.tvAttachment.setOnClickListener(view -> showImageOption());

        btnAddKeyword.setOnClickListener(view -> showKeywordsDialog());

        ArrayList<String> categories = new ArrayList<>();
        categories.add(getResources().getString(R.string.disease));
        categories.add(getResources().getString(R.string.planting));

        materialSpinner.setItems(categories);
        materialSpinner.setHint("اختر الفئة");
        materialSpinner.setOnItemSelectedListener((MaterialSpinner.OnItemSelectedListener<String>) (view, position, id, item) -> {

            if (position == 0)
                categoryId = Consts.DISEASE;
            else if (position == 1)
                categoryId = Consts.PLANTING;

        });

        tvQuestionTags.setOnClickListener(view -> showKeywordsDialog());

        galleryActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Uri uri = data.getData();
                            // Handle the selected image URI
                        }
                    }
                }
        );

    }

    private void initViews() {
        materialSpinner = binding.spinnerCategory;
        autoCompleteKeywords = binding.tvKeywords;
        etTitle = binding.etTitle;
        etQuestion = binding.etQuestion;
        filesLayout = binding.filesLayout;
        btnSave = binding.btnDone;
        progressBar = binding.progressBar;
        btnAddKeyword = binding.btnAddKeyword;
        tvTags = binding.tvTags;
        tvQuestionTags = binding.tvQuestionTags;
        llProgressBar = binding.llProgressBar.getRoot();
    }


    private void showImageOption() {

        ArrayList<String> categories = new ArrayList<>();
        categories.add(getResources().getString(R.string.camera));
        categories.add(getResources().getString(R.string.gallery));


        ModalBottomSheet<String> modalBottomSheet = new ModalBottomSheet.Builder<String>()
                .setTitle(getResources().getString(R.string.select_option))
                .setItems(categories)
                .setListener((position, item) -> {

                    if (position == 0) {

                        if (fileUris.size() < ATTACHMENTS_LIMIT) {
                            pickImage();
                        }

                    } else {

                        if (fileUris.size() < ATTACHMENTS_LIMIT) {
                            if (PermissionsHelper.isMediaImagePermissionGranted(this)) {
                                Intent galleryIntent = new Intent().setAction(Intent.ACTION_PICK);
                                galleryIntent.setType("image/*");
                                galleryActivityResultLauncher.launch(galleryIntent);
                            }
                        }
                    }

                })
                .build();
        modalBottomSheet.show(getSupportFragmentManager(), "image_option");


//
//        BottomSheet.Builder builder = new BottomSheet.Builder(AskQuestionActivity.this);
//        builder.setTitle(getResources().getString(R.string.select_option))
//                .setItems(categories, (dialogInterface, i) -> {
//
//                    if (i == 0) {
//
//                        if (fileUris.size() < ATTACHMENTS_LIMIT) {
//                            pickImage();
//                        }
//
//                    } else {
//
//                        if (fileUris.size() < ATTACHMENTS_LIMIT) {
//                            if (PermissionsHelper.isMediaImagePermissionGranted(this)) {
//                                Intent galleryIntent = new Intent().setAction(Intent.ACTION_PICK);
//                                galleryIntent.setType("image/*");
//                                startActivityForResult(galleryIntent, GALLERY_PICK_CODE);
//                            }
//                        }
//                    }
//
//                })
//                .setTitleTextColorRes(R.color.black)
//                .setItemTextColor(getResources().getColor(R.color.colorPrimary, null))
//                .show();

    }

    private void pickImage() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

            if (PermissionsHelper.isMediaImagePermissionGranted(this)) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Ensure that there's a camera activity to handle the intent
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(this,
                                getPackageName() + ".fileprovider",
                                photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, CAMERA_REQUEST);
                    }
                }

            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 4);
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void addKeyword(String keyword) {
        if (!keyword.equals("")) {
            progressBarKeyowords.setVisibility(View.VISIBLE);

            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            Call<BaseResponse> call = apiInterface.addKeyword("Bearer " + TokenHelper.getToken(), keyword);
            call.enqueue(new Callback<BaseResponse>() {
                @Override
                public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {

                    progressBarKeyowords.setVisibility(View.GONE);

                    try {


                        if (response.body().isStatus()) {
                            Item item = new Item();
                            item.setNameAr(keyword);
                            item.setSelected(true);

                            selectedItems.add(item);
                            resultedKeyword.add(0, item);
                            setAdapterKeyword();
                        }


                    } catch (Exception ex) {
                    }
                }

                @Override
                public void onFailure(Call<BaseResponse> call, Throwable t) {
                    progressBarKeyowords.setVisibility(View.GONE);
                }
            });
        }
    }

    private void postQuestion() {

        //  progressBar.setVisibility(View.VISIBLE);
        llProgressBar.setVisibility(View.VISIBLE);

        ArrayList<MultipartBody.Part> files = new ArrayList<MultipartBody.Part>();

        for (int i = 0; i < fileUris.size(); i++) {
            files.add(prepareFilePart("file_" + (i + 1), fileUris.get(i)));
        }

        final MultipartBody.Part title = MultipartBody.Part.createFormData("title", etTitle.getText().toString());
        final MultipartBody.Part description = MultipartBody.Part.createFormData("description", etQuestion.getText().toString());
        final MultipartBody.Part keyword = MultipartBody.Part.createFormData("keywords", keywordStr);
        final MultipartBody.Part category_id = MultipartBody.Part.createFormData("category_id", String.valueOf(categoryId));


        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<BaseResponse> call = apiInterface.postQuestion("Bearer " + TokenHelper.getToken(), title, description, keyword, category_id, files);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {


                try {
                    PrefUtil.writeBooleanValue(getApplicationContext(), "is_question_asked", true);
                    finish();

                } catch (Exception ignored) {
                }
            }

            @Override
            public void onFailure(@NonNull Call<BaseResponse> call, @NonNull Throwable t) {
                //  progressBar.setVisibility(View.GONE);
                llProgressBar.setVisibility(View.GONE);
                binding.tvAttachment.setEnabled(true);
                btnAddKeyword.setEnabled(true);
                etQuestion.setEnabled(true);
                materialSpinner.setEnabled(true);
                btnSave.setEnabled(true);
            }
        });

    }

    public static MultipartBody.Part prepareFilePart(String partName, File file) {
        //   File file = new File(fileUri);
        String mimeType = URLConnection.guessContentTypeFromName(file.getName());
        Log.e("mimeType", mimeType);
        RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), file);
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

    private void getKeywords(String keyword) {
        progressBarKeyowords.setVisibility(View.VISIBLE);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<List<Question>> call = apiInterface.getAutoCompleteKeyword("Bearer " + TokenHelper.getToken(), keyword);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<Question>> call, Response<List<Question>> response) {

                try {

                    progressBarKeyowords.setVisibility(View.GONE);

                    ArrayList<Question> keywordsList = (ArrayList<Question>) response.body();

                    resultedKeyword.clear();

                    for (Question keyword : keywordsList) {
                        Item item = new Item();
                        item.setId(keyword.getId());
                        item.setNameAr(keyword.getName());
                        resultedKeyword.add(item);
                    }

                    for (Item selected : selectedItems) {
                        if (!isExistInResulted(selected.getNameAr()))
                            resultedKeyword.add(selected);
                    }

                    if (resultedKeyword.size() < 1 && randomKeywords != null)
                        resultedKeyword.addAll(randomKeywords);


                    if (search.equals("")) {
                        if (randomKeywords != null)
                            for (Item random : randomKeywords) {
                                if (isMissingInResultedList(random.getNameAr())) {
                                    resultedKeyword.add(random);
                                }
                            }
                    }

                    setAdapterKeyword();


                } catch (Exception ex) {
                }
            }

            @Override
            public void onFailure(Call<List<Question>> call, Throwable t) {
                progressBarKeyowords.setVisibility(View.VISIBLE);
            }
        });

    }


    private boolean isExistInResulted(String keyword) {

        for (Item item : resultedKeyword) {
            if (item.getNameAr().equals(keyword)) {
                item.setSelected(true);
                return true;
            }
        }
        return false;
    }

    private boolean isMissingInResultedList(String keyword) {

        for (Item item : resultedKeyword) {
            if (item.getNameAr().equals(keyword)) {
                return false;
            }
        }
        return true;
    }

    private void showKeywordsDialog() {

        resultedKeyword.clear();

        for (Item selected : selectedItems) {
            if (!isExistInResulted(selected.getNameAr()))
                resultedKeyword.add(selected);
        }

        if (randomKeywords != null)
            for (Item random : randomKeywords) {
                if (isMissingInResultedList(random.getNameAr())) {
                    resultedKeyword.add(random);
                }
            }


        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.dialog_keywords, null);

        //stop multiple dialog window
        if (builder != null) {
            builder.dismiss();
            builder = null;
        }

        builder = new BottomDialog.Builder(AskQuestionActivity.this)
                .setTitle("تصفية حسب الفئة")
                //.setCustomView(customView)
                .setCustomView(customView, 0, 0, 0, 50)
                .show();

        btnDone = customView.findViewById(R.id.btnDone);
        ImageView addKeyword = customView.findViewById(R.id.btnSaveKeyword);
        progressBarKeyowords = customView.findViewById(R.id.progressBar);

        RecyclerView recyclerView = customView.findViewById(R.id.rv_keywords);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        androidx.appcompat.widget.SearchView searchView = customView.findViewById(R.id.searchView);

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false);
            }
        });

        searchView.setIconifiedByDefault(false);

        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                if (query.length() > 0) {
                    //KeyboardUtils.hideKeyboard(searchView);
                    search = query;
                    getKeywords(query);
                } else if (query.equals("")) {

                    search = "";
                    resultedKeyword.clear();

                    for (Item selected : selectedItems) {
                        if (!isExistInResulted(selected.getNameAr()))
                            resultedKeyword.add(selected);
                    }

                    if (randomKeywords != null)
                        for (Item random : randomKeywords) {
                            if (isMissingInResultedList(random.getNameAr())) {
                                resultedKeyword.add(random);
                            }
                        }

                    setAdapterKeyword();
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 2) {
                    getKeywords(newText);
                    search = newText;
                } else if (newText.equals("")) {

                    search = "";
                    resultedKeyword.clear();

                    for (Item selected : selectedItems) {
                        if (!isExistInResulted(selected.getNameAr()))
                            resultedKeyword.add(selected);
                    }

                    if (randomKeywords != null)
                        for (Item random : randomKeywords) {
                            if (isMissingInResultedList(random.getNameAr())) {
                                resultedKeyword.add(random);
                            }
                        }

                    setAdapterKeyword();
                }
                return false;
            }

        });

        cityAdapter = new CityAdapter(resultedKeyword, getApplicationContext(), new CityAdapter.OnSelectItemClickListener() {
            @Override
            public void onCitySelect(View view, int position, Item city) {


                if (city.isSelected()) {
                    city.setSelected(false);

                    Iterator<Item> itr = selectedItems.iterator();
                    while (itr.hasNext()) {
                        if (itr.next().getId() == city.getId()) {
                            itr.remove();
                        }
                    }

                } else {
                    city.setSelected(true);
                    selectedItems.add(city);
                }

                resultedKeyword.clear();

                for (Item selected : selectedItems) {
                    if (!isExistInResulted(selected.getNameAr()))
                        resultedKeyword.add(selected);
                }

                if (randomKeywords != null)
                    for (Item random : randomKeywords) {
                        if (isMissingInResultedList(random.getNameAr())) {
                            resultedKeyword.add(random);
                        }
                    }

                cityAdapter.notifyDataSetChanged();
            }
        });

        recyclerView.setAdapter(cityAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        addKeyword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addKeyword(searchView.getQuery().toString());
                search = "";
                searchView.clearFocus();
                searchView.setQuery("", false);

            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                keywordStr = "";
                String withTagStr = "";

                for (Item item : resultedKeyword) {
                    if (item.isSelected()) {
                        keywordStr = keywordStr + item.getNameAr() + ",";
                        withTagStr = withTagStr + "#" + item.getNameAr() + ",";
                    }
                }

              /*  if(!keywordStr.equals(""))
                    tvTags.setVisibility(View.VISIBLE);
                else
                    tvTags.setVisibility(View.GONE);*/

                tvQuestionTags.setText(withTagStr);

                if (withTagStr.equals("")) {
                    tvQuestionTags.setText("الكلمات الدالة");
                }

                builder.dismiss();
            }
        });

    }

    private void setAdapterKeyword() {
        cityAdapter.notifyDataSetChanged();
    }

    private boolean checkForm() {
        if (TextUtils.isEmpty(etTitle.getText().toString()) || TextUtils.isEmpty(etQuestion.getText().toString()) || categoryId == 0 || keywordStr.equals("")) {
            Util.showToast(getResources().getString(R.string.fields_required), this);
            return false;
        }

        if (fileUris.isEmpty()) {
            Util.showToast(getResources().getString(R.string.attach_a_picture), this);
            return false;
        }

        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK_CODE && resultCode == RESULT_OK) {

            if (data == null) return;
            Uri uri = data.getData();

            File file = new File(StorageUtil.getRealPathFromURIPath(uri, this));
            file = FileUtils.getCompressedImageFile(file, this);

            if (file != null) {
                sendFileToServer(file, uri, file.getAbsolutePath(), true);
            } else
                Toast.makeText(this, "No Image Chosen", Toast.LENGTH_LONG).show();

        }

        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {

            Uri uri = mMakePhotoUri;

            imageFile = FileUtils.getCompressedImageFile(new File(currentPhotoPath), this);

            if (imageFile != null) {
                sendFileToServer(imageFile, uri, imageFile.getAbsolutePath(), true);
            } else
                Toast.makeText(this, "No Image Chosen", Toast.LENGTH_LONG).show();
        }
    }

    private int dpToPixels(int sizeInDp) {
        float scale = getResources().getDisplayMetrics().density;
        int dpAsPixels = (int) (sizeInDp * scale + 0.5f);
        return dpAsPixels;
    }

    private void sendFileToServer(File file, Uri fileUri, String imagePath, boolean isImage) {

        final View itemAttachment = getLayoutInflater().inflate(R.layout.item_attachment, null);
        filesLayout.addView(itemAttachment);
        final TextView name = itemAttachment.findViewById(R.id.name);
        final View progress = itemAttachment.findViewById(R.id.progress);
        final View delete = itemAttachment.findViewById(R.id.delete);
        final ImageView thumbnail = itemAttachment.findViewById(R.id.thumbnail);

        fileUris.add(file);

        if (isImage) {
            Glide.with(this).load(fileUri).into(thumbnail);
        } else {
            thumbnail.setPadding(dpToPixels(10), dpToPixels(10), dpToPixels(10), dpToPixels(10));
            thumbnail.setImageResource(com.jaiselrahman.filepicker.R.drawable.ic_file);
        }

        name.setText(file.getName());

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                filesLayout.removeView(itemAttachment);
                fileUris.remove(file);
            }
        });

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));

        Configuration config = new Configuration(base.getResources().getConfiguration());
        config.fontScale = 1.2f;
        applyOverrideConfiguration(config);
    }

}
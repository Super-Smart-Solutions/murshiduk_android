package com.saatco.murshadik;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.abedelazizshe.lightcompressorlibrary.CompressionListener;
import com.abedelazizshe.lightcompressorlibrary.VideoCompressor;
import com.abedelazizshe.lightcompressorlibrary.VideoQuality;
import com.abedelazizshe.lightcompressorlibrary.config.AppSpecificStorageConfiguration;
import com.abedelazizshe.lightcompressorlibrary.config.SaveLocation;
import com.abedelazizshe.lightcompressorlibrary.config.SharedStorageConfiguration;
import com.saatco.murshadik.Helpers.TokenHelper;
import com.saatco.murshadik.Helpers.ToolbarHelper;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.api.APIInterface;
import com.saatco.murshadik.model.consultantvideos.NewAPIsResponse;
import com.saatco.murshadik.model.consultantvideos.CategoryDataOfConsultantVideos;
import com.saatco.murshadik.model.consultantvideos.CategorySubDataOfConsultantVideos;
import com.saatco.murshadik.model.consultantvideos.VideoDataOfConsultantVideos;
import com.saatco.murshadik.utils.DialogUtil;
import com.saatco.murshadik.utils.ToastUtils;
import com.saatco.murshadik.utils.Util;
import com.saatco.murshadik.views.EditWithBorderTitle;
import com.saatco.murshadik.views.ScrollableEditText;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddVideoByConsultantsActivity extends BaseActivity implements AdapterView.OnItemSelectedListener {


    EditWithBorderTitle areas_indicative;
    EditWithBorderTitle department_indicative;
    Button btn_submit;
    ImageView btn_chose_video, btn_start_stop_video;
    CardView card_view_video_preview;
    SwitchCompat switch_hide_show_clip;
    EditWithBorderTitle et_clip_title, et_tags;
    ScrollableEditText et_description;
    VideoView video_view_preview;
    MediaController mediaControls;
    File file;
    LinearLayout ll_progress_bar, ll_chose_video;
    TextView tv_layout_title;

    String video_id = null;
    boolean is_video_attached = false, is_editing_data = false, is_area_or_department_been_changed = false;

    ArrayAdapter<CategorySubDataOfConsultantVideos> arrayAdapter_department_indicative;
    ArrayAdapter<CategoryDataOfConsultantVideos> arrayAdapter_areas_indicative;
    ArrayList<CategorySubDataOfConsultantVideos> department_indicative_names;
    ArrayList<CategoryDataOfConsultantVideos> areas_indicative_names;
    NewAPIsResponse<ArrayList<CategoryDataOfConsultantVideos>> consultantVideosCategory;

    public final int MAX_TIME_AVAILABLE_IN_SECONDS = 63;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_video_by_consultants);
        ToolbarHelper.setToolBarTrans(this, "", findViewById(R.id.toolbarTrans));

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        initViews();


        is_editing_data = getIntent().getBooleanExtra("is_edit", false);
        editingMode();

        department_indicative_names = new ArrayList<>();


        mediaControls = new MediaController(this);
        mediaControls.setAnchorView(video_view_preview);

        ActivityResultLauncher<Intent> galleryResultLauncher = initGalleryResultLauncher(initTrimVideoActivity());

        getVideoCategoriesFromAPI();
        Log.v("token is : ", TokenHelper.getToken());

        video_view_preview.setOnErrorListener((mp, what, extra) -> {
            is_video_attached = false;
            card_view_video_preview.setVisibility(View.GONE);

            return false;
        });

        video_view_preview.setOnClickListener(v -> {
            if (video_view_preview.isPlaying()) {
                btn_start_stop_video.setVisibility(View.VISIBLE);
                btn_start_stop_video.setImageResource(R.drawable.ic_play_arrow);
                video_view_preview.pause();
            }
        });


        btn_submit.setOnClickListener(v -> {
            if (isFormFill()) {
                if (is_editing_data) {
                    updateVideoClipData();
                } else
                    postVideoClip();
            }
        });

        btn_chose_video.setOnClickListener(v -> openGallery(galleryResultLauncher));

        btn_start_stop_video.setOnClickListener(v -> {
            if (is_video_attached) {
                if (video_view_preview.isPlaying()) {
                    btn_start_stop_video.setVisibility(View.VISIBLE);
                    btn_start_stop_video.setImageResource(R.drawable.ic_play_arrow);
                    video_view_preview.pause();
                } else {
                    btn_start_stop_video.setVisibility(View.INVISIBLE);
                    video_view_preview.start();
                }
            }
        });
    }

    private void initViews() {
        btn_submit = findViewById(R.id.toolbarTrans).findViewById(R.id.btn_submit);
        btn_start_stop_video = findViewById(R.id.btn_start_stop_video);
        switch_hide_show_clip = findViewById(R.id.switch_hide_show_clip);
        areas_indicative = findViewById(R.id.c_spinner_areas_indicative);
        department_indicative = findViewById(R.id.c_spinner_department_indicative);
        btn_chose_video = findViewById(R.id.btn_chose_video);
        et_clip_title = findViewById(R.id.cet_clip_title);
        et_description = findViewById(R.id.cet_description);
        video_view_preview = findViewById(R.id.video_view_preview);
        et_tags = findViewById(R.id.cet_tags);
        ll_progress_bar = findViewById(R.id.ll_progress_bar);
        ll_chose_video = findViewById(R.id.ll_chose_video);
        tv_layout_title = findViewById(R.id.tv_layout_title);
        card_view_video_preview = findViewById(R.id.card_view_video_preview);
    }


    /**
     * @param launcher to handle result that will come from trimming video
     */
    private void openGallery(ActivityResultLauncher<Intent> launcher) {

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        launcher.launch(intent);

    }

    /**
     * @param video_uri uri of video that will be trimmed
     * @param launcher  to handle result that will come from trimming video
     */
    private void lunchTrimVideoActivity(String video_uri, ActivityResultLauncher<Intent> launcher) {
//        TrimVideo.activity(video_uri)
//                .setTrimType(TrimType.MIN_MAX_DURATION)
//                .setMinToMax(10, 60)
//                .setLocal("ar")
//                .start(AddVideoByConsultantsActivity.this, launcher);
    }

    private ActivityResultLauncher<Intent> initGalleryResultLauncher(ActivityResultLauncher<Intent> trimActivityLauncher) {
        return registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getData() == null) return;

                    Intent i = result.getData();
                    Uri uri = i.getData();

                    String file_type = Util.getMimeType(getApplicationContext(), uri);
                    if (!file_type.contains("video")) {
                        Util.showToast(getString(R.string.not_a_video_file), AddVideoByConsultantsActivity.this);
                        return;
                    }

                    lunchTrimVideoActivity(uri.toString(), trimActivityLauncher);

                });
    }

    private ActivityResultLauncher<Intent> initTrimVideoActivity() {
        return registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK &&
                            result.getData() != null) {
                        Uri uri = Uri.parse("");
                        Log.d("TAG", "Trimmed path:: " + uri);

                        //running progress bar for compressing the video
                        ll_progress_bar.setVisibility(View.VISIBLE);
                        Util.disableInteraction(this);


                        ArrayList<Uri> uris = new ArrayList<>();
                        uris.add(uri);

                        VideoCompressor.start(
                                getApplicationContext(), // => This is required
                                uris, // => Source can be provided as content uris
                                false, // => isStreamable
                                null,
                                new AppSpecificStorageConfiguration(
                                        "compressed_video", null

                                ), // => the directory to save the compressed video(s)
                                new com.abedelazizshe.lightcompressorlibrary.config.Configuration(
                                        VideoQuality.MEDIUM,
                                        true,
                                        null,
                                        false,
                                        true,
                                        null,
                                        null
                                ),

                                new CompressionListener() {
                                    @Override
                                    public void onSuccess(int i, long l, @Nullable String s) {
                                        prepareVideoForUpload(Uri.parse(s), s);

                                        ll_progress_bar.setVisibility(View.GONE);
                                        Util.enableInteraction(AddVideoByConsultantsActivity.this);
                                    }

                                    @Override
                                    public void onStart(int i) {

                                    }

                                    @Override
                                    public void onFailure(int index, @NonNull String failureMessage) {
                                        // On Failure
                                        if (index == 0)
                                            prepareVideoForUpload(uri, uri.toString());

                                        ll_progress_bar.setVisibility(View.GONE);
                                        Util.enableInteraction(AddVideoByConsultantsActivity.this);
                                    }

                                    @Override
                                    public void onProgress(int index, float progressPercent) {
                                        // Update UI with progress value
                                        runOnUiThread(() -> Log.v("Compress progress: ", progressPercent + "%"));
                                        int progress = (int) progressPercent;
                                        ((TextView) ll_progress_bar.findViewById(R.id.pbText))
                                                .setText(String.format("جاري ضغط الفيديو: %s %%", progress));

                                    }

                                    @Override
                                    public void onCancelled(int index) {
                                        // On Cancelled
                                    }
                                }
                        );


                    } else {
//                        LogMessage.v("videoTrimResultLauncher data is null");
                    }
                });
    }

    boolean isVideoLengthBetweenLimits(Uri uri) {
        long duration = 100 * 1000;
        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(AddVideoByConsultantsActivity.this, uri);
            duration = Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));

            retriever.release();
        } catch (IOException e) {
            ToastUtils.longToast(com.vanillaplacepicker.R.string.something_went_worng);
        }
        return duration / 1000 <= MAX_TIME_AVAILABLE_IN_SECONDS;
    }

    void prepareVideoForUpload(Uri uri, String s){
        if (isVideoLengthBetweenLimits(uri)) {
            assert s != null;
            file = new File(s);
            video_view_preview.setVideoURI(uri);
            card_view_video_preview.setVisibility(View.VISIBLE);
            is_video_attached = true;
        } else {
            ToastUtils.longToast(getString(R.string.max_video_length));
            btn_start_stop_video.setVisibility(View.GONE);
        }
    }


    boolean isFormFill() {
        if (TextUtils.isEmpty(et_clip_title.getText().toString().trim()) ||
                TextUtils.isEmpty(et_description.getText().trim()) ||
                TextUtils.isEmpty(et_tags.getText().toString().trim())) {
            Util.showToast(getResources().getString(R.string.fields_required), this);
            return false;
        }
        if (areas_indicative.innerViews.spinner.getSelectedItem() == null || department_indicative.innerViews.spinner.getSelectedItem() == null) {
            Util.showToast(getResources().getString(R.string.areas_indicative_and_department_indicative_required), this);
            return false;
        }
        if ((!is_video_attached && !is_editing_data) || (file == null && !is_editing_data)) {
            Util.showToast(getResources().getString(R.string.attach_a_video), this);
            return false;
        }
        return true;
    }

    private void getVideoCategoriesFromAPI() {
        ll_progress_bar.setVisibility(View.VISIBLE);
        Util.disableInteraction(this);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<NewAPIsResponse<ArrayList<CategoryDataOfConsultantVideos>>> call = apiInterface.getVideoCategories("Bearer " + TokenHelper.getToken());
        call.enqueue(new Callback<NewAPIsResponse<ArrayList<CategoryDataOfConsultantVideos>>>() {
            @Override
            public void onResponse(@NonNull Call<NewAPIsResponse<ArrayList<CategoryDataOfConsultantVideos>>> call, @NonNull Response<NewAPIsResponse<ArrayList<CategoryDataOfConsultantVideos>>> response) {
                consultantVideosCategory = response.body();
                //setting categories array for spinners adapters
                setCategoryList();
                if (is_editing_data)
                    loadIndicativeSpinnersForEditingMode();

                ll_progress_bar.setVisibility(View.GONE);
                Util.enableInteraction(AddVideoByConsultantsActivity.this);
            }

            @Override
            public void onFailure(@NonNull Call<NewAPIsResponse<ArrayList<CategoryDataOfConsultantVideos>>> call, @NonNull Throwable t) {
                ToastUtils.longToast(getString(R.string.connection_lost));

                ll_progress_bar.setVisibility(View.GONE);
                Util.enableInteraction(AddVideoByConsultantsActivity.this);
            }
        });
    }

    private void setCategoryList() {
        if (consultantVideosCategory != null) {
            if (consultantVideosCategory.getStatus()) {
                //setting name of indicatives spinner
                areas_indicative_names = new ArrayList<>();
                areas_indicative_names.addAll(consultantVideosCategory.getData());

                //create array adapter for areas indicative
                arrayAdapter_areas_indicative = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, areas_indicative_names);
                arrayAdapter_areas_indicative.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                //setting adapter to the spinner
                areas_indicative.innerViews.spinner.setAdapter(arrayAdapter_areas_indicative);
                areas_indicative.innerViews.spinner.setOnItemSelectedListener(new AreasIndicativeSpinnerListener());
            }
        }
    }

    /**
     * @param index the index of 2D array depending on selected areas indicative
     */
    private void setDepartmentIndicativeAdapter(int index) {
        //setting names of department spinner
        department_indicative_names.clear();
        department_indicative_names.addAll(consultantVideosCategory.getData().get(index).getSubData());

        //create array adapter for departments indicative
        arrayAdapter_department_indicative = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, department_indicative_names);
        arrayAdapter_department_indicative.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //setting adapter to the spinner
        department_indicative.innerViews.spinner.setAdapter(arrayAdapter_department_indicative);
        department_indicative.innerViews.spinner.setOnItemSelectedListener(this);

    }

    public static MultipartBody.Part prepareFilePart(String partName, File file) {
        //   File file = new File(fileUri);
        String mimeType = URLConnection.guessContentTypeFromName(file.getName());
        Log.e("mimeType", mimeType);
        RequestBody requestFile = RequestBody.create(file, MediaType.parse(mimeType));
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

    private void postVideoClip() {

        ll_progress_bar.setVisibility(View.VISIBLE);
        Util.disableInteraction(this);

        final CategoryDataOfConsultantVideos catData = (CategoryDataOfConsultantVideos) areas_indicative.innerViews.spinner.getSelectedItem();
        final CategorySubDataOfConsultantVideos subCatData = (CategorySubDataOfConsultantVideos) department_indicative.innerViews.spinner.getSelectedItem();

        final MultipartBody.Part title = MultipartBody.Part.createFormData("Title", et_clip_title.getText().toString());
        final MultipartBody.Part categoryId = MultipartBody.Part.createFormData("CategoryId", catData.getId());
        final MultipartBody.Part departmentId = MultipartBody.Part.createFormData("DepartmentId", subCatData.getId());
        final MultipartBody.Part video = prepareFilePart("Video", file);
        final MultipartBody.Part description = MultipartBody.Part.createFormData("Description", et_description.getText());
        final MultipartBody.Part tag = MultipartBody.Part.createFormData("Tag", et_tags.getText().toString());

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<NewAPIsResponse<String>> call = apiInterface.uploadVideoClip("Bearer " + TokenHelper.getToken(), title, description, categoryId, departmentId, video, tag);
        call.enqueue(new Callback<NewAPIsResponse<String>>() {
            @Override
            public void onResponse(@NonNull Call<NewAPIsResponse<String>> call, @NonNull Response<NewAPIsResponse<String>> response) {
                assert response.body() != null;
                if (response.body().getStatus()) {
                    DialogUtil.showInfoDialogAndFinishActivity(AddVideoByConsultantsActivity.this, 1, "تم إضافة الفيديو", "إضافة");
                    Log.v("respone status msg: ", response.body().getMessage());
                } else {
                    Log.v("respone error: ", response.body().getMessage());
                }

                ll_progress_bar.setVisibility(View.GONE);
                Util.enableInteraction(AddVideoByConsultantsActivity.this);

            }

            @Override
            public void onFailure(@NonNull Call<NewAPIsResponse<String>> call, @NonNull Throwable t) {
                Util.showErrorToast("عذرا لم يتم رفع الفيديو", AddVideoByConsultantsActivity.this);
                Log.e("Error msg: ", t.getMessage());

                ll_progress_bar.setVisibility(View.GONE);
                Util.enableInteraction(AddVideoByConsultantsActivity.this);
            }
        });

    }

    private void updateVideoClipData() {

        if (video_id == null) return;

        ll_progress_bar.setVisibility(View.VISIBLE);

        final CategoryDataOfConsultantVideos catData = (CategoryDataOfConsultantVideos) areas_indicative.innerViews.spinner.getSelectedItem();
        final CategorySubDataOfConsultantVideos subCatData = (CategorySubDataOfConsultantVideos) department_indicative.innerViews.spinner.getSelectedItem();

        String cat_id = "0", department_id = "0";
        if (is_area_or_department_been_changed) {
            cat_id = catData.getId();
            department_id = subCatData.getId();
        }


        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<NewAPIsResponse<String>> call = apiInterface.updateVideoClipData("Bearer " + TokenHelper.getToken(), video_id, et_clip_title.getText().toString(),
                et_description.getText(), department_id, cat_id, et_tags.getText().toString(), !switch_hide_show_clip.isChecked());

        call.enqueue(new Callback<NewAPIsResponse<String>>() {
            @Override
            public void onResponse(@NonNull Call<NewAPIsResponse<String>> call, @NonNull Response<NewAPIsResponse<String>> response) {
                assert response.body() != null;
                if (response.body().getStatus()) {
                    DialogUtil.showInfoDialogAndFinishActivity(AddVideoByConsultantsActivity.this, 1, "تم تعديل الفيديو", "تعديل");

                    Log.v("respone status msg: ", response.body().getMessage());
                } else {
                    Util.showErrorToast("عذرا حدث خطأ!! لم يتم تعديل الفيديو", AddVideoByConsultantsActivity.this);
                    Log.v("respone error: ", response.body().getMessage());
                }

                ll_progress_bar.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(@NonNull Call<NewAPIsResponse<String>> call, @NonNull Throwable t) {
                Util.showErrorToast("عذرا حدث خطأ!! لم يتم تعديل الفيديو", AddVideoByConsultantsActivity.this);
                Log.e("Error msg: ", t.getMessage());

                ll_progress_bar.setVisibility(View.GONE);
            }
        });

    }

    void editingMode() {
        if (!is_editing_data)
            return;
        btn_submit.setText(getString(R.string.save));
        tv_layout_title.setText(getString(R.string.edit_clip));
        ll_chose_video.setVisibility(View.GONE);
        VideoDataOfConsultantVideos videoData = (VideoDataOfConsultantVideos) getIntent().getSerializableExtra("data");
        et_clip_title.setText(videoData.getTitle());
        et_description.setText(videoData.getDescription());
        et_tags.setText(videoData.getTag());
        switch_hide_show_clip.setChecked(!videoData.isHidden());
        switch_hide_show_clip.setText(switch_hide_show_clip.isChecked() ? getString(R.string.clip_is_visible_to_all) : getString(R.string.clip_is_invisible_to_all));

        video_id = videoData.getId();


        switch_hide_show_clip.setVisibility(View.VISIBLE);
        switch_hide_show_clip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                switch_hide_show_clip.setText(getString(R.string.clip_is_visible_to_all));
                switch_hide_show_clip.setTextColor(getColor(R.color.greenLight));
            } else {
                switch_hide_show_clip.setText(getString(R.string.clip_is_invisible_to_all));
                switch_hide_show_clip.setTextColor(getColor(R.color.red));
            }
        });
    }

    void loadIndicativeSpinnersForEditingMode() {
        VideoDataOfConsultantVideos videoData = (VideoDataOfConsultantVideos) getIntent().getSerializableExtra("data");
        int index_of_areas = getIndexOfCategory(areas_indicative_names, videoData.getCategory());
        int index_of_department = getIndexOfDepartment(areas_indicative_names.get(index_of_areas).getSubData(), videoData.getDepartment());
        areas_indicative.innerViews.spinner.setSelection(index_of_areas);
        department_indicative.innerViews.spinner.setSelection(index_of_department);

    }

    int getIndexOfCategory(ArrayList<CategoryDataOfConsultantVideos> cat_data, String cat_name) {
        for (int i = 0; i < cat_data.size(); i++) {
            if (cat_data.get(i).getName().compareTo(cat_name) == 0)
                return i;
        }
        return 0;
    }

    int getIndexOfDepartment(ArrayList<CategorySubDataOfConsultantVideos> sub_cat_data, String sub_cat_name) {
        for (int i = 0; i < sub_cat_data.size(); i++) {
            if (sub_cat_data.get(i).getName().compareTo(sub_cat_name) == 0)
                return i;
        }
        return 0;
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        is_area_or_department_been_changed = true;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    class AreasIndicativeSpinnerListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            setDepartmentIndicativeAdapter(position);
            is_area_or_department_been_changed = true;
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }


}
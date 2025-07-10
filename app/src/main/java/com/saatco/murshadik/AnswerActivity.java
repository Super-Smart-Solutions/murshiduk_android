package com.saatco.murshadik;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.carousel.CarouselLayoutManager;
import com.google.android.material.carousel.CarouselSnapHelper;
import com.google.android.material.carousel.FullScreenCarouselStrategy;
import com.saatco.murshadik.Helpers.ProfileHelper;
import com.saatco.murshadik.Helpers.TokenHelper;
import com.saatco.murshadik.Helpers.ToolbarHelper;
import com.saatco.murshadik.adapters.AnswerAdapter;
import com.saatco.murshadik.adapters.DesignV3.InfinityImageIndecatorAdapter;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.api.APIInterface;
import com.saatco.murshadik.api.response.BaseResponse;
import com.saatco.murshadik.api.response.QAVoteResponse;
import com.saatco.murshadik.api.response.QuestionDetailResponse;
import com.saatco.murshadik.databinding.ActivityAnswerBinding;
import com.saatco.murshadik.model.Attachment;
import com.saatco.murshadik.model.Question;
import com.saatco.murshadik.utils.Consts;
import com.saatco.murshadik.utils.KeyboardUtils;
import com.saatco.murshadik.utils.Util;

import java.io.File;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnswerActivity extends BaseActivity implements AnswerAdapter.OnSelectItemClickListener {

    RecyclerView recyclerView;
    ProgressBar progressBar;
    EditText etAnswer;
    TextView tvTitle;
    TextView tvTags;
    TextView tvDate;
    TextView tvCreatedBy;
    TextView tvDescription;
    TextView tvCategory;
    TextView tvRating;
    ImageView btnUp;
    ImageView btnDown;
    FrameLayout btnAttachment;
    LinearLayout filesLayout;
    Button btnSave;
    LinearLayout layoutRating;
    SpinKitView spinKitView;
    FrameLayout layoutEmpty;
    ImageView ivVerified;
    LinearLayout layoutTags;
    LinearLayout llProgressBar;

    private ArrayList<Question> answers = new ArrayList<>();


    ArrayList<File> fileUris = new ArrayList<>();

    Question question;

    AnswerAdapter answerAdapter;

    int questionId;

    View loader;
    private ActivityAnswerBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAnswerBinding.inflate(getLayoutInflater(), null, false);
        setContentView(binding.getRoot());

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        initViews();


        ToolbarHelper.setToolBar(this, "", findViewById(R.id.toolbarTrans));


        spinKitView.setVisibility(View.GONE);

        questionId = getIntent().getIntExtra("ID", 0);

        PrefUtil.writeBooleanValue(getApplicationContext(), "is_question_asked", false);

        //********************* check for profile complete ******************//
        if (!ProfileHelper.getAccount(getApplicationContext()).isProfileComplete()) {
            etAnswer.setVisibility(View.GONE);
            btnSave.setVisibility(View.GONE);
            Util.showErrorToast(getResources().getString(R.string.complete_profile_msg), this);
        } else {
            if (ProfileHelper.getAccount(getApplicationContext()).isConsultantUser() && !ProfileHelper.getAccount(getApplicationContext()).isApproved()) {
                etAnswer.setVisibility(View.GONE);
                btnSave.setVisibility(View.GONE);
                Util.showErrorToast(getResources().getString(R.string.complete_profile_msg), this);
            }
        }

        //get question api
        getQuestion();


        btnUp.setOnClickListener(view -> voteQuestion(1));

        btnDown.setOnClickListener(view -> voteQuestion(0));

        btnSave.setOnClickListener(view -> {

            if (!etAnswer.getText().toString().equals("")) {
                btnSave.setEnabled(false);
                postAnswer();
            } else
                Util.showToast(getResources().getString(R.string.enter_anwser), AnswerActivity.this);
        });

    }

    private void initViews() {
        recyclerView = binding.rvAnswers;
        progressBar = binding.progressBar;
        etAnswer = binding.etAnswer;
        tvTitle = binding.tvTitle;
        tvTags = binding.tvTags;
        tvDate = binding.tvDate;
        tvCreatedBy = binding.tvCreatedBy;
        tvDescription = binding.tvDescription;
        tvCategory = binding.tvCategory;
        tvRating = binding.tvRating;
        btnUp = binding.btnUp;
        btnDown = binding.btnDown;
        btnAttachment = binding.btnAttachment;
        filesLayout = binding.filesLayout;
        btnSave = binding.btnDone;
        layoutRating = binding.layoutRating;
        spinKitView = binding.spinKit;
        layoutEmpty = binding.btnAttachment;
        ivVerified = binding.ivVerified;
        layoutTags = binding.layoutTags;
        llProgressBar = binding.filesLayout;
        loader = binding.llProgressBar.getRoot();
    }

    private void getQuestion() {

        loader.setVisibility(View.VISIBLE);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<QuestionDetailResponse> call = apiInterface.getQuestion("Bearer " + TokenHelper.getToken(), questionId);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<QuestionDetailResponse> call, @NonNull Response<QuestionDetailResponse> response) {

                loader.setVisibility(View.GONE);

                try {

                    if (response.body() == null || response.body().getQuestion() == null) return;

                    layoutEmpty.setVisibility(View.GONE);

                    question = response.body().getQuestion();
                    tvTitle.setText(question.getTitle());
                    tvDescription.setText(question.getDescription());

                    String voteCount = "" + question.getVoteCount();
                    tvRating.setText(voteCount);
                    tvDate.setText(String.format(Locale.US, question.getDate()));
                    tvCreatedBy.setText(question.getCreatedBy());
                    tvCategory.setText(question.getCategoryId() == Consts.PLANTING ? getResources().getString(R.string.planting) : getResources().getString(R.string.disease));

                    if (question.isVerified())
                        ivVerified.setVisibility(View.VISIBLE);

                    setUpAnswers();
                    setSlider();
                    layoutTags.removeAllViews();

                    //************************** make tags ***************************//
//                        ChipCloud chipCloud = findViewById(R.id.chip_cloud);
//
//                        String[] tags = question.getTagList().toArray(new String[0]);
//
//                        new ChipCloud.Configure()
//                                .chipCloud(chipCloud)
//                                .selectTransitionMS(500)
//                                .deselectTransitionMS(250)
//                                .mode(ChipCloud.Mode.MULTI)
//                                .allCaps(false)
//                                .gravity(ChipCloud.Gravity.RIGHT)
//                                .textSize(getResources().getDimensionPixelSize(com.adroitandroid.chipcloud.R.dimen.default_textsize))
//                                .verticalSpacing(getResources().getDimensionPixelSize(com.adroitandroid.chipcloud.R.dimen.vertical_spacing))
//                                .minHorizontalSpacing(getResources().getDimensionPixelSize(com.adroitandroid.chipcloud.R.dimen.min_horizontal_spacing))
//                                .labels(tags)
//                                .build();

                    if (question.getUserId() == ProfileHelper.getAccount(getApplicationContext()).getId()) {
                        etAnswer.setVisibility(View.GONE);
                        btnSave.setVisibility(View.GONE);
//                            tvAnswerHeading.setVisibility(View.GONE);

                        btnDown.setEnabled(false);
                        btnUp.setEnabled(false);
                        btnUp.setImageTintList(ColorStateList.valueOf(getApplicationContext().getColor(R.color.light_gray)));
                        btnDown.setImageTintList(ColorStateList.valueOf(getApplicationContext().getColor(R.color.light_gray)));
                    } else {
                        btnDown.setEnabled(true);
                        btnUp.setEnabled(true);
                        btnUp.setImageTintList(ColorStateList.valueOf(getApplicationContext().getColor(R.color.themeColor)));
                        btnDown.setImageTintList(ColorStateList.valueOf(getApplicationContext().getColor(R.color.red)));
                    }


                } catch (Exception ignored) {
                }
            }

            @Override
            public void onFailure(@NonNull Call<QuestionDetailResponse> call, @NonNull Throwable t) {
                loader.setVisibility(View.GONE);
            }
        });

    }


    private void postAnswer() {
        llProgressBar.setVisibility(View.VISIBLE);

        ArrayList<MultipartBody.Part> files = new ArrayList<>();

        for (int i = 0; i < fileUris.size(); i++) {
            files.add(prepareFilePart("file_" + (i + 1), fileUris.get(i)));
        }

        final MultipartBody.Part title = MultipartBody.Part.createFormData("title", "");
        final MultipartBody.Part description = MultipartBody.Part.createFormData("description", etAnswer.getText().toString());
        final MultipartBody.Part q_id = MultipartBody.Part.createFormData("qid", String.valueOf(questionId));

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<BaseResponse> call = apiInterface.postAnswer("Bearer " + TokenHelper.getToken(), title, description, q_id, files);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(@NonNull Call<BaseResponse> call, @NonNull Response<BaseResponse> response) {

                llProgressBar.setVisibility(View.GONE);


                try {

                    etAnswer.setText("");
                    fileUris.clear();
                    filesLayout.removeAllViews();
                    btnSave.setEnabled(true);

                    etAnswer.setVisibility(View.GONE);
                    btnSave.setVisibility(View.GONE);
//                    tvAnswerHeading.setVisibility(View.GONE);

                    KeyboardUtils.hideKeyboard(etAnswer);
                    Util.showSuccessToast(getResources().getString(R.string.answer_success), AnswerActivity.this);

                    getQuestion();

                } catch (Exception ignored) {
                }
            }

            @Override
            public void onFailure(@NonNull Call<BaseResponse> call, @NonNull Throwable t) {
                llProgressBar.setVisibility(View.GONE);
            }
        });

    }

    public static MultipartBody.Part prepareFilePart(String partName, File file) {
        // File file = new File(fileUri);
        String mimeType = URLConnection.guessContentTypeFromName(file.getName());
        Log.e("mimeType", mimeType);
        RequestBody requestFile = RequestBody.create(file, MediaType.parse(mimeType));
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

    private void setUpAnswers() {
        if (question.getAnswers() != null) {
            answers = question.getAnswers();
            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            answerAdapter = new AnswerAdapter(answers, getApplicationContext(), this);
            recyclerView.setAdapter(answerAdapter);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
        }
    }

    private void setSlider() {

        if (question.getAttachments() != null) {
            ArrayList<String> imagesUrl = new ArrayList<>();

            for (Attachment attachment : question.getAttachments())
                imagesUrl.add(APIClient.imageUrl + attachment.getUrl());

            //********************* set slider ******************//
            binding.carouselRecyclerView.setAdapter(new InfinityImageIndecatorAdapter(getApplicationContext(), imagesUrl, imageUrl -> {
                Intent intent = new Intent(AnswerActivity.this, ViewImageActivity.class);
                intent.putExtra("URL", imageUrl);
                startActivity(intent);
            }));
            CarouselLayoutManager layoutManager = new CarouselLayoutManager(new FullScreenCarouselStrategy()); // , RecyclerView.HORIZONTAL
            binding.carouselRecyclerView.setLayoutManager(layoutManager);

            CarouselSnapHelper snapHelper = new CarouselSnapHelper();
            snapHelper.attachToRecyclerView(binding.carouselRecyclerView);

            ArrayList<ImageView> indicators = new ArrayList<>();
            for (int i = 0; i < imagesUrl.size(); i++) {
                ImageView imageView = new ImageView(getApplicationContext());
                if (i == 0)
                    imageView.setImageResource(R.drawable.active_dot);
                else
                    imageView.setImageResource(R.drawable.non_active_dot);
                binding.layoutCarouselIndicator.addView(imageView);
                indicators.add(imageView);
            }

            binding.carouselRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    // find out which item is current item position
                    if (newState != RecyclerView.SCROLL_STATE_IDLE) return;

                    int position = snapHelper.findTargetSnapPosition(layoutManager, 0, 0);
                    for (ImageView iv : indicators) {
                        if (indicators.indexOf(iv) == position)
                            iv.setImageResource(R.drawable.active_dot);
                        else
                            iv.setImageResource(R.drawable.non_active_dot);
                    }

                }

                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    Log.e("dx", dx + "");
                    Log.e("dy", dy + "");
                }
            });

        }
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));

        Configuration config = new Configuration(base.getResources().getConfiguration());
        config.fontScale = 1.2f;
        applyOverrideConfiguration(config);
    }

    private void voteQuestion(int vote) {

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<QAVoteResponse> call = apiInterface.questionVote("Bearer " + TokenHelper.getToken(), questionId, vote);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<QAVoteResponse> call, @NonNull Response<QAVoteResponse> response) {

                try {
                    if (response.body() == null) {
                        return;
                    }
                    String voteCount = "" + response.body().getCount();
                    tvRating.setText(voteCount);


                } catch (Exception ignored) {
                }
            }

            @Override
            public void onFailure(@NonNull Call<QAVoteResponse> call, @NonNull Throwable t) {
                loader.setVisibility(View.GONE);
            }
        });
    }

    private void voteAnswer(int vote, Question question, int position) {

        loader.setVisibility(View.VISIBLE);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<QAVoteResponse> call = apiInterface.answerVote("Bearer " + TokenHelper.getToken(), question.getId(), vote);
        call.enqueue(new Callback<QAVoteResponse>() {
            @Override
            public void onResponse(@NonNull Call<QAVoteResponse> call, @NonNull Response<QAVoteResponse> response) {

                loader.setVisibility(View.GONE);

                try {

                    if (response.body() == null) {
                        return;
                    }
                    answers.get(position).setVoteCount(response.body().getCount());
                    answerAdapter.notifyItemChanged(position);


                } catch (Exception ignored) {
                }
            }

            @Override
            public void onFailure(@NonNull Call<QAVoteResponse> call, @NonNull Throwable t) {
                loader.setVisibility(View.GONE);
            }
        });
    }


    @Override
    public void onUpClick(View view, int position, Question question) {
        voteAnswer(1, question, position);
    }

    @Override
    public void onDownClick(View view, int position, Question question) {
        voteAnswer(0, question, position);
    }

    @Override
    public void onImageClick(View view, int position, Attachment attachment) {
        Intent intent = new Intent(AnswerActivity.this, ViewImageActivity.class);
        intent.putExtra("URL", APIClient.imageUrl + attachment.getUrl());
        intent.putExtra("qa", true);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
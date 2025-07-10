package com.saatco.murshadik;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.video.VideoSize;
import com.google.android.material.chip.Chip;
import com.saatco.murshadik.Helpers.ProfileHelper;
import com.saatco.murshadik.Helpers.TokenHelper;
import com.saatco.murshadik.Helpers.ToolbarHelper;
import com.saatco.murshadik.adapters.CommentVideoClipAdapter;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.api.APIInterface;
import com.saatco.murshadik.model.User;
import com.saatco.murshadik.model.consultantvideos.NewAPIsResponse;
import com.saatco.murshadik.model.consultantvideos.CommentOfConsultantVideo;
import com.saatco.murshadik.model.consultantvideos.VideoDataOfConsultantVideos;
import com.saatco.murshadik.model.consultantvideos.VideoLikesOfConsultantVideos;
import com.saatco.murshadik.utils.KeyboardUtils;
import com.saatco.murshadik.utils.SimpleTarget;
import com.saatco.murshadik.utils.Util;
import com.saatco.murshadik.views.HeaderViewWithLikeButtonsAndText;

import java.util.ArrayList;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConsultantVideoClipsViewerActivity extends BaseActivity implements CommentVideoClipAdapter.OnReplayItemClickListener {

    HeaderViewWithLikeButtonsAndText hlt_comments_header;
    RecyclerView rv_comments;
    ArrayList<CommentOfConsultantVideo> commentVideoClips;
    ImageView fullscreenButton, iv_send_comment, iv_consultant_image;
    NestedScrollView nsv_data_content_layout, nsv_comment_layout;
    LinearLayout include_view_trans_bar_layout, ll_comment_layout;
    EditText et_write_comment;
    Chip chip_reply_comment;
    TextView tv_consultant_name, tv_tag, tv_description, tv_title;
    ImageButton btn_messaging_consultant;
    boolean did_like = false, did_dislike = false, is_reply_comment = false, is_first_call_of_get_likes_api = true, is_video_width_bigger = true;
    boolean is_background_removed_from_exo_player = false;
    final boolean[] fullscreen = {false};

    // creating a variable for exoplayer
    PlayerView playerView;
    ExoPlayer exoPlayer;
    // url of video which we are loading.

    String video_id, parent_comment_id;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultant_video_clips_viewer);

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        ToolbarHelper.setToolBarTrans(this, "", findViewById(R.id.toolbarTrans));

        //assign views
        assignViews();

        // disable the btn until getting consultant user that post this video
        btn_messaging_consultant.setEnabled(false);

        setViewsData((VideoDataOfConsultantVideos) getIntent().getSerializableExtra("data"));

        commentVideoClips = new ArrayList<>();

        setupLikeButtons();

        btn_messaging_consultant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ConsultantVideoClipsViewerActivity.this, ChatActivityNew.class);
                i.putExtra("USER", user);
                startActivity(i);
            }
        });


        //adding listener to follow the resize of text box
        final boolean[] is_resized = {false};// setting 50 for default
        et_write_comment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //resize the layout of comment depending on numbers of lines in the edit text view
                // check if lines is more than 3 or less than 1
                if (et_write_comment.getLineCount() > 4 && !is_resized[0]) {
                    nsv_comment_layout.getLayoutParams().height = nsv_comment_layout.getMeasuredHeight();
                    nsv_comment_layout.requestLayout();
                    is_resized[0] = true;
                } else if (et_write_comment.getLineCount() <= 4 && is_resized[0]) {
                    nsv_comment_layout.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    is_resized[0] = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        //allow full screen video view
        fullscreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fullscreen[0]) {
                    exitFullScreenMode();
                } else {
                    enterFullScreenMode();
                }
            }
        });


        //setup send comment btn
        iv_send_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_write_comment.getText().toString().trim().length() < 3) {
                    Util.showToast(getString(R.string.small_comment), ConsultantVideoClipsViewerActivity.this);
                    return;
                }
                postComment(et_write_comment.getText().toString(), parent_comment_id);
                et_write_comment.setText("");
                clearReplyView();
            }
        });

        chip_reply_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearReplyView();
            }
        });
        chip_reply_comment.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearReplyView();
            }
        });
    }

    /**
     * set reply view invisible and clear the data of replay
     */
    private void clearReplyView() {
        is_reply_comment = false;
        parent_comment_id = "";
        KeyboardUtils.hideKeyboard(ConsultantVideoClipsViewerActivity.this);
        chip_reply_comment.setVisibility(View.GONE);
    }

    private void setViewsData(VideoDataOfConsultantVideos videoData) {
        tv_consultant_name.setText(videoData.getConsultantName());
        tv_tag.setText(videoData.getTag());
        hlt_comments_header.setDateText(videoData.getCreatAt().split(" ")[0]);
        tv_description.setText(videoData.getDescription());
        tv_title.setText(videoData.getTitle());
        setExoPlayer(playerView, videoData.getLocation(), this);
        getUserById(videoData.getUserId());

        video_id = videoData.getId();

        getCommentsByVideoId(video_id);
        getLikesByVideoId(video_id);

        parent_comment_id = "";
    }

    private void assignViews() {
        hlt_comments_header = findViewById(R.id.hlt_comments_header);
        rv_comments = findViewById(R.id.rv_comments);
        playerView = findViewById(R.id.video_play_consultant_clip);
        nsv_data_content_layout = findViewById(R.id.nsv_data_content_layout);
        include_view_trans_bar_layout = findViewById(R.id.include_view_trans_bar_layout);
        fullscreenButton = playerView.findViewById(R.id.exo_fullscreen_icon);

        tv_consultant_name = findViewById(R.id.tv_consultant_name);
        tv_tag = findViewById(R.id.tv_tag);
        tv_description = findViewById(R.id.tv_description);
        tv_title = findViewById(R.id.tv_title);
        iv_send_comment = findViewById(R.id.iv_send_comment);
        nsv_comment_layout = findViewById(R.id.nsv_comment_layout);
        ll_comment_layout = findViewById(R.id.ll_comment_layout);
        et_write_comment = findViewById(R.id.et_write_comment);

        btn_messaging_consultant = findViewById(R.id.btn_messaging_consultant);
        iv_consultant_image = findViewById(R.id.iv_consultant_image);

        chip_reply_comment = findViewById(R.id.chip_reply_comment);
        chip_reply_comment.setChipIconVisible(false);
        chip_reply_comment.setCloseIconVisible(true);
    }

    @Override
    public void onBackPressed() {
        if (fullscreen[0]) {
            exitFullScreenMode();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        exoPlayer.setPlayWhenReady(false);
    }

    @Override
    public void onDestroy() {
        exoPlayer.release();
        super.onDestroy();
    }

    @SuppressLint("SourceLockedOrientationActivity")
    public void exitFullScreenMode() {
        fullscreenButton.setImageDrawable(ContextCompat.getDrawable(ConsultantVideoClipsViewerActivity.this, R.drawable.ic_fullscreen_open));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        if (getSupportActionBar() != null) {
            getSupportActionBar().show();
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) playerView.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        playerView.setLayoutParams(params);
        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);

        fullscreen[0] = false;

        //show other views
        nsv_data_content_layout.setVisibility(View.VISIBLE);
        ll_comment_layout.setVisibility(View.VISIBLE);
        include_view_trans_bar_layout.setVisibility(View.VISIBLE);

    }

    public void enterFullScreenMode() {
        //hide other views
        nsv_data_content_layout.setVisibility(View.GONE);
        ll_comment_layout.setVisibility(View.GONE);
        include_view_trans_bar_layout.setVisibility(View.GONE);

        fullscreenButton.setImageDrawable(ContextCompat.getDrawable(ConsultantVideoClipsViewerActivity.this, R.drawable.ic_fullscreen_close));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        if (is_video_width_bigger)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);


        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) playerView.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        playerView.setLayoutParams(params);
        fullscreen[0] = true;
    }

    void setupLikeButtons() {
        hlt_comments_header.innerViews.layoutDislikeBtn.setOnClickListener(v -> {
            updateDislikeBtnDrawable();
            if (!did_like && !did_dislike)
                postLike(0);
            else
                postLike(2);// 2 is dislike code
        });

        hlt_comments_header.innerViews.layoutLikeBtn.setOnClickListener(v -> {
            updateLikeBtnDrawable();
            if (!did_like && !did_dislike)
                postLike(0);
            else
                postLike(1);// 1 is like code

        });
    }

    void updateDislikeBtnDrawable() {
        did_dislike = !did_dislike;
        if (did_dislike) {
            hlt_comments_header.setDislikeActive();
            did_like = false;
        } else {
            hlt_comments_header.resetDislike();
        }
    }

    void updateLikeBtnDrawable() {
        did_like = !did_like;
        if (did_like) {
            hlt_comments_header.setLikeActive();
            did_dislike = false;
        } else {
            hlt_comments_header.resetLike();
        }
    }

    void setRecyclerViewAdapter(ArrayList<CommentOfConsultantVideo> commentVideoClips) {
        rv_comments.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        CommentVideoClipAdapter adapter = new CommentVideoClipAdapter(commentVideoClips, getApplicationContext(), this);
        rv_comments.setAdapter(adapter);
        rv_comments.setItemAnimator(new DefaultItemAnimator());
    }

    public void setExoPlayer(PlayerView epPlayerView, String url, Context context) {

        Uri videoUri = Uri.parse(url);

        exoPlayer = new ExoPlayer.Builder(context).build();
        epPlayerView.setPlayer(exoPlayer);
        epPlayerView.setControllerShowTimeoutMs(1500);
        ImageView image = new ImageView(context);

        MediaItem mediaItem = MediaItem.fromUri(videoUri);
        exoPlayer.clearMediaItems();
        exoPlayer.setMediaItem(mediaItem);
        exoPlayer.prepare();

//        epPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.isMemoryCacheable();
        Glide.with(context)
                .setDefaultRequestOptions(requestOptions)
                .load(url)
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        epPlayerView.setBackground(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });

        exoPlayer.addAnalyticsListener(new AnalyticsListener() {
            @Override
            public void onIsPlayingChanged(EventTime eventTime, boolean isPlaying) {
                AnalyticsListener.super.onIsPlayingChanged(eventTime, isPlaying);
                if (!is_background_removed_from_exo_player) {
                    epPlayerView.setBackground(AppCompatResources.getDrawable(ConsultantVideoClipsViewerActivity.this, R.color.black));
                    is_background_removed_from_exo_player = true;
                }
            }

            @Override
            public void onVideoSizeChanged(EventTime eventTime, VideoSize videoSize) {
                AnalyticsListener.super.onVideoSizeChanged(eventTime, videoSize);
                if (videoSize.height > videoSize.width) {
                    is_video_width_bigger = false;
                }
            }
        });

    }


    private void setLikesData(VideoLikesOfConsultantVideos likesOfConsultantVideos) {
        //getting only the first 3 numbers
        int like_counts = likesOfConsultantVideos.getLikeCount();
        int dislike_counts = likesOfConsultantVideos.getDislikeCount();

        String like_counts_char = "";
        String dislike_counts_char = "";

        if (likesOfConsultantVideos.getLikeCount() > 1000) {
            like_counts_char = "K";
            like_counts /= 1000;
            if (likesOfConsultantVideos.getLikeCount() > 1000000) {
                like_counts_char = "M";
                like_counts /= 1000;
            }
        }

        if (likesOfConsultantVideos.getDislikeCount() > 1000) {
            dislike_counts_char = "K";
            dislike_counts /= 1000;
            if (likesOfConsultantVideos.getDislikeCount() > 1000000) {
                dislike_counts_char = "M";
                dislike_counts /= 1000;
            }
        }

        hlt_comments_header.setLikesNum(like_counts + like_counts_char);
        hlt_comments_header.setDislikesNum(dislike_counts + dislike_counts_char);

        //showing the like if user had been set like before
        if (is_first_call_of_get_likes_api) {
            if (likesOfConsultantVideos.getLikeStatus() == 1)
                updateLikeBtnDrawable();
            if (likesOfConsultantVideos.getLikeStatus() == 2)
                updateDislikeBtnDrawable();
            is_first_call_of_get_likes_api = false;
        }
    }

    private void getUserById(String userId) {

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<User> call = apiInterface.getUserBy("Bearer " + TokenHelper.getToken(), userId);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                user = response.body();
                if (user != null)
                    if (ProfileHelper.getAccount(getApplicationContext()).getId() == user.getId())
                        btn_messaging_consultant.setVisibility(View.GONE);
                    else
                        btn_messaging_consultant.setEnabled(true);
                setUserImage();

            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Log.e("Error in get user by id method:", t.getMessage());

            }
        });
    }

    private void setUserImage() {
        if (user.getPhotoUrl() == null || user.getPhotoUrl().isEmpty()) return;

        String url = APIClient.baseUrl + user.getPhotoUrl();
        RequestOptions requestOptions = new RequestOptions().override(200, 200).circleCrop();
        Glide.with(getApplicationContext())
                .asBitmap()
                .load(url)
                .apply(requestOptions)
                .into(iv_consultant_image);
    }

    private void postComment(String comment, String parent_comment_id) {


        if (video_id == null) return;

        final MultipartBody.Part comment_part = MultipartBody.Part.createFormData("Comment", comment);
        final MultipartBody.Part parent_comment_id_part = MultipartBody.Part.createFormData("CommentId", parent_comment_id);
        final MultipartBody.Part video_id_part = MultipartBody.Part.createFormData("VideoId", video_id);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<NewAPIsResponse<String>> call = apiInterface.postComment("Bearer " + TokenHelper.getToken(), video_id_part, comment_part, parent_comment_id_part);
        call.enqueue(new Callback<NewAPIsResponse<String>>() {
            @Override
            public void onResponse(@NonNull Call<NewAPIsResponse<String>> call, @NonNull Response<NewAPIsResponse<String>> response) {
                if (response.body() == null) return;

                if (response.body().getStatus()) {
                    getCommentsByVideoId(video_id);

                }

            }

            @Override
            public void onFailure(@NonNull Call<NewAPIsResponse<String>> call, @NonNull Throwable t) {
                Log.e("Error in post comment method:", t.getMessage());

            }
        });
    }

    /**
     * @param likeStatus: accept only 0 if user removed his like/dislike, 1 if user liked the video, or 2 if user didn't like the video
     */
    private void postLike(int likeStatus) {


        if (video_id == null) return;

        final MultipartBody.Part like_status_part = MultipartBody.Part.createFormData("LikeStatus", likeStatus + "");
        final MultipartBody.Part video_id_part = MultipartBody.Part.createFormData("VideoId", video_id);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<NewAPIsResponse<String>> call = apiInterface.postLike("Bearer " + TokenHelper.getToken(), video_id_part, like_status_part);
        call.enqueue(new Callback<NewAPIsResponse<String>>() {
            @Override
            public void onResponse(@NonNull Call<NewAPIsResponse<String>> call, @NonNull Response<NewAPIsResponse<String>> response) {
                if (response.body() == null) return;

                if (response.body().getStatus()) {
                    getLikesByVideoId(video_id);
                }

            }

            @Override
            public void onFailure(@NonNull Call<NewAPIsResponse<String>> call, @NonNull Throwable t) {
                Log.e("Error in post comment method:", t.getMessage());

            }
        });
    }

    private void getCommentsByVideoId(String video_id) {

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<NewAPIsResponse<ArrayList<CommentOfConsultantVideo>>> call = apiInterface.getCommentsOfVideo("Bearer " + TokenHelper.getToken(), video_id);
        call.enqueue(new Callback<NewAPIsResponse<ArrayList<CommentOfConsultantVideo>>>() {
            @Override
            public void onResponse(@NonNull Call<NewAPIsResponse<ArrayList<CommentOfConsultantVideo>>> call, @NonNull Response<NewAPIsResponse<ArrayList<CommentOfConsultantVideo>>> response) {
                if (response.body() == null) return;

                commentVideoClips = response.body().getData();
                if (commentVideoClips.isEmpty()) {
                    //TODO no comments

                    return;
                }

                setRecyclerViewAdapter(commentVideoClips);

            }

            @Override
            public void onFailure(@NonNull Call<NewAPIsResponse<ArrayList<CommentOfConsultantVideo>>> call, @NonNull Throwable t) {
                Log.e("Error in get comments by video id method:", t.getMessage());

            }
        });
    }

    private void getLikesByVideoId(String video_id) {

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<NewAPIsResponse<VideoLikesOfConsultantVideos>> call = apiInterface.getLikesOfVideo("Bearer " + TokenHelper.getToken(), video_id);
        call.enqueue(new Callback<NewAPIsResponse<VideoLikesOfConsultantVideos>>() {
            @Override
            public void onResponse(@NonNull Call<NewAPIsResponse<VideoLikesOfConsultantVideos>> call, @NonNull Response<NewAPIsResponse<VideoLikesOfConsultantVideos>> response) {
                if (response.body() == null) return;

                VideoLikesOfConsultantVideos likesOfConsultantVideos = response.body().getData();
                if (likesOfConsultantVideos == null) {
                    //TODO no comments
                    return;
                }

                setLikesData(likesOfConsultantVideos);

            }

            @Override
            public void onFailure(@NonNull Call<NewAPIsResponse<VideoLikesOfConsultantVideos>> call, @NonNull Throwable t) {
                Log.e("Error in get comments by video id method:", t.getMessage());

            }
        });
    }

    @Override
    public void onReplayItemClick(View view, int position, CommentOfConsultantVideo item) {
        is_reply_comment = true;
        parent_comment_id = item.getId() + "";

        chip_reply_comment.setVisibility(View.VISIBLE);
        chip_reply_comment.setText(Util.makeBoldAndNormalText(item.getUser() + "", getString(R.string.reply_on), false));

        et_write_comment.requestFocus();
        KeyboardUtils.showKeyboard(et_write_comment);

    }
}
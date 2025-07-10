package com.saatco.murshadik;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerUtils;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.saatco.murshadik.Helpers.ToolbarHelper;
import com.saatco.murshadik.databinding.ActivityVideoViewBinding;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VideoViewActivity extends BaseActivity {

    String path;

    // creating a variable for exoplayer
    StyledPlayerView playerView;
    YouTubePlayerView youTubePlayer;

    ExoPlayer exoPlayer;

    ActivityVideoViewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVideoViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initViews();

        ToolbarHelper.setToolBar(this, "", findViewById(R.id.toolbarTrans));

        ImageView iv = findViewById(R.id.btn_back);
        iv.setImageTintList(ColorStateList.valueOf(getColor(R.color.white)));

        path = getIntent().getStringExtra("path");
        boolean isYoutube = getIntent().getBooleanExtra("is_youtube", false);

        Log.v("PATH", path);

        if (isYoutube) {
            youTubePlayer.setVisibility(View.VISIBLE);
            youTubePlayer.enterFullScreen();
            getLifecycle().addObserver(youTubePlayer);

            youTubePlayer.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                @Override
                public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                    YouTubePlayerUtils.loadOrCueVideo(
                            youTubePlayer, getLifecycle(), getYouTubeId(path), 0f
                    );
                }

                @Override
                public void onError(@NotNull YouTubePlayer youTubePlayer, @NotNull PlayerConstants.PlayerError error) {
                    super.onError(youTubePlayer, error);

                    Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + "id"));
                    Intent webIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(path));
                    try {
                        startActivity(appIntent);
                    } catch (ActivityNotFoundException ex) {
                        startActivity(webIntent);
                    }
                }
            });

        } else {
            setExoPlayer(playerView, path, this);
        }

      /*  StorageReference videoRef = FirebaseStorage.getInstance().getReference().child("media/"+ path +".mp4");

        videoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                andExoPlayerView.setSource(uri.toString());

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        });*/

    }

    private void initViews() {
        playerView = binding.andExoPlayerView;
        youTubePlayer = binding.youtubePlayerView;
    }

    private String getYouTubeId(String youTubeUrl) {
        String pattern = "(?<=youtu.be/|watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(youTubeUrl);
        if (matcher.find()) {
            return matcher.group();
        } else {
            return "error";
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (exoPlayer != null)
            exoPlayer.release();
        if (youTubePlayer != null)
            youTubePlayer.release();
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));

        Configuration config = new Configuration(base.getResources().getConfiguration());
        config.fontScale = 1.2f;
        applyOverrideConfiguration(config);
    }


    public void setExoPlayer(StyledPlayerView epPlayerView, String url, Context context) {

        Uri videoUri = Uri.parse(url);

        exoPlayer = new ExoPlayer.Builder(context).build();
        epPlayerView.setPlayer(exoPlayer);
        epPlayerView.setControllerShowTimeoutMs(1500);

        MediaItem mediaItem = MediaItem.fromUri(videoUri);
        exoPlayer.clearMediaItems();
        exoPlayer.setMediaItem(mediaItem);
        exoPlayer.prepare();
        exoPlayer.setPlayWhenReady(true);

    }
}

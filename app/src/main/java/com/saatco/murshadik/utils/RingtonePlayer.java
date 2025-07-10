package com.saatco.murshadik.utils;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.util.Log;

import androidx.annotation.NonNull;

import com.saatco.murshadik.R;

import java.io.IOException;

/**
 * QuickBlox team
 */
public class RingtonePlayer {

    private static final String TAG = RingtonePlayer.class.getSimpleName();
    private MediaPlayer mediaPlayer, mediaPlayerDefault;
    private Context context;
    private boolean isPlaying = false;

    private Vibrator vibrator;

    public RingtonePlayer(@NonNull Context context, int resource) {
        this.context = context;
        Uri beepUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + resource);
        mediaPlayer = new MediaPlayer();
        AudioAttributes.Builder audioAttributesBuilder = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION_SIGNALLING);

        mediaPlayer.setAudioAttributes(audioAttributesBuilder.build());

        try {
            mediaPlayer.setDataSource(context, beepUri);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public RingtonePlayer(Context context) {
        this.context = context;
        Uri notification = getRingtoneSystemDefault();
        if (notification != null) {
            mediaPlayer = new MediaPlayer();

            AudioAttributes.Builder audioAttributesBuilder = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE);

            mediaPlayer.setAudioAttributes(audioAttributesBuilder.build());

            try {
                mediaPlayer.setDataSource(context, notification);
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void playDefault(Context context) {
        this.context = context;
        if (mediaPlayerDefault != null) return;

        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        am.setSpeakerphoneOn(true);
        Uri beepUri = getRingtoneSystemDefault();
        mediaPlayerDefault = new MediaPlayer();

        Log.v("Ringtone: ", "Play default init");

        try {
            mediaPlayerDefault.setDataSource(context, beepUri);
            mediaPlayerDefault.setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
            );
            mediaPlayerDefault.prepare();
            mediaPlayerDefault.start();
            Log.v("Ringtone: ", "Play default Started");
            isPlaying = true;
        } catch (IOException e) {
            e.printStackTrace();
            Log.v("Ringtone: ", "Play default Error");
        }
    }

    public void playVibrator(Context context) {
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        // Start without a delay
        // Vibrate for 100 milliseconds
        // Sleep for 1000 milliseconds
        long[] pattern = {0, 100, 1000};

        // The '0' here means to repeat indefinitely
        // '0' is actually the index at which the pattern keeps repeating from (the start)
        // To repeat the pattern from any other point, you could increase the index, e.g. '1'
        vibrator.vibrate(pattern, 0);
    }

    public void stopVibrator() {
        if (vibrator == null) return;
        vibrator.cancel();
        vibrator = null;
    }


    private Uri getRingtoneSystemDefault() {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);

        if (notification == null) {
            // notification is null, using backup
            notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            // I can't see this ever being null (as always have a default notification)
            // but just encase
            if (notification == null) {
                // notification backup is null, using 2nd backup
                notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            }
        }
        return notification;
    }

    public void play(boolean looping) {
        Log.i(TAG, "play");
        if (mediaPlayer == null) {
            Log.i(TAG, "mediaPlayer isn't created ");
            return;
        }
        mediaPlayer.setLooping(looping);
        mediaPlayer.start();
    }

    public synchronized void stop() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
                isPlaying = false;
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public boolean isPlaying() {
        return isPlaying;
    }


    public synchronized void stopDefault() {
        if (mediaPlayerDefault != null) {
            try {
                AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                am.setSpeakerphoneOn(false);
                mediaPlayerDefault.stop();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            mediaPlayerDefault.release();
            mediaPlayerDefault = null;
        }
    }
}
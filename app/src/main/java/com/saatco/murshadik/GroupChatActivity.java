package com.saatco.murshadik;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
//import omrecorder.AudioChunk;
//import omrecorder.AudioRecordConfig;
//import omrecorder.OmRecorder;
//import omrecorder.PullTransport;
//import omrecorder.PullableSource;
//import omrecorder.Recorder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioFormat;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.devlomi.record_view.OnBasketAnimationEnd;
import com.devlomi.record_view.OnRecordClickListener;
import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.snackbar.Snackbar;

import com.jaiselrahman.filepicker.activity.FilePickerActivity;
import com.jaiselrahman.filepicker.config.Configurations;
import com.jaiselrahman.filepicker.model.MediaFile;
import com.saatco.murshadik.Helpers.KeyboardHelper;
import com.saatco.murshadik.Helpers.PermissionsHelper;
import com.saatco.murshadik.Helpers.ProfileHelper;
import com.saatco.murshadik.Helpers.TokenHelper;
import com.saatco.murshadik.adapters.MessageAdapter;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.api.APIInterface;
import com.saatco.murshadik.api.response.GroupMessageResponse;
import com.saatco.murshadik.constants.MessageType;
import com.saatco.murshadik.databinding.ActivityGroupChatBinding;
import com.saatco.murshadik.db.StorageHelper;
import com.saatco.murshadik.model.GroupChat;
import com.saatco.murshadik.model.Message;
import com.saatco.murshadik.model.Question;
import com.saatco.murshadik.model.User;
import com.saatco.murshadik.utils.Consts;
import com.saatco.murshadik.utils.DirManager;
import com.saatco.murshadik.utils.FileUtils;
import com.saatco.murshadik.utils.PermissionsChecker;
import com.saatco.murshadik.utils.RealPathUtil;
import com.saatco.murshadik.utils.StorageUtil;
import com.saatco.murshadik.utils.Util;
import com.saatco.murshadik.views.AnimButton;
import com.saatco.murshadik.views.AttachmentView;
import com.vanillaplacepicker.data.VanillaAddress;
import com.vanillaplacepicker.utils.KeyUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;
import java.util.Timer;

import static com.adevinta.leku.LocationPickerActivityKt.LATITUDE;
import static com.adevinta.leku.LocationPickerActivityKt.LONGITUDE;

public class GroupChatActivity extends BaseActivity implements LocationListener, MessageAdapter.OnMessageItemClickListener {

    private static final int CAMERA_REQUEST = 4659;
    private static final int PICK_GALLERY_REQUEST = 4815;

    private final int MY_PERMISSIONS_RECORD_AUDIO = 1;
    private static final int REQUEST_TAKE_GALLERY_VIDEO = 4981;
    private static final int REQUEST_FILE_OPEN = 975;

    private static final int MAP_BUTTON_REQUEST_CODE = 909;

    //start voice record after 575ms until the sound effect finishes
    private static final int RECORD_START_AUDIO_LENGTH = 575;

    private RecyclerView recyclerView;
    private ImageView imgAttachment, cameraBtn, emojiBtn, detailButton;
    private LinearLayout typingLayout;
    public EmojiconEditText etMessage;
    private RecordView recordView;
    private AnimButton recordButton;
    private AttachmentView attachmentView;
    private ConstraintLayout typingLayoutContainer, mainContainer;
    private MessageAdapter messageAdapter;

    View rootView;

    EmojIconActions emojIcon;


    ArrayList<com.saatco.murshadik.model.Message> messageList = new ArrayList<>();
    LinearLayoutManager linearLayoutManager;

    boolean wasInTypingMode = false;
    boolean typingStarted = false;


    User user = null;

//    Recorder recorder;
    File recordFile;

    TextView tvName;
    ImageView back;

    //location
    protected double latitude, longitude = 0;

    ProgressDialog progressDialog;

    Uri mMakePhotoUri;


    private PermissionsChecker checker;

    Timer t;

    private final ArrayList<MediaFile> mediaFiles = new ArrayList<>();


    private MediaPlayer mediaPlayer;
    private final Handler seekHandler = new Handler();

    LocationManager locationManager;

    // private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location currentLocation;

    public static final int SENT_VIDEO = 3;
    public static final int SENT_IMAGE = 4;
    public static final int SENT_AUDIO = 2;
    public static final int SENT_LOCATION = 6;
    public static final int SENT_FILE = 5;

    File imageFile;
    String pdfFileName;

    GroupChat groupChat;

    private static final int CAPTURE_MEDIA = 368;

    public static final String TEMP_FILE = "temp.";

    int pageNo = 1;
    boolean isLoading = false;

    View loader;

    ArrayList<User> members = new ArrayList<>();

    ActivityGroupChatBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupChatBinding.inflate(getLayoutInflater(), null, false);
        setContentView(binding.getRoot());

        tvName = binding.tvTitle;
        back = binding.imgBack;
        loader = binding.llProgressBar.getRoot();

        progressDialog = new ProgressDialog(GroupChatActivity.this);
        progressDialog.setMessage("Uploading ...");
        progressDialog.setCancelable(false);




        groupChat = (GroupChat) getIntent().getSerializableExtra("GROUP");

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        checker = new PermissionsChecker(getApplicationContext());


        init();
        setUpInitial();
        setAdapter();
        getMessagesFromHistory();
        initScrollListener();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        recordView.setCancelBounds(0);

        recordButton.setRecordView(recordView);

        recordButton.setOnRecordClickListener(new OnRecordClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {
                hideOrShowRecord(false);
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                    handleRecord();
                } else {
                    ActivityCompat.requestPermissions(GroupChatActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, 222);
                }
            }

            @Override
            public void onCancel() {
                stopRecord(true, -1);
            }

            @Override
            public void onFinish(long recordTime, boolean limitReached) {
                hideOrShowRecord(true);
                stopRecord(false, recordTime);
                requestEditTextFocus();
            }

            @Override
            public void onLessThanSecond() {
                hideOrShowRecord(true);
                stopRecord(true, -1);
                requestEditTextFocus();
            }
        });

        recordView.setOnBasketAnimationEndListener(new OnBasketAnimationEnd() {
            @Override
            public void onAnimationEnd() {
                hideOrShowRecord(true);
                requestEditTextFocus();
            }
        });


        //onSendButton Click in keyboard
        etMessage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    // sendMessage(etMessage.getText().toString());
                    sendMessage(1, "");
                    return true;
                }
                return false;
            }
        });


        prefixEmojicon();

        etMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emojIcon.closeEmojIcon();
                if (attachmentView.isShowing())
                    attachmentView.hide(imgAttachment);

            }
        });

        imgAttachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                attachmentView.reveal(view);
                KeyboardHelper.hideSoftKeyboard(GroupChatActivity.this, etMessage);

            }
        });


        attachmentView.setOnAttachmentClick(new AttachmentView.AttachmentClickListener() {
            @Override
            public void OnClick(int id) {
                if (id == R.id.attachment_gallery) {
                    pickImages();
                } else if (id == R.id.attachment_camera) {
                    startCamera();
                } else if (id == R.id.attachment_document) {
                    pickGallery();
                } else if (id == R.id.attachment_location) {
                    pickLocation();
                } else if (id == R.id.attachment_contact) {
                    uploadFile();
                }
            }
        });

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendMessage(1, "");
            }
        });

        emojiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emojIcon.ShowEmojIcon();
            }
        });

        etMessage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                wasInTypingMode = !hasFocus;
            }
        });


    }

    private void initScrollListener() {

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (isLoading == false) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == messageList.size() - 1) {
                        //bottom of list!
                        getMessagesFromHistory();
                    }
                }

            }
        });
    }

    private void getMessagesFromHistory() {

        loader.setVisibility(View.VISIBLE);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<GroupMessageResponse> call = apiInterface.getGroupMessages("Bearer " + TokenHelper.getToken(), ProfileHelper.getAccount(getApplicationContext()).getRegionId(), 20, pageNo, true);
        call.enqueue(new Callback<GroupMessageResponse>() {
            @Override
            public void onResponse(Call<GroupMessageResponse> call, Response<GroupMessageResponse> response) {
                try {

                    loader.setVisibility(View.GONE);

                    if (response.body() != null) {


                        ArrayList<Message> list = response.body().getData().getGroupMessages();

                        members = response.body().getData().getConsultants();

                        for (Message message : list) {
                            message.setCreatedAt(Util.getDateFromString(message.getDate()).getTime());
                            messageList.add(message);
                        }

                        pageNo = pageNo + 1;
                        isLoading = false;

                        messageAdapter.members = members == null ? new ArrayList<>() : members;
                        recyclerView.getRecycledViewPool().clear();
                        messageAdapter.notifyDataSetChanged();

                        scrollToLast();
                    }


                } catch (Exception ex) {
                }
            }

            @Override
            public void onFailure(Call<GroupMessageResponse> call, Throwable t) {

                loader.setVisibility(View.GONE);
            }
        });


    }

    private void startCurrentLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500,
                500, mLocationListener);

    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {

            StorageHelper.saveLocation(location.getLatitude(), location.getLongitude());

        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }
    };

    private final LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (locationResult.getLastLocation() == null) {
                return;
            } else {
                Log.d("ccc ", "in location callback " + locationResult.getLastLocation().toString());
            }

            currentLocation = locationResult.getLastLocation();
            latitude = currentLocation.getLatitude();
            longitude = currentLocation.getLongitude();

            StorageHelper.saveLocation(currentLocation.getLatitude(), currentLocation.getLongitude());

        }
    };

    private void pickGallery() {

        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 60);
        takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Environment.getExternalStorageDirectory().getPath() + "videocapture_example.mp4");

        startActivityForResult(takeVideoIntent, REQUEST_TAKE_GALLERY_VIDEO);
    }

    private void setUpInitial() {

    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status)
            return true;
        else {
            if (googleApiAvailability.isUserResolvableError(status))
                Toast.makeText(this, "Please Install google play services to use this application", Toast.LENGTH_LONG).show();
        }
        return false;
    }


    private void startPermissionsActivity(boolean checkOnlyAudio) {
        PermissionsActivity.startActivity(this, checkOnlyAudio, Consts.PERMISSIONS);
    }



    //init views
    private void init() {

        recyclerView = findViewById(R.id.recycler_chat);
        rootView = findViewById(R.id.root_view);
        emojiBtn = findViewById(R.id.emoji_btn);
        etMessage = findViewById(R.id.et_message);
        imgAttachment = findViewById(R.id.img_attachment);
        cameraBtn = findViewById(R.id.ivSend);
        recordView = findViewById(R.id.record_view);
        recordButton = findViewById(R.id.record_button);
        typingLayout = findViewById(R.id.typing_layout);
        detailButton = findViewById(R.id.detailButton);

        emojIcon = new EmojIconActions(getApplicationContext(), rootView, etMessage, emojiBtn, "#495C66", "#DCE1E2", "#E6EBEF");

        attachmentView = findViewById(R.id.attachment_view);
        typingLayoutContainer = findViewById(R.id.typing_layout_container);

        tvName.setText(ProfileHelper.getAccount(getApplicationContext()).getLocation());

        if (!ProfileHelper.getAccount(getApplicationContext()).isCanPost()) {
            typingLayoutContainer.setVisibility(View.GONE);
            Snackbar.make(rootView, "يمكن للمسؤول فقط النشر", Snackbar.LENGTH_INDEFINITE)
                    .setAction("رفض", v -> {
                    })
                    .show();
        }

        detailButton.setOnClickListener(view -> {

            if (members.size() > 0) {
                Intent intent = new Intent(GroupChatActivity.this, GroupInfoActivity.class);
                intent.putExtra("MEMBERS", members);
                intent.putExtra("NAME", tvName.getText().toString());
                startActivity(intent);
            }
        });
    }

    // hide/show typingLayout or recordLayout
    private void hideOrShowRecord(boolean hideRecord) {
        if (hideRecord) {
            recordView.setVisibility(View.GONE);
            typingLayout.setVisibility(View.VISIBLE);
        } else {
            recordView.setVisibility(View.VISIBLE);
            typingLayout.setVisibility(View.GONE);
        }
    }

    //set the cursor on the EditText after finish recording
    private void requestEditTextFocus() {
        if (wasInTypingMode) {
            new Handler().postDelayed(() -> etMessage.requestFocus(), 100);

        }
    }

    //pick image
    private void pickImages() {
        Intent galleryIntent = new Intent().setAction(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, PICK_GALLERY_REQUEST);
    }

    //pick location
    private void pickLocation() {

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            //sendLocation();
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                sendLocation();
            } else {
                showGPSDisabledAlertToUser();
            }

        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 303);
        }
    }

    private void sendLocation() {

        if (longitude != 0 && longitude != 0) {
            Log.v("BSM", this.latitude + "," + this.longitude);
            sendMessage(SENT_LOCATION, this.latitude + "," + this.longitude);
        }
    }

    //startCamera
    private void startCamera() {

        //stopped by amin for enhance in future
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
//            Intent intent = new Intent(GroupChatActivity.this, CameraActivity.class);
//            startActivityForResult(intent,Consts.CAMERA_REQUEST);
//
//        }else{
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},4);
//        }

    }

    private void uploadFile() {

        if (PermissionsHelper.isMediaImagePermissionGranted(this)) {

            Intent intent = new Intent(GroupChatActivity.this, FilePickerActivity.class);
            intent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                    .setCheckPermission(true)
                    // .setSelectedMediaFiles(mediaFiles)
                    .setShowFiles(true)
                    .setShowImages(false)
                    .setShowVideos(false)
                    .setMaxSelection(1)
                    .setSingleChoiceMode(true)
                    .setSuffixes("pdf")
                    .setRootPath(Environment.getExternalStorageDirectory().getPath() + "/Download")
                    .build());
            startActivityForResult(intent, REQUEST_FILE_OPEN);


        } 

    }


    @Override
    protected void onStop() {
        super.onStop();

        locationManager.removeUpdates(mLocationListener);
    }


    private void prefixEmojicon() {
        emojiBtn.getViewTreeObserver().
                addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        //free up resources!
                        emojiBtn.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        //Prefix for Bug! in Library
                        emojIcon.ShowEmojIcon();


                    }
                });

        if (t != null) {
            t.cancel();
            t = null;
        }
    }

    private void changeSendButtonState(boolean setTyping) {
        if (setTyping) {
            recordButton.goToState(AnimButton.TYPING_STATE);
            recordButton.setListenForRecord(false);
            typingStarted = true;

        } else {
            recordButton.goToState(AnimButton.RECORDING_STATE);
            recordButton.setListenForRecord(true);
            typingStarted = false;
        }
    }


    //stop record
    private void stopRecord(boolean isCancelled, long recordTime) {
        try {
//            if (recorder != null)
//                recorder.stopRecording();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //if it's cancelled (the user swiped to cancel) then delete the recordFile
        if (isCancelled) {
            if (recordFile != null)
                recordFile.delete();
        } else {

            if (recordFile != null) {
                //otherwise get the recordTime and convert it to Readable String and send the message
                String timerStr = Util.milliSecondsToTimer(recordTime);
                String filePath = recordFile.getPath();
                sendVoiceMessage(recordFile, timerStr);
            }
        }

    }

//    private PullableSource getMic() {
//        return new PullableSource.AutomaticGainControl(
//                new PullableSource.Default(
//                        new AudioRecordConfig.Default(
//                                MediaRecorder.AudioSource.MIC, AudioFormat.ENCODING_PCM_16BIT,
//                                AudioFormat.CHANNEL_IN_MONO, 44100
//                        )
//                )
//        );
//    }

    //start recording voice message
    private void handleRecord() {

        if (ContextCompat.checkSelfPermission(GroupChatActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(GroupChatActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 844);
        } else {
            // already granted, start OmRecorder

//            recordFile = DirManager.generateFile(MessageType.SENT_VOICE_MESSAGE);
//            recorder = OmRecorder.wav(
//                    new PullTransport.Default(getMic(), new PullTransport.OnAudioChunkPulledListener() {
//                        @Override
//                        public void onAudioChunkPulled(AudioChunk audioChunk) {
//
//                        }
//                    }), recordFile);
//
//
//            //start record when the record sound "BEEP" finishes
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    recorder.startRecording();
//                }
//            }, RECORD_START_AUDIO_LENGTH);

        }

    }


    private void scrollToLast() {
        recyclerView.scrollToPosition(messageList.size() - 1);
    }

    private void setAdapter() {

        mediaPlayer = new MediaPlayer();
        messageAdapter = new MessageAdapter(messageList, getApplicationContext(), this, mediaPlayer, seekHandler, null);
        messageAdapter.isGroup = true;
        messageAdapter.members = members == null ? new ArrayList<>() : members;
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(messageAdapter);
        recyclerView.setNestedScrollingEnabled(true);

        scrollToLast();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_GALLERY_REQUEST && resultCode == RESULT_OK) {

            Uri uri = data.getData();

            File file = new File(StorageUtil.getRealPathFromURIPath(uri, this));

            if (uri.toString().contains("image")) {
                sendImage(uri, file);
            } else if (uri.toString().contains("video")) {

                File directory = new File(Environment.getExternalStorageDirectory() + "/videos");
                if (!directory.isDirectory())
                    directory.mkdirs();

                File videoFile = new File(directory, Util.randomString() + "_my_video" + ".mp4");

                sendTheVideo(data.getData(), file);
                createVideoMessage(file.getName());
/*
                VideoCompressor.start(
                        getApplicationContext(), // => This is required if srcUri is provided. If not, pass null.
                        null, // => Source can be provided as content uri, it requires context.
                        StorageUtil.getRealPathFromURIPath(uri, this), // => This could be null if srcUri and context are provided.
                        videoFile.getPath(),
                        new CompressionListener() {
                            @Override
                            public void onStart() {

                            }

                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onFailure(String failureMessage) {
                            }

                            @Override
                            public void onProgress(float v) {
                                // Update UI with progress value
                                runOnUiThread(new Runnable() {
                                    public void run() {

                                    }
                                });
                            }

                            @Override
                            public void onCancelled() {
                                // On Cancelled
                            }
                        }, VideoQuality.MEDIUM, false, false);*/
            }

        } else if (requestCode == REQUEST_TAKE_GALLERY_VIDEO && resultCode == RESULT_OK) {

            Uri uri = data.getData();

            File file = new File(StorageUtil.getRealPathFromURIPath(uri, this));

            if (uri != null) {

                long fileSizeInBytes = file.length();
                // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
                long fileSizeInKB = fileSizeInBytes / 1024;
                long fileSizeInMB = fileSizeInKB / 1024;

                File directory = new File(Environment.getExternalStorageDirectory() + "/videos");
                if (!directory.isDirectory())
                    directory.mkdirs();

                File videoFile = new File(directory, Util.randomString() + "_my_video" + ".mp4");

                sendTheVideo(data.getData(), file);
                createVideoMessage(file.getName());

                /*
                VideoCompressor.start(
                        getApplicationContext(), // => This is required if srcUri is provided. If not, pass null.
                        null, // => Source can be provided as content uri, it requires context.
                        StorageUtil.getRealPathFromURIPath(uri, this), // => This could be null if srcUri and context are provided.
                        videoFile.getPath(),
                        new CompressionListener() {
                            @Override
                            public void onStart() {

                            }

                            @Override
                            public void onSuccess() {
                            }

                            @Override
                            public void onFailure(String failureMessage) {
                                Toast.makeText(getApplicationContext(),"FAILED :" + failureMessage,Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onProgress(float v) {
                                // Update UI with progress value
                                runOnUiThread(new Runnable() {
                                    public void run() {

                                    }
                                });
                            }

                            @Override
                            public void onCancelled() {
                                // On Cancelled
                            }
                        }, VideoQuality.MEDIUM, false, false);

                 */

            }


        } else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {

            Uri uri = mMakePhotoUri;

            if (uri != null) {
                sendImage(uri, imageFile);
            }
        }

        if (requestCode == MAP_BUTTON_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                VanillaAddress selectedPlace = (VanillaAddress) data.getSerializableExtra(KeyUtils.SELECTED_PLACE);
                Log.v("sohaib", selectedPlace.getLatitude() + " longi :" + selectedPlace.getLongitude());

                double latitude = data.getDoubleExtra(LATITUDE, 0.0);
                Log.d("LATITUDE****", String.valueOf(latitude));
                double longitude = data.getDoubleExtra(LONGITUDE, 0.0);
                Log.d("LONGITUDE****", String.valueOf(longitude));

                this.latitude = selectedPlace.getLatitude();
                this.longitude = selectedPlace.getLongitude();

                if (this.latitude != 0 && this.longitude != 0) {
                    sendMessage(SENT_LOCATION, this.latitude + "," + this.longitude);
                }

            }

        }

        if (requestCode == REQUEST_FILE_OPEN && resultCode == Activity.RESULT_OK) {


            ArrayList<MediaFile> files = data.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES);
            if (files.size() > 0) {

                mediaFiles.clear();
                mediaFiles.addAll(data.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES));
                String extension = files.get(0).getName().substring(files.get(0).getName().lastIndexOf(".") + 1);

                long fileSizeInBytes = new File(files.get(0).getUri().getPath()).length();
                // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
                long fileSizeInKB = fileSizeInBytes / 1024;
                long fileSizeInMB = fileSizeInKB / 1024;

                if (fileSizeInMB < 10) {

                    try {
                        sendFile(files.get(0).getUri(), new File(RealPathUtil.getRealPath(getApplicationContext(), files.get(0).getUri())), files.get(0).getName(), files.get(0).getMimeType());
                    } catch (Exception e) {

                        e.printStackTrace();

                        File pdfFile = null;

                        final String id = DocumentsContract.getDocumentId(files.get(0).getUri());

                        if (id != null) {
                            final File file = new File(getApplicationContext().getCacheDir(), TEMP_FILE + Objects.requireNonNull(getApplicationContext().getContentResolver().getType(files.get(0).getUri())).split("/")[1]);
                            try (final InputStream inputStream = getApplicationContext().getContentResolver().openInputStream(files.get(0).getUri()); OutputStream output = new FileOutputStream(file)) {
                                final byte[] buffer = new byte[4 * 1024]; // or other buffer size
                                int read;

                                while ((read = inputStream.read(buffer)) != -1) {
                                    output.write(buffer, 0, read);
                                }

                                output.flush();
                                pdfFile = file;

                                if (pdfFile != null) {
                                    sendFile(files.get(0).getUri(), pdfFile, files.get(0).getName(), files.get(0).getMimeType());
                                }
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }

                } else
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.file_limit_msg), Toast.LENGTH_SHORT).show();

            }

        }

        if (requestCode == Consts.CAMERA_REQUEST && resultCode == Consts.CAMERA_REQUEST_OK) {
            String filePath = data.getStringExtra("PATH");
            if (data.getIntExtra("TYPE", 0) == 2) {
                sendTheVideo(data.getData(), new File(filePath));
            } else {
                sendImage(data.getData(), FileUtils.getCompressedImageFile(new File(filePath), getApplicationContext()));
            }
        }
    }

    private void sendFile(final Uri fileUri, final File file, final String name, final String ext) {
        pdfFileName = name;
        saveMessage("", SENT_FILE, fileUri, file);
    }

    private void sendVoiceMessage(File recordFile, String duration) {
        saveMessage("", SENT_AUDIO, Uri.parse(recordFile.getAbsolutePath()), recordFile);
    }


    private void sendTheVideo(Uri videoUri, File file) {
        saveMessage("", SENT_VIDEO, videoUri, file);
    }


    //send text message
    private void sendMessage(int type, String location) {

        String message = etMessage.getText().toString();

        if (TextUtils.isEmpty(message) && type == 1) {
            Toast.makeText(getApplicationContext(), "لرجاء كتابة الرسالة", Toast.LENGTH_SHORT).show();
        } else {
            saveMessage(type == 1 ? message : location, type, null, null);
            etMessage.setText("");
        }

    }


    //"isFromCamera" is when taking a picture ,because taking a picture from camera will save it directly in the app folder
    //send only one image
    private void sendImage(Uri imageUri, File file) {
        saveMessage("", SENT_IMAGE, imageUri, file);
    }


    public void showMessage(Message message) {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        message.setCreatedAt(new Date().getTime());
        messageList.add(message);
        messageAdapter.notifyDataSetChanged();
        scrollToLast();

    }

    @Override
    public void onLocationChanged(Location location) {
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
        Log.v("LOCATION", latitude + "," + longitude);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));

        Configuration config = new Configuration(base.getResources().getConfiguration());
        config.fontScale = 1.2f;
        applyOverrideConfiguration(config);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Clear the Activity's bundle of the subsidiary fragments' bundles.
        outState.clear();
    }


    @Override
    public void onItemClick(View view, int position, String path, Message message, int type) {

        if (type == SENT_IMAGE) {
            Intent intent = new Intent(GroupChatActivity.this, ViewImageActivity.class);
            intent.putExtra("URL", APIClient.imageUrl + message.getMessage());
            intent.putExtra("qa", true);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else if (type == SENT_LOCATION) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(path));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else if (type == SENT_VIDEO) {
            //Intent intent = new Intent(Intent.ACTION_VIEW);
            //  intent.setDataAndType(Uri.parse(path), "video/mp4");
            Intent intent = new Intent(GroupChatActivity.this, VideoViewActivity.class);
            intent.putExtra("path", APIClient.imageUrl + message.getMessage());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            Intent browserIntent = new Intent(GroupChatActivity.this, PDFViewerActivity.class);
            browserIntent.putExtra("file_uri", path);
            browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(browserIntent);
        }
    }

    @Override
    public void onDeleteItem(Message message) {

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v("qqq", "Permission: " + permissions[0] + "was " + grantResults[0]);
            //resume tasks needing this permission
            if (requestCode == 303) {
                startCurrentLocationUpdates();
                //pickLocation();
            }
        } else {
            if (requestCode == 303) {
                Toast.makeText(getApplicationContext(), "Please allow location permission to send location", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        }
    }

    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Goto Settings Page To Enable GPS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // if (isGooglePlayServicesAvailable()) {
        //  fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        startCurrentLocationUpdates();
        // }
    }

    @Override
    protected void onPause() {
        super.onPause();

        locationManager.removeUpdates(mLocationListener);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


        PrefUtil.writePreferenceValue(getApplicationContext(), "ACTIVE_USER", "xxx");

        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                seekHandler.removeCallbacksAndMessages(null);
                mediaPlayer.stop();
                mediaPlayer.release();
            }
        }

    }

    public void createVideoMessage(String urls) {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

        Message message1 = new Message();
        message1.setFileUrl(urls);
        message1.setMessage(null);
        message1.setType(SENT_VIDEO);
        message1.setUserId(user.getId());
        message1.setCreatedAt(new Date().getTime());
        messageList.add(message1);
        messageAdapter.notifyDataSetChanged();
        scrollToLast();

    }

    //save Chat Message

    private void saveMessage(String message, int type, Uri fileUri, File file) {

        if (type != 3)
            loader.setVisibility(View.VISIBLE);

        final MultipartBody.Part msg = MultipartBody.Part.createFormData("message", message);
        final MultipartBody.Part typeId = MultipartBody.Part.createFormData("type_id", String.valueOf(type));

        MultipartBody.Part fileBody = null;

        if (fileUri != null) {

            String mimeType = URLConnection.guessContentTypeFromName(file.getName());

            RequestBody requestFile =
                    RequestBody.create(
                            MediaType.parse(mimeType),
                            file
                    );

            fileBody = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        }

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<Message> call = apiInterface.sendGroupMessage("Bearer " + TokenHelper.getToken(), msg, typeId, fileBody);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {


                loader.setVisibility(View.GONE);

                try {

                    if (response.body() != null) {
                        if (type == SENT_VIDEO)
                            setUploadVideoPath(file.getName(), response.body().getMessage());
                        else
                            showMessage(response.body());
                    }

                    if (type == SENT_FILE) {
                        deleteTempFile();
                    }

                } catch (Exception ex) {
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                loader.setVisibility(View.GONE);
            }
        });


    }

    private void deleteTempFile() {
        final File[] files = getApplicationContext().getCacheDir().listFiles();
        if (files != null) {
            for (final File file : files) {
                if (file.getName().contains(TEMP_FILE)) {
                    file.delete();
                }
            }
        }
    }

    private void setUploadVideoPath(String path, String url) {

        for (int i = 0; i < messageList.size(); i++) {

            if (messageList.get(i).getType() == SENT_VIDEO) {
                if (messageList.get(i).getFileUrl() != null) {
                    if (messageList.get(i).getFileUrl().equals(path)) {
                        messageList.get(i).setMessage(url);
                        break;
                    }
                }
            }
        }
        messageAdapter.notifyDataSetChanged();
    }
}




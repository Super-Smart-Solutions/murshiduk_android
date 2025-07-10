package com.saatco.murshadik;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

import me.leolin.shortcutbadger.ShortcutBadger;
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
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.abedelazizshe.lightcompressorlibrary.CompressionListener;
import com.abedelazizshe.lightcompressorlibrary.VideoCompressor;
import com.abedelazizshe.lightcompressorlibrary.VideoQuality;
import com.abedelazizshe.lightcompressorlibrary.config.AppSpecificStorageConfiguration;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.devlomi.record_view.OnBasketAnimationEnd;
import com.devlomi.record_view.OnRecordClickListener;
import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordView;
import com.github.javiersantos.bottomdialogs.BottomDialog;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;


import com.jaiselrahman.filepicker.activity.FilePickerActivity;
import com.jaiselrahman.filepicker.config.Configurations;
import com.jaiselrahman.filepicker.model.MediaFile;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBMessageStatusesManager;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.QBSystemMessagesManager;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBChatDialogMessageListener;
import com.quickblox.chat.listeners.QBChatDialogParticipantListener;
import com.quickblox.chat.listeners.QBMessageStatusListener;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.chat.request.QBMessageGetBuilder;
import com.quickblox.chat.utils.DialogUtils;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.QBRTCClient;
import com.quickblox.videochat.webrtc.QBRTCSession;
import com.quickblox.videochat.webrtc.QBRTCTypes;
import com.saatco.murshadik.Helpers.ChatHelper;
import com.saatco.murshadik.Helpers.KeyboardHelper;
import com.saatco.murshadik.Helpers.PermissionsHelper;
import com.saatco.murshadik.Helpers.ProfileHelper;
import com.saatco.murshadik.Helpers.TokenHelper;
import com.saatco.murshadik.adapters.MessageAdapter;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.api.APIInterface;
import com.saatco.murshadik.api.response.BaseResponse;
import com.saatco.murshadik.constants.MessageType;
import com.saatco.murshadik.databinding.ActivityChatNewBinding;
import com.saatco.murshadik.db.StorageHelper;
import com.saatco.murshadik.fcm.LoginService;
import com.saatco.murshadik.model.Message;
import com.saatco.murshadik.model.User;
import com.saatco.murshadik.utils.Consts;
import com.saatco.murshadik.utils.DialogUtil;
import com.saatco.murshadik.utils.DirManager;
import com.saatco.murshadik.utils.FileUtils;
import com.saatco.murshadik.utils.PermissionsChecker;
import com.saatco.murshadik.utils.PushNotificationSender;
import com.saatco.murshadik.utils.RealPathUtil;
import com.saatco.murshadik.utils.SharedPrefsHelper;
import com.saatco.murshadik.utils.SimpleTarget;
import com.saatco.murshadik.utils.StorageUtil;
import com.saatco.murshadik.utils.ToastUtils;
import com.saatco.murshadik.utils.Util;
import com.saatco.murshadik.utils.WebRtcSessionManager;
import com.saatco.murshadik.views.AnimButton;
import com.saatco.murshadik.views.AttachmentView;

import com.vanillaplacepicker.data.VanillaAddress;
import com.vanillaplacepicker.utils.KeyUtils;


import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatMessageListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.saatco.murshadik.App.getInstance;
import static com.adevinta.leku.LocationPickerActivityKt.LATITUDE;
import static com.adevinta.leku.LocationPickerActivityKt.LONGITUDE;

public class ChatActivityNew extends BaseActivity implements LocationListener, MessageAdapter.OnMessageItemClickListener, ChatMessageListener, QBMessageStatusListener {

    private static final int CAMERA_REQUEST = 4659;
    private static final int PICK_GALLERY_REQUEST = 4815;

    private final int MY_PERMISSIONS_RECORD_AUDIO = 1;
    private static final int REQUEST_TAKE_GALLERY_VIDEO = 4981;
    private static final int REQUEST_FILE_OPEN = 975;

    private static final int ACTION_TAKE_VIDEO = 320;

    private static final int MAP_BUTTON_REQUEST_CODE = 909;

    //start voice record after 575ms until the sound effect finishes
    private static final int RECORD_START_AUDIO_LENGTH = 575;

    private RecyclerView recyclerView;
    private ImageView imgAttachment, cameraBtn, emojiBtn;
    private LinearLayout typingLayout;
    public EmojiconEditText etMessage;
    private RecordView recordView;
    private AnimButton recordButton;
    private AttachmentView attachmentView;
    private MessageAdapter messageAdapter;
    private ScrollView nestedScrollView;
    View rootView;

    EmojIconActions emojIcon;

    Menu currentMenu;

    ArrayList<com.saatco.murshadik.model.Message> messageList = new ArrayList<>();
    LinearLayoutManager linearLayoutManager;

    boolean wasInTypingMode = false;
    boolean typingStarted = false;

    User user, receiverUser = null;

//    Recorder recorder;
    File recordFile;

    ActivityResultLauncher<Intent> startForResultVideoTrimmer;


    TextView tvName;
    TextView tvStatus;
    ImageView ivProfile;
    ImageView back;
    ImageView btnCall;
    ImageView btnVideo;
    ImageView btnRate;
    LinearLayout btnUserInfo;

    //location
    protected double latitude, longitude = 0;

    private PermissionsChecker checker;

    Timer t;

    QBChatDialog qbChatDialog;

    boolean isActivityOpen = false;

    //Quickblox
    private final ArrayList<MediaFile> mediaFiles = new ArrayList<>();
    private QBChatDialogParticipantListener participantListener;
    private MediaPlayer mediaPlayer;
    private final Handler seekHandler = new Handler();

    LocationManager locationManager;

    // private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location currentLocation;

    private QBMessageStatusesManager messageStatusesManager;
    private QBMessageStatusListener messageStatusListener;
    private QBSystemMessagesManager systemMessagesManager;
    private ChatMessageListener chatMessageListener;
    private ConnectionListener chatConnectionListener;

    public static final int SENT_TEXT = 1;
    public static final int SENT_VIDEO = 3;
    public static final int SENT_IMAGE = 4;
    public static final int SENT_AUDIO = 2;
    public static final int SENT_LOCATION = 6;
    public static final int SENT_FILE = 5;
    public static final String TEMP_FILE = "temp.";
    private static final int CAPTURE_MEDIA = 368;

    File imageFile, videoFile;
    String pdfFileName;


    private boolean isDialogJoin = false;
    private boolean isChatBot = false;
    View loader;

    private String currentPhotoPath;

    /**
     * Called when the activity has detected the user's press of the back
     * key. The {@link #getOnBackPressedDispatcher() OnBackPressedDispatcher} will be given a
     * chance to handle the back button before the default behavior of
     * {@link Activity#onBackPressed()} is invoked.
     *
     * @see #getOnBackPressedDispatcher()
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (getIntent().getBooleanExtra("is_noti", false)) {
            finish();
            startActivity(new Intent(this, ChatUserActivity.class));
        }
    }

    ActivityChatNewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatNewBinding.inflate(getLayoutInflater(), null, false);
        setContentView(binding.getRoot());

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(binding.rootView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        initViews();



        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        receiverUser = (User) getIntent().getSerializableExtra("USER");
        user = ProfileHelper.getAccount(getInstance());
        isChatBot = getIntent().getBooleanExtra("is_cb", false);

        if (receiverUser.getChatId().trim().isEmpty()) {
            onBackPressed();
        }

        try {
            PrefUtil.writeIntValue(getApplicationContext(), "r_id", Integer.parseInt(receiverUser.getChatId()));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        PrefUtil.writeIntValue(getApplicationContext(), "USER_ID", receiverUser.getId());
        PrefUtil.writeBooleanValue(getApplicationContext(), "is_chat_open", true);



        checker = new PermissionsChecker(getApplicationContext());

        isActivityOpen = true;

        QBChatService.setDefaultPacketReplyTimeout(30000);

        systemMessagesManager = QBChatService.getInstance().getSystemMessagesManager();
        chatMessageListener = this;

        ChatHelper.getInstance();
        //Connection listener
        ChatHelper.getInstance().addConnectionListener(connectionListener);

        if (receiverUser.chatDialog != null) {
            qbChatDialog = receiverUser.chatDialog;
        }

        if (!isChatBot && receiverUser.getName() != null)
            loginQbUser();

        getUserByChatId();

//        stopping permissions request until app needed
//        edit by amin
//        permissions();
        init();
        setupCall();
        getUserPhoto();
        setAdapter();

        messageStatusListener = this;
        messageStatusesManager = ChatHelper.getInstance().getQbChatService().getMessageStatusesManager();
        if (messageStatusesManager != null)
            messageStatusesManager.addMessageStatusListener(messageStatusListener);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (getIntent().getBooleanExtra("is_noti", false)) {
                    finish();
                    startActivity(new Intent(ChatActivityNew.this, ChatUserActivity.class));
                }
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
                    ActivityCompat.requestPermissions(ChatActivityNew.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, 222);
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
                Toast.makeText(ChatActivityNew.this, R.string.voice_message_is_short_toast, Toast.LENGTH_SHORT).show();
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

        etMessage.setOnClickListener(v -> {
            emojIcon.closeEmojIcon();
            if (attachmentView.isShowing())
                attachmentView.hide(imgAttachment);

        });

        imgAttachment.setOnClickListener(view -> {

            attachmentView.reveal(view);
            KeyboardHelper.hideSoftKeyboard(ChatActivityNew.this, etMessage);

        });


        attachmentView.setOnAttachmentClick(id -> {
            if (id == R.id.attachment_gallery) {
                if (PermissionsHelper.isMediaImagePermissionGranted(ChatActivityNew.this) &&
                        PermissionsHelper.isMediaVideoPermissionGranted(ChatActivityNew.this))
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
        });

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendMessage(SENT_TEXT, "");
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

        btnUserInfo.setClickable(false);

        btnUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(ChatActivityNew.this, userInfoActivity.class);
                i.putExtra("USER", receiverUser);
                startActivity(i);
            }
        });


        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mStatus,
                new IntentFilter("user_status"));

        btnRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showRatingDialog();

            }
        });

        //start activity for result need to init in onCreate method;
        initActivityResultForVideoTrim();


    }

    private void initViews() {
        tvName = binding.tvTitle;
        tvStatus = binding.tvStatus;
        ivProfile = binding.imgProfile;
        back = binding.imgBack;
        btnCall = binding.btnCall;
        btnVideo = binding.btnVideo;
        btnRate = binding.btnRate;
        btnUserInfo = binding.btnUserInfo;

        loader = binding.llProgressBar.getRoot();
    }

    //************************** show rating dialog ***********************//
    private void showRatingDialog() {

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.dialog_rating, null);

        BottomDialog builder = new BottomDialog.Builder(ChatActivityNew.this)
                .setCustomView(customView, 0, 0, 0, 0)
                .show();

        Button btnDone = customView.findViewById(R.id.btnDone);
        RatingBar ratingBar = customView.findViewById(R.id.rating);
        EditText comment = customView.findViewById(R.id.etComment);

        btnDone.setOnClickListener(view -> {
            if (ratingBar.getRating() > 0.0 && !comment.getText().toString().equals("")) {
                rateConsultant(String.valueOf(ratingBar.getRating()), comment.getText().toString());
                builder.dismiss();
            }
        });


    }

    private void initActivityResultForVideoTrim() {
        startForResultVideoTrimmer = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK &&
                            result.getData() != null) {
                        Uri trimmed_uri = Uri.parse("");
                        Log.d("TAG ", "Trimmed path:: " + trimmed_uri);
                        ArrayList<Uri> uris = new ArrayList<>();
                        uris.add(trimmed_uri);

                        createVideoMessage(trimmed_uri.getPath());

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
                                // => the directory to save the compressed video(s)
                                new CompressionListener() {
                                    @Override
                                    public void onSuccess(int i, long l, @Nullable String s) {
                                        if (s != null) {
                                            setUploadVideoPath(trimmed_uri.getPath(), s);
                                            sendTheVideo(trimmed_uri, new File(s));
                                        }
                                        Log.v("size: ", l + "%");
                                    }

                                    @Override
                                    public void onStart(int i) {

                                    }

                                    @Override
                                    public void onFailure(int index, String failureMessage) {
                                        // On Failure
                                    }

                                    @Override
                                    public void onProgress(int index, float progressPercent) {
                                        // Update UI with progress value
                                        runOnUiThread(() -> Log.v("Compress progress: ", progressPercent + "%"));
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

    //**************************  rating api ***********************//
    private void rateConsultant(String rating, String comment) {

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<BaseResponse> call = apiInterface.updateUserRating("Bearer " + TokenHelper.getToken(), receiverUser.getId(), rating, comment);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {

                try {
                    Util.showSuccessToast(getResources().getString(R.string.success_rate), ChatActivityNew.this);

                } catch (Exception ignored) {
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {

            }
        });

    }

    public void loginQbUser() {
        if (!ChatHelper.getInstance().isLogged()) {

            ChatHelper.getInstance().loginToChat(SharedPrefsHelper.getInstance().getQbUser(), new QBEntityCallback<Void>() {
                @Override
                public void onSuccess(Void aVoid, Bundle bundle) {
                    createChatDialog();
                }

                @Override
                public void onError(QBResponseException e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else if (qbChatDialog != null) {
            initChat();
        } else {
            createChatDialog();
        }
    }

    private void createChatDialog() {

        ArrayList<Integer> occupantIdsList = new ArrayList<Integer>();

        occupantIdsList.add(Integer.valueOf(receiverUser.getChatId()));

        QBChatDialog dialogNew = new QBChatDialog();
        dialogNew.setName("Chat with " + receiverUser.getName());
        dialogNew.setPhoto("1786");
        dialogNew.setType(QBDialogType.PRIVATE);
        dialogNew.setOccupantsIds(occupantIdsList);

        QBChatDialog dialog = DialogUtils.buildDialog("Chat with " + receiverUser.getName(), QBDialogType.PRIVATE, occupantIdsList);

        QBRestChatService.createChatDialog(dialog).performAsync(new QBEntityCallback<QBChatDialog>() {
            @Override
            public void onSuccess(QBChatDialog result, Bundle params) {

                qbChatDialog = result;
                initChat();

            }

            @Override
            public void onError(QBResponseException responseException) {
                Log.v("QBERRE", responseException.getLocalizedMessage());
            }
        });
    }

    private void initChat() {

        try {
            qbChatDialog.initForChat(ChatHelper.getInstance().getQbChatService());
        }catch (Exception ignore){
            ToastUtils.longToast(R.string.error_happend);
            finish();
            return;
        }

        isDialogJoin = true;

        if (qbChatDialog != null) {
            if (messageList.size() < 1)
                getMessagesFromHistory(qbChatDialog);
        }

        recieveMessages();
        QBChatMessage qbChatMessage = new QBChatMessage();
        qbChatMessage.setBody("update_read_status");

        try {
            qbChatDialog.readMessage(qbChatMessage);
        } catch (XMPPException | SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }

    private void deleteChatMessage(String messageId) {
        Set<String> messagesIDs = new HashSet<>();
        messagesIDs.add(messageId);
        if (ChatHelper.getInstance().getQbChatService().isLoggedIn()) {
            QBRestChatService.deleteMessages(messagesIDs, true).performAsync(new QBEntityCallback<Void>() {
                @Override
                public void onSuccess(Void aVoid, Bundle bundle) {
                }

                @Override
                public void onError(QBResponseException e) {

                }
            });
        }
    }

    private final QBChatDialogMessageListener listener = new QBChatDialogMessageListener() {
        @Override
        public void processMessage(String s, QBChatMessage qbChatMessage, Integer integer) {

            showRecieverMessage(qbChatMessage);

            if (isActivityOpen) {
                try {
                    markRead(qbChatDialog.getDialogId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            ExecutorService executors = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());
            executors.execute(() -> {
                //background work
                if (isActivityOpen) {
                    StorageHelper.removeMessage(Integer.parseInt(receiverUser.getChatId()));
                }
                handler.post(() -> {
                    //UI Thread work here

                });
            });

        }

        @Override
        public void processError(String s, QBChatException e, QBChatMessage qbChatMessage, Integer integer) {

            Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    };


    //Recieve chat message
    private void recieveMessages() {
        qbChatDialog.addMessageListener(listener);
    }

    //********************* get chat history from QB SDK ******************//
    private void getMessagesFromHistory(QBChatDialog qbChatDialog) {

        if (qbChatDialog != null) {

            markRead(qbChatDialog.getDialogId());
            LocalBroadcastManager.getInstance(ChatActivityNew.this).sendBroadcast(new Intent("chat_user_service"));

            if (isActivityOpen) {
                loader.setVisibility(View.VISIBLE);
            }

            QBMessageGetBuilder messageGetBuilder = new QBMessageGetBuilder();
            messageGetBuilder.setLimit(100);
            messageGetBuilder.sortDesc("date_sent");

            QBRestChatService.getDialogMessages(qbChatDialog, messageGetBuilder).performAsync(new QBEntityCallback<ArrayList<QBChatMessage>>() {
                @Override
                public void onSuccess(ArrayList<QBChatMessage> qbChatMessages, Bundle bundle) {

                    loader.setVisibility(View.GONE);

                    messageList.clear();

                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss", Locale.ENGLISH);
                    formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

                    SimpleDateFormat formatterRiyadh = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss", Locale.ENGLISH);
                    formatterRiyadh.setTimeZone(TimeZone.getTimeZone("Asia/Riyadh"));
                    Calendar cal = Calendar.getInstance(Locale.ENGLISH);

                    for (QBChatMessage qbChatMessage : qbChatMessages) {
                        int type = 1;
                        try {
                            type = Integer.parseInt(String.valueOf(qbChatMessage.getProperty("type")));
                        }catch (Exception ignored){
                        }

                        int userId = qbChatMessage.getSenderId() == Integer.parseInt(receiverUser.getChatId()) ? receiverUser.getId() : user.getId();
                        Message message1 = new Message();
                        message1.setMessage(type == SENT_TEXT ? qbChatMessage.getBody() : String.valueOf(qbChatMessage.getProperty("url")));
                        message1.setType(Integer.parseInt(String.valueOf(qbChatMessage.getProperty("type"))));
                        message1.setChatUserIds(receiverUser.getId() + "," + user.getId());
                        message1.setUserId(userId);

                        message1.setObjectId(qbChatMessage.getId());

                        cal.setTimeInMillis(qbChatMessage.getDateSent() * 1000);
                        message1.setCreatedAt(cal.getTimeInMillis());

                        messageList.add(message1);
                    }

                    Collections.reverse(messageList);
                    messageAdapter.notifyDataSetChanged();
                    scrollToLast();

                }

                @Override
                public void onError(QBResponseException e) {
                    //    spinKitView.setVisibility(View.GONE);
                    loader.setVisibility(View.GONE);
                }
            });
        }
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

    private void pickGallery() {

        if (!isPermissionGranted(Manifest.permission.CAMERA)) {
            return;
        }
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 60);
        takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Environment.getExternalStorageDirectory().getPath() + "videocapture_example.mp4");

        startActivityForResult(takeVideoIntent, ACTION_TAKE_VIDEO);

        PrefUtil.writeBooleanValue(getApplicationContext(), "is_chat_pick", true);

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

    private void setupCall() {


        if (!QBChatService.getInstance().isLoggedIn()) {
            startLoginService();
        }

        // Ensure the user is logged in to QuickBlox chat service before enabling call buttons
        if (!QBChatService.getInstance().isLoggedIn()) {
            startLoginService();
        }

        // Set up the audio call button click listener
        btnCall.setOnClickListener(view -> {
            // If the receiver is a consultant and offline, show an offline alert dialog
            if (!receiverUser.isOnline() && receiverUser.isConsultantUser()) {
                DialogUtil.showOfflineAlert(ChatActivityNew.this, getResources().getString(R.string.work_status), getResources().getString(R.string.not_available_msg) + " " + receiverUser.getName());
                return;
            }
            // Check for required permissions (audio or Bluetooth), request if missing
            if (checker.lacksPermissions(Consts.PERMISSIONS[1]) || checkBluetoothConnectPermission()) {
                startPermissionsActivity(true);
            } else if (checkIsLoggedInChat()) {
                // If permissions and login are OK, start an audio call
                startCall(false);
            }
        });

        // Set up the video call button click listener
        btnVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // If the receiver is a consultant and offline, show an offline alert dialog
                if (!receiverUser.isOnline() && receiverUser.isConsultantUser()) {
                    DialogUtil.showOfflineAlert(ChatActivityNew.this, getResources().getString(R.string.work_status), getResources().getString(R.string.not_available_msg) + " " + receiverUser.getName());
                    return;
                }
                // Prepare headers for the video call (e.g., sender's name)
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("name", ProfileHelper.getAccount(getApplicationContext()).getFullname());

                // Check for required permissions (audio, video, Bluetooth), request if missing
                if (checker.lacksPermissions(Consts.PERMISSIONS) || checkBluetoothConnectPermission()) {
                    startPermissionsActivity(false);
                } else if (checkIsLoggedInChat()) {
                    // If permissions and login are OK, start a video call
                    startCall(true);
                }
            }
        });

    }

    private boolean checkBluetoothConnectPermission() {
        return checker.lacksPermissions(Manifest.permission.BLUETOOTH_CONNECT) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S;
    }

    private void startPermissionsActivity(boolean checkOnlyAudio) {
        PermissionsActivity.startActivity(this, checkOnlyAudio, Consts.PERMISSIONS);
    }



    /**
     * TODO: improving permissions request method
     */
    private boolean isPermissionGranted(String str_permission_name) {
        if (ContextCompat.checkSelfPermission(this, str_permission_name) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{str_permission_name}, 4);
        } else {
            return true;
        }
        return false;
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
        nestedScrollView = findViewById(R.id.nestedScrollView);

        emojIcon = new EmojIconActions(getApplicationContext(), rootView, etMessage, emojiBtn, "#495C66", "#DCE1E2", "#E6EBEF");

        attachmentView = findViewById(R.id.attachment_view);


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
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    etMessage.requestFocus();
                }
            }, 100);

        }
    }

    //pick image
    private void pickImages() {

        Intent galleryIntent = new Intent().setAction(Intent.ACTION_PICK);
        galleryIntent.setType("video/*, image/*");
        String[] mimetypes = {"image/*", "video/*"};
        galleryIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
        startActivityForResult(galleryIntent, PICK_GALLERY_REQUEST);

        PrefUtil.writeBooleanValue(getApplicationContext(), "is_chat_pick", true);

    }

    //pick location
    private void pickLocation() {


        if (ActivityCompat.checkSelfPermission(
                ChatActivityNew.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                ChatActivityNew.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 303);
        } else {

            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                sendLocation();
            } else {
                Toast.makeText(getApplicationContext(), "من فضلك على موقعك", Toast.LENGTH_SHORT).show();
                showGPSDisabledAlertToUser();
            }
        }
    }

    private void sendLocation() {

        if (!StorageHelper.getLocation().equals("")) {
            sendMessage(SENT_LOCATION, StorageHelper.getLocation());
        } else {
            Toast.makeText(getApplicationContext(), "حاول مرة اخرى", Toast.LENGTH_SHORT).show();
        }
    }

    //startCamera
    private void startCamera() {

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
                        startActivityForResult(takePictureIntent, Consts.CAMERA_REQUEST);
                    }
                }

//                Intent intent = new Intent(ChatActivityNew.this, CameraActivity.class);
//                startActivityForResult(intent, Consts.CAMERA_REQUEST);
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

    private void uploadFile() {

        if (PermissionsHelper.isMediaImagePermissionGranted(this)) {

            Intent intent = new Intent(ChatActivityNew.this, FilePickerActivity.class);
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

            PrefUtil.writeBooleanValue(getApplicationContext(), "is_chat_pick", true);

        }

    }


    private int getUnreadMessageCount() {
        int count = 0;
        if (StorageHelper.getAllMessages() != null) {
            for (Message msg : StorageHelper.getAllMessages()) {
                count++;
            }
        }
        return count;
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

    //update user photo thumb img
    private void getUserPhoto() {

        tvName.setText(receiverUser.getName());

        if (receiverUser.isConsultantUser())
            btnRate.setVisibility(View.VISIBLE);
        else
            btnRate.setVisibility(View.GONE);

        if (user.isConsultantUser())
            btnRate.setVisibility(View.GONE);

        if (receiverUser.isFarmer())
            tvStatus.setVisibility(View.GONE);


        RequestOptions requestOptions = new RequestOptions().override(100, 100).circleCrop();

        Glide.with(getApplicationContext())
                .asBitmap()
                .load(APIClient.imageUrl + receiverUser.getPhotoUrl())
                .apply(requestOptions)
                .placeholder(R.drawable.profile_photo)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {

                        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), resource);
                        final float roundPx = (float) resource.getWidth() * 2f;
                        roundedBitmapDrawable.setCornerRadius(roundPx);

                        ivProfile.setImageDrawable(roundedBitmapDrawable);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
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

        if (ContextCompat.checkSelfPermission(ChatActivityNew.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ChatActivityNew.this,
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
        nestedScrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                nestedScrollView.fullScroll(View.FOCUS_DOWN);
                etMessage.requestFocus();
            }
        }, 200);
    }

    private void setAdapter() {

        mediaPlayer = new MediaPlayer();
        // adapter = new MessagingAdapter(messageList, getApplicationContext());
        messageAdapter = new MessageAdapter(messageList, getApplicationContext(), this, mediaPlayer, seekHandler, null);
        linearLayoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(messageAdapter);
        recyclerView.setNestedScrollingEnabled(true);


        scrollToLast();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        PrefUtil.writeBooleanValue(getApplicationContext(), "is_chat_pick", false);

        if (requestCode == PICK_GALLERY_REQUEST && resultCode == RESULT_OK) {

            Uri uri = data.getData();

            File file = new File(StorageUtil.getRealPathFromURIPath(uri, this));

            if (uri.toString().contains("image") || FileUtils.isImgFile(file.getName())) {
                sendImage(uri, file);
            } else if (uri.toString().contains("video") || FileUtils.isVideoFile(file.getName())) {


//                TrimVideo.activity(uri.toString())
//                        .setHideSeekBar(true)
//                        .setLocal("ar")
//                        .start(this, startForResultVideoTrimmer);
            }

        } else if (requestCode == ACTION_TAKE_VIDEO && resultCode == RESULT_OK) {

            Uri uri = data.getData();


            if (uri != null) {

//                TrimVideo.activity(uri.toString())
//                        .setHideSeekBar(true)
//                        .setLocal("ar")
//                        .start(this, startForResultVideoTrimmer);

            }

        }

        if (requestCode == MAP_BUTTON_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                VanillaAddress selectedPlace = (VanillaAddress) data.getSerializableExtra(KeyUtils.SELECTED_PLACE);

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
                    Toast.makeText(getApplicationContext(), "حجم الملف يجب ان لايتجاوز ١٠ ميجابايت", Toast.LENGTH_SHORT).show();

            }

        }

        if (requestCode == Consts.CAMERA_REQUEST && resultCode == RESULT_OK) {
            sendImage(Uri.parse(currentPhotoPath), FileUtils.getCompressedImageFile(new File(currentPhotoPath), getApplicationContext()));
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

            if (ChatHelper.getInstance().isLogged()) {


                QBChatMessage chatMessage = new QBChatMessage();

                chatMessage.setProperty("custom", String.valueOf(receiverUser.getId()));
                chatMessage.setProperty("type", String.valueOf(type));
                chatMessage.setProperty("url", String.valueOf(location));
                chatMessage.setRecipientId(Integer.valueOf(receiverUser.getChatId()));
                chatMessage.setSaveToHistory(true);
                chatMessage.setDateSent(System.currentTimeMillis() / 1000);
                chatMessage.setBody(type == SENT_LOCATION ? getType(type) : message);
                chatMessage.setMarkable(true);

                try {

                    if (qbChatDialog != null) {

                        qbChatDialog.sendMessage(chatMessage);
                        showMessage(chatMessage, type, "");
                        saveMessage(type == SENT_LOCATION ? location : message, type, null, null);

                        String msg = chatMessage.getBody().length() >= 10 ? chatMessage.getBody().substring(0, 9) : chatMessage.getBody();

                        ArrayList<Integer> opponentsList = new ArrayList<>();
                        opponentsList.add(Integer.valueOf(receiverUser.getChatId()));
                        String sender_name = ProfileHelper.getAccount(this).getName();
                        PushNotificationSender.sendMessageNotification(opponentsList, sender_name, message, type);

                        Intent intent = new Intent("update_message");
                        intent.putExtra("sender_id", receiverUser.getChatId());
                        intent.putExtra("last_message", chatMessage.getBody());
                        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                    }

                    etMessage.setText("");

                } catch (SmackException.NotConnectedException e) {
                    Log.d("BASSAM", "dialog" + e.getLocalizedMessage());
                    Toast.makeText(getApplicationContext(), "Unable to send a message", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

    private void sendFileMessage(int type, String path) {
        QBChatMessage chatMessage = new QBChatMessage();

        chatMessage.setProperty("custom", String.valueOf(receiverUser.getId()));
        chatMessage.setProperty("type", String.valueOf(type));
        chatMessage.setProperty("url", String.valueOf(path));
        chatMessage.setRecipientId(Integer.valueOf(receiverUser.getChatId()));
        chatMessage.setSaveToHistory(true);
        chatMessage.setDateSent(System.currentTimeMillis() / 1000);
        chatMessage.setBody(getType(type));
        chatMessage.setMarkable(true);

        if (type != SENT_VIDEO)
            showMessage(chatMessage, type, path);


        try {

            if (qbChatDialog != null) {
                qbChatDialog.sendMessage(chatMessage);


                ArrayList<Integer> opponentsList = new ArrayList<>();
                opponentsList.add(Integer.valueOf(receiverUser.getChatId()));

                String sender_name = ProfileHelper.getAccount(ChatActivityNew.this).getName();
                PushNotificationSender.sendMessageNotification(opponentsList, sender_name, "", type);
            }

            etMessage.setText("");

        } catch (SmackException.NotConnectedException e) {
            tryResendFileMessageAgine(chatMessage, type);
        }
    }

    private void tryResendFileMessageAgine(QBChatMessage chatMessage, int type) {
        try {
            Thread.sleep(1500);

            if (qbChatDialog != null) {
                qbChatDialog.sendMessage(chatMessage);


                ArrayList<Integer> opponentsList = new ArrayList<>();
                opponentsList.add(Integer.valueOf(receiverUser.getChatId()));

                String sender_name = ProfileHelper.getAccount(ChatActivityNew.this).getName();
                PushNotificationSender.sendMessageNotification(opponentsList, sender_name, "", type);
            }

            etMessage.setText("");

        } catch (SmackException.NotConnectedException | InterruptedException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Unable to send a message", Toast.LENGTH_SHORT).show();
        }
    }


    //"isFromCamera" is when taking a picture ,because taking a picture from camera will save it directly in the app folder
    //send only one image
    private void sendImage(Uri imageUri, File file) {
        saveMessage("", SENT_IMAGE, imageUri, file);
    }


    private void sendAudio(final String filePath, String audioDuration) {
        // Message message = new MessageCreator.Builder(user, MessageType.SENT_AUDIO).quotedMessage(getQuotedMessage()).path(filePath).duration(audioDuration).build();
    }

    public void updateStatusRead(String messageID, Integer userId) {
        for (Message message : messageList) {
            message.setRead(true);
        }
        messageAdapter.notifyDataSetChanged();
    }

    private void markRead(String dialogId) {

        Log.v("DID", dialogId);

        QBRestChatService.markMessagesAsRead(dialogId, null).performAsync(new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {

            }

            @Override
            public void onError(QBResponseException e) {

            }
        });

    }

    public void showRecieverMessage(QBChatMessage message) {

        Log.v("BSMO", String.valueOf(message.getProperty("type")));

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

        int type = Integer.parseInt(String.valueOf(message.getProperty("type")));
        String url = String.valueOf(message.getProperty("url"));

        Message message1 = new Message();
        message1.setMessage(type == 1 ? message.getBody() : url);
        message1.setType(Integer.parseInt(String.valueOf(message.getProperty("type"))));
        message1.setChatUserIds(receiverUser.getId() + "," + user.getId());
        message1.setUserId(receiverUser.getId());
        message1.setCreatedAt(new Date().getTime());

        messageList.add(message1);
        messageAdapter.notifyDataSetChanged();
        scrollToLast();

        StorageHelper.updateChatUser(receiverUser, message.getBody());
        StorageHelper.updateLastChat(message.getBody(), receiverUser.getId(), Integer.parseInt(String.valueOf(message.getProperty("type"))));
    }

    public void showMessage(QBChatMessage message, int type, String urlss) {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

        String url = String.valueOf(message.getProperty("url"));

        Message message1 = new Message();
        message1.setMessage(type == 1 ? message.getBody() : url);
        message1.setType(type);
        message1.setObjectId(message.getId());
        message1.setUserId(user.getId());
        message1.setChatUserIds(receiverUser.getId() + "," + user.getId());
        message1.setCreatedAt(new Date().getTime());
        messageList.add(message1);
        messageAdapter.notifyDataSetChanged();
        scrollToLast();

    }

    public int createVideoMessage(String urls) {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

        Message message1 = new Message();
        message1.setFileUrl(urls);
        message1.setMessage(null);
        message1.setType(SENT_VIDEO);
        message1.setUserId(user.getId());
        message1.setChatUserIds(receiverUser.getId() + "," + user.getId());
        message1.setCreatedAt(new Date().getTime());
        messageList.add(message1);
        messageAdapter.notifyDataSetChanged();
        scrollToLast();

        return messageList.size() - 1;
    }

    ConnectionListener connectionListener = new ConnectionListener() {
        @Override
        public void connected(XMPPConnection connection) {

        }

        @Override
        public void authenticated(XMPPConnection xmppConnection, boolean b) {

        }


        @Override
        public void connectionClosed() {

        }

        @Override
        public void connectionClosedOnError(Exception e) {
            // connection closed on error. It will be established soon
        }

        @Override
        public void reconnectingIn(int seconds) {

        }

        @Override
        public void reconnectionSuccessful() {

        }

        @Override
        public void reconnectionFailed(Exception e) {

        }
    };


    @Override
    public void onLocationChanged(Location location) {

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


    //Quickblox

    private boolean checkIsLoggedInChat() {

        if (!QBChatService.getInstance().isLoggedIn()) {
            startLoginService();
//            Toast.makeText(getApplicationContext(), getResources().getString(R.string.still_connected), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void startLoginService() {
        if (sharedPrefsHelper.hasQbUser()) {
            QBUser qbUser = sharedPrefsHelper.getQbUser();
            LoginService.start(this, qbUser);
        }
    }

    //******************* START CALL from QB SDK **********************//
    private void startCall(boolean isVideoCall) {
        if (receiverUser.getChatId() != null) {

            ArrayList<Integer> opponentsList = new ArrayList<>();
            opponentsList.add(Integer.valueOf(receiverUser.getChatId()));

            QBRTCTypes.QBConferenceType conferenceType = isVideoCall
                    ? QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO
                    : QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_AUDIO;

            QBRTCClient qbrtcClient = QBRTCClient.getInstance(getApplicationContext());
            QBRTCSession newQbRtcSession = qbrtcClient.createNewSessionWithOpponents(opponentsList, conferenceType);
            WebRtcSessionManager.getInstance(this).setCurrentSession(newQbRtcSession);
            String newSessionID = newQbRtcSession.getSessionID();
            //******************* send push to user **********************/
            PushNotificationSender.sendPushMessage(opponentsList, newSessionID, ProfileHelper.getAccount(getApplicationContext()).getName(), "1");

            PrefUtil.writePreferenceValue(getApplicationContext(), "call_user_name", receiverUser.getName());
            CallActivity.start(this, false);

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Clear the Activity's bundle of the subsidiary fragments' bundles.
        outState.clear();
    }


    private final BroadcastReceiver mStatus = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        }
    };

    @Override
    public void onItemClick(View view, int position, String path, Message message, int type) {

        if (type == SENT_IMAGE) {
            Intent intent = new Intent(ChatActivityNew.this, ViewImageActivity.class);
            intent.putExtra("URL", APIClient.imageUrl + message.getMessage());
            intent.putExtra("qa", true);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else if (type == SENT_LOCATION) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(path));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else if (type == SENT_VIDEO) {
            Intent intent = new Intent(ChatActivityNew.this, VideoViewActivity.class);
            intent.putExtra("path", APIClient.imageUrl + message.getMessage());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
//            Intent browserIntent = new Intent(ChatActivityNew.this, PDFViewerActivity.class);
//            browserIntent.putExtra("file_uri", path);
//            browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(browserIntent);

//            String pdf_url = APIClient.baseUrl + "Media/PDF/Lab_Reports/" +labReport.extra1;

            Intent browserIntent = new Intent(Intent.ACTION_VIEW);
            browserIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            browserIntent.setClipData(ClipData.newRawUri("", Uri.parse(path)));
            browserIntent.setDataAndType(Uri.parse(path), "application/pdf");
            browserIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION |  Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            startActivity(browserIntent);
        }
    }

    @Override
    public void onDeleteItem(Message message) {

        Iterator<Message> it = messageList.iterator();

        while (it.hasNext()) {
            Message msg = it.next();
            if (msg.getObjectId() != null) {
                if (msg.getObjectId().equals(message.getObjectId())) {
                    it.remove();
                }
            }
        }

        messageAdapter.notifyDataSetChanged();
        deleteChatMessage(message.getObjectId());
    }

    @Override
    public void processMessageDelivered(String messageId, String dialogId, Integer userId) {

    }

    @Override
    public void processMessageRead(String messageId, String dialogId, Integer userId) {
        updateStatusRead(messageId, userId);
    }

    @Override
    public void processMessage(Chat chat, org.jivesoftware.smack.packet.Message message) {

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v("qqq", "Permission: " + permissions[0] + "was " + grantResults[0]);
            //resume tasks needing this permission
            if (requestCode == 303) {
                startCurrentLocationUpdates();
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

        checkIsLoggedInChat();

        ChatHelper.getInstance().addConnectionListener(chatConnectionListener);
        messageStatusesManager = ChatHelper.getInstance().getQbChatService().getMessageStatusesManager();
        if (messageStatusesManager != null)
            messageStatusesManager.addMessageStatusListener(messageStatusListener);

        StorageHelper.removeMessage(Integer.parseInt(receiverUser.getChatId()));

        //******************** remove notification bubble *******************/
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (getUnreadMessageCount() > 0) {
            notificationManager.cancel(101);
            ShortcutBadger.removeCount(getApplicationContext()); //for 1.1.4+
            PrefUtil.writeIntValue(this, Consts.SHORTCUT_BADGER_COUNTER_CHAT_MSG, 0);
        } else {
            notificationManager.cancelAll();
        }

        startCurrentLocationUpdates();

        //******************** again connect to qb after comes into foreground *******************/
        if (isDialogJoin) {

            ArrayList<Integer> occupantIdsList = new ArrayList<Integer>();
            occupantIdsList.add(PrefUtil.getInteger(getApplicationContext(), "r_id"));

            QBChatDialog dialogNew = new QBChatDialog();
            dialogNew.setName("Chat with " + receiverUser.getFullname());
            dialogNew.setPhoto("1786");
            dialogNew.setType(QBDialogType.PRIVATE);
            dialogNew.setOccupantsIds(occupantIdsList);

            QBChatDialog dialog = DialogUtils.buildDialog("Chat with " + receiverUser.getFullname(), QBDialogType.PRIVATE, occupantIdsList);


            if (qbChatDialog != null) {
                loginQbUser();
            }
        }


        if (qbChatDialog != null) {
            markRead(qbChatDialog.getDialogId());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        locationManager.removeUpdates(mLocationListener);

        ChatHelper.getInstance().removeConnectionListener(chatConnectionListener);
        if (messageStatusesManager != null)
            messageStatusesManager.removeMessageStatusListener(this);


        KeyboardHelper.hideSoftKeyboard(getApplicationContext(), etMessage);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


        if (messageStatusesManager != null)
            messageStatusesManager.removeMessageStatusListener(this);

        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mStatus);

        StorageHelper.saveLastChat(messageList, user.getId(), receiverUser.getId());

        PrefUtil.writePreferenceValue(getApplicationContext(), "ACTIVE_USER", "xxx");

        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                seekHandler.removeCallbacksAndMessages(null);
                mediaPlayer.stop();
                mediaPlayer.release();
            }
        }


        isActivityOpen = false;
        PrefUtil.writeBooleanValue(getApplicationContext(), "is_chat_open", false);

        //******************** save last consultant name to show in 24 hours rating *******************/
        if (!messageList.isEmpty())
            StorageHelper.saveLastChattingConsultant(receiverUser.getName());

    }

    //save Chat Message

    private void saveMessage(String message, int type, Uri fileUri, File file) {

        if (type != SENT_TEXT && type != SENT_VIDEO)
            loader.setVisibility(View.VISIBLE);

        final MultipartBody.Part msg = MultipartBody.Part.createFormData("message", message);
        final MultipartBody.Part typeId = MultipartBody.Part.createFormData("type_id", String.valueOf(type));
        final MultipartBody.Part status = MultipartBody.Part.createFormData("status", "1");
        final MultipartBody.Part receiverId = MultipartBody.Part.createFormData("receiver_id", String.valueOf(receiverUser.getId()));

        MultipartBody.Part fileBody = null;

        if (file != null) {

            String mimeType = URLConnection.guessContentTypeFromName(file.getName());

            RequestBody requestFile =
                    RequestBody.create(
                            file,
                            MediaType.parse(type == SENT_AUDIO ? ".m4a" : mimeType)
                    );

            fileBody = MultipartBody.Part.createFormData("file", type == SENT_AUDIO ? "abc.m4a" : file.getName(), requestFile);
        }


        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<Message> call = apiInterface.sendMessage("Bearer " + TokenHelper.getToken(), msg, typeId, status, receiverId, fileBody);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {

                loader.setVisibility(View.GONE);

                try {

                    if (response.body() != null) {
                        if (type != SENT_TEXT && type != SENT_LOCATION) {
                            if (type == SENT_VIDEO) {
                                assert file != null;
                                setUploadVideoPath(file.getName(), response.body().getMessage());
                            }
                            sendFileMessage(type, response.body().getMessage());
                        }
                        StorageHelper.updateLastChat(response.body().getMessage(), receiverUser.getId(), type);
                    }

                    if (type == SENT_FILE) {
                        deleteTempFile();
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                loader.setVisibility(View.GONE);
            }
        });


    }

    private void getUserByChatId() {
        loader.setVisibility(View.VISIBLE);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<User> call = apiInterface.getUserByChatId("Bearer " + TokenHelper.getToken(), Integer.parseInt(receiverUser.getChatId()));
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                try {

                    if (response.body() != null) {

                        receiverUser.setId(response.body().getId());
                        receiverUser.setOnline(response.body().isOnline());
                        receiverUser.setPhotoUrl(response.body().getPhotoUrl());
                        receiverUser.setRoleId(response.body().getRoleId());
                        receiverUser.setName(response.body().getName());
                        receiverUser.setPhotoUrl(response.body().getPhotoUrl());
                        receiverUser.setFullname(response.body().getName());
                        PrefUtil.writeIntValue(getApplicationContext(), "USER_ID", receiverUser.getId());

                        setOnlineState();

                        getUserPhoto();
                        if (!isChatBot)
                            loginQbUser();

                    }

                } catch (Exception ex) {
                }

                loader.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                loader.setVisibility(View.GONE);
            }
        });
    }


    public static MultipartBody.Part prepareFilePart(String partName, String fileUri) {
        File file = new File(fileUri);
        String mimeType = URLConnection.guessContentTypeFromName(file.getName());
        RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), file);
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
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

    private String getType(int type) {

        if (type == SENT_AUDIO)
            return "رسالة صوت";
        else if (type == SENT_VIDEO)
            return "رسالة فيديو";
        else if (type == SENT_IMAGE)
            return "رسالة صور";
        else if (type == SENT_FILE)
            return "رسالة ملف";

        return " رسالة موقع";
    }

    private void setUploadVideoPath(String path, String url) {

        for (int i = 0; i < messageList.size(); i++) {
            if (messageList.get(i).getType() == SENT_VIDEO) {
                if (messageList.get(i).getFileUrl() != null) {
                    if (messageList.get(i).getFileUrl().contains(path)) {
                        messageList.get(i).setMessage(url);
                        messageList.get(i).setFileUrl(url);

                        messageAdapter.notifyItemChanged(i);
                        break;
                    }
                }
            }
        }
    }

    private void setOnlineState() {
        //************************** check for user online and offline ***********************//
        if (!receiverUser.isOnline() && receiverUser.isConsultantUser()) {
            tvStatus.setText(getResources().getString(R.string.offline_status));
        } else
            tvStatus.setText(getResources().getString(R.string.online_status));
    }


}

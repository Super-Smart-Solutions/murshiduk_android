package com.saatco.murshadik.adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.RequestOptions;
import com.saatco.murshadik.GlideApp;
import com.saatco.murshadik.Helpers.ProfileHelper;
import com.saatco.murshadik.R;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.model.Message;
import com.saatco.murshadik.model.User;
import com.saatco.murshadik.utils.Util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Guideline;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import me.piruin.quickaction.ActionItem;
import me.piruin.quickaction.QuickAction;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Message> messageList;


    private static final int VIEW_TYPE_MESSAGE = 1;
    private static final int VIEW_TYPE_IMAGE = 4;
    private static final int VIEW_TYPE_AUDIO = 2;
    private static final int VIEW_TYPE_VIDEO = 3;
    private static final int VIEW_TYPE_FILE = 5;
    private static final int VIEW_TYPE_LOCATION = 6;

    private static final int ID_UP = 1;
    private static final int ID_DOWN = 2;

    SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);

    private final MessageAdapter.OnMessageItemClickListener onMessageItemClickListener;
    public boolean isGroup = false;
    public ArrayList<User> members;

    Handler seekHandler;
    Runnable run;

    // MediaPlayer mediaPlayer;

    ArrayList<String> loadedAudios = new ArrayList<>();

    public MessageAdapter(List<Message> messageList, Context context, MessageAdapter.
            OnMessageItemClickListener onMessageItemClickListener, MediaPlayer mediaPlayer, Handler handler, Runnable runnable) {
        setHasStableIds(true);
        this.messageList = messageList;
        this.context = context;
        this.onMessageItemClickListener = onMessageItemClickListener;
        this.seekHandler = handler;
    }

    Context context;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case VIEW_TYPE_MESSAGE:
                return new MessageAdapter.TextViewHolder(inflater.inflate(R.layout.item_text, parent, false));
            case VIEW_TYPE_IMAGE:
            case VIEW_TYPE_LOCATION:
                return new MessageAdapter.ImageViewHolder(inflater.inflate(R.layout.item_image_msg, parent, false));
            case VIEW_TYPE_AUDIO:
                return new MessageAdapter.AudioViewHolder(inflater.inflate(R.layout.item_audio_msg, parent, false));
            case VIEW_TYPE_VIDEO:
                return new MessageAdapter.VideoViewHolder(inflater.inflate(R.layout.item_video_msg, parent, false));
            default:
                return new MessageAdapter.FileViewHolder(inflater.inflate(R.layout.item_file_message, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, int position) {


        int sender_UID = ProfileHelper.getAccount(context).getId();
        Message message = messageList.get(position);

        int from_user_ID = message.getUserId();
        int messageType = message.getType();

        QuickAction.setDefaultColor(ResourcesCompat.getColor(context.getResources(), R.color.black, null));
        QuickAction.setDefaultTextColor(Color.WHITE);

        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Riyadh"));

        if (viewHolder instanceof TextViewHolder) {
            final TextViewHolder textViewHolder = (TextViewHolder) viewHolder;

            textViewHolder.layoutRcvMsg.setVisibility(View.GONE);
            textViewHolder.layoutSendMsg.setVisibility(View.GONE);


            ActionItem copyItem = new ActionItem(ID_DOWN, "نسخ");
            ActionItem deleteItem = new ActionItem(ID_UP, "حذف");

            QuickAction quickAction = new QuickAction(context, QuickAction.HORIZONTAL);
            quickAction.setColorRes(R.color.black);
            quickAction.setTextColorRes(R.color.white);

            quickAction.addActionItem(copyItem, deleteItem);

            if (from_user_ID == sender_UID) {
                textViewHolder.layoutSendMsg.setVisibility(View.VISIBLE);
                textViewHolder.sender_text_message.setText(message.getMessage());


                if (message.getMessage().length() <= 25) {
                    textViewHolder.ll_orientation.setOrientation(LinearLayout.HORIZONTAL);
                } else {
                    textViewHolder.ll_orientation.setOrientation(LinearLayout.VERTICAL);
                }

                if (message.isRead())
                    textViewHolder.msgTickMsg.setImageResource(R.drawable.double_tick);
                else
                    textViewHolder.msgTickMsg.setImageResource(R.drawable.ic_check);

                textViewHolder.tvSendTime.setText(dateFormat.format(new Date(message.getCreatedAt())));

                textViewHolder.layoutSendMsg.setOnClickListener(quickAction::show);

                quickAction.setOnActionItemClickListener(item -> {
                    if (item.getActionId() == ID_DOWN) {
                        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("label", textViewHolder.sender_text_message.getText().toString());
                        clipboard.setPrimaryClip(clip);
                    } else {
                        onMessageItemClickListener.onDeleteItem(message);
                    }
                });
            } else {

                quickAction.remove(ID_UP);

                textViewHolder.layoutRcvMsg.setVisibility(View.VISIBLE);
                textViewHolder.receiver_text_message.setText(message.getMessage());
                textViewHolder.tvTime.setText(dateFormat.format(new Date(message.getCreatedAt())));

                if (message.getMessage().length() <= 25) {
                    textViewHolder.ll_orientation_receiver.setOrientation(LinearLayout.HORIZONTAL);
                } else {
                    textViewHolder.ll_orientation_receiver.setOrientation(LinearLayout.VERTICAL);
                }

                if (isGroup) {
                    textViewHolder.tvGroupUserName.setVisibility(View.VISIBLE);
                    textViewHolder.tvGroupUserName.setText(getMemberName(message.getUserId()));
                }

                textViewHolder.layoutRcvMsg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        quickAction.show(view);
                    }
                });

                quickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
                    @Override
                    public void onItemClick(ActionItem item) {
                        if (item.getActionId() == ID_DOWN) {
                            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("label", textViewHolder.receiver_text_message.getText().toString());
                            clipboard.setPrimaryClip(clip);
                        } else {
                            onMessageItemClickListener.onDeleteItem(message);
                        }
                    }
                });

            }

            long previousTs = 0;
            if (position > 1) {
                Message pm = messageList.get(position - 1);
                previousTs = pm.getCreatedAt();
            }
            setTimeTextVisibility(message.getCreatedAt(), previousTs, textViewHolder.dateText, position);

        } else if (viewHolder instanceof ImageViewHolder) {
            final ImageViewHolder imageViewHolder = (ImageViewHolder) viewHolder;

            imageViewHolder.layoutRcvImg.setVisibility(View.GONE);
            imageViewHolder.layoutSendImg.setVisibility(View.GONE);

            ActionItem deleteItem = new ActionItem(ID_UP, "حذف");

            QuickAction quickAction = new QuickAction(context, QuickAction.HORIZONTAL);
            quickAction.setColorRes(R.color.black);
            quickAction.setTextColorRes(R.color.white);
            quickAction.addActionItem(deleteItem);


            if (messageType == VIEW_TYPE_IMAGE) { // if message type is NON TEXT

                String imgRef = APIClient.imageUrl + message.getMessage();

                if (from_user_ID == sender_UID) {

                    imageViewHolder.tvSendTimeImg.setText(dateFormat.format(new Date(message.getCreatedAt())));
                    imageViewHolder.layoutSendImg.setVisibility(View.VISIBLE);

                    GlideApp.with(context)
                            .load(imgRef)
                            .placeholder(R.drawable.ic_broken_image)
                            .into(imageViewHolder.senderImageMsg);


                    imageViewHolder.senderImageMsg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            onMessageItemClickListener.onItemClick(view, position, imgRef, message, VIEW_TYPE_IMAGE);
                        }
                    });

                    imageViewHolder.senderImageMsg.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            quickAction.show(view);
                            return true;
                        }
                    });

                    quickAction.setOnActionItemClickListener(item -> onMessageItemClickListener.onDeleteItem(message));


                    if (message.isRead())
                        imageViewHolder.msgTickImg.setImageResource(R.drawable.double_tick);
                    else
                        imageViewHolder.msgTickImg.setImageResource(R.drawable.ic_check);

                } else {

                    imageViewHolder.layoutRcvImg.setVisibility(View.VISIBLE);
                    imageViewHolder.tvRcvLocTime.setText(dateFormat.format(new Date(message.getCreatedAt())));

                    if (isGroup) {
                        imageViewHolder.tvGroupUserName.setVisibility(View.VISIBLE);
                        imageViewHolder.tvGroupUserName.setText(getMemberName(message.getUserId()));
                    }

                    GlideApp.with(context)
                            .load(imgRef)
                            .placeholder(R.drawable.ic_broken_image)
                            .into(imageViewHolder.receiverImageMsg);


                    imageViewHolder.receiverImageMsg.setOnClickListener(view -> onMessageItemClickListener.onItemClick(view, position, imgRef, message, VIEW_TYPE_IMAGE));

                    imageViewHolder.receiverImageMsg.setOnLongClickListener(view -> {
                        quickAction.show(view);
                        return true;
                    });

                    quickAction.setOnActionItemClickListener(item -> onMessageItemClickListener.onDeleteItem(message));
                }

                long previousTs = 0;
                if (position > 1) {
                    Message pm = messageList.get(position - 1);
                    previousTs = pm.getCreatedAt();
                }
                setTimeTextVisibility(message.getCreatedAt(), previousTs, imageViewHolder.dateText, position);
            }

            if (messageType == VIEW_TYPE_LOCATION) {

                if (from_user_ID == sender_UID) {

                    imageViewHolder.tvSendTimeImg.setText(dateFormat.format(new Date(message.getCreatedAt())));
                    imageViewHolder.layoutSendImg.setVisibility(View.VISIBLE);

                    imageViewHolder.senderImageMsg.setImageDrawable(null);
                    imageViewHolder.senderImageMsg.setImageResource(R.drawable.map);


                    imageViewHolder.senderImageMsg.setOnClickListener(view -> {

                        String[] coordinates = new String[2];

                        Log.d("MSG_LOC", message.getMessage());

                        if (!message.getMessage().equals("null"))
                            coordinates = message.getMessage().split(",");
                        else {
                            coordinates[0] = "0.0";
                            coordinates[1] = "0.0";
                        }

                        String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?q=%f,%f", Float.parseFloat(coordinates[0]), Float.parseFloat(coordinates[1]));
                        GlideApp.with(context)
                                .load("")
                                .placeholder(R.drawable.map)
                                .into(imageViewHolder.senderImageMsg);
                        onMessageItemClickListener.onItemClick(view, position, uri, message, VIEW_TYPE_LOCATION);


                    });

                    imageViewHolder.senderImageMsg.setOnLongClickListener(view -> {
                        quickAction.show(view);
                        return true;
                    });

                    quickAction.setOnActionItemClickListener(item -> onMessageItemClickListener.onDeleteItem(message));

                    if (message.isRead())
                        imageViewHolder.msgTickImg.setImageResource(R.drawable.double_tick);
                    else
                        imageViewHolder.msgTickImg.setImageResource(R.drawable.ic_check);

                } else {


                    //  String[] coordinates = message.getMessage().split(",");

                    imageViewHolder.layoutRcvImg.setVisibility(View.VISIBLE);
                    imageViewHolder.tvRcvLocTime.setText(dateFormat.format(new Date(message.getCreatedAt())));
                    imageViewHolder.receiverImageMsg.setImageDrawable(null);
                    imageViewHolder.receiverImageMsg.setImageResource(R.drawable.map);

                    if (isGroup) {
                        imageViewHolder.tvGroupUserName.setVisibility(View.VISIBLE);
                        imageViewHolder.tvGroupUserName.setText(getMemberName(message.getUserId()));
                    }

                    imageViewHolder.receiverImageMsg.setOnClickListener(view -> {

                        String[] coordinates = new String[2];

                        if (message.getMessage() != null)
                            coordinates = message.getMessage().split(",");
                        else {
                            coordinates[0] = "0.0";
                            coordinates[1] = "0.0";
                        }

                        String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?q=%f,%f", Float.parseFloat(coordinates[0]), Float.parseFloat(coordinates[1]), "Location");
                        GlideApp.with(context)
                                .load("")
                                .placeholder(R.drawable.map)
                                .into(imageViewHolder.receiverImageMsg);
                        onMessageItemClickListener.onItemClick(view, position, uri, message, VIEW_TYPE_LOCATION);

                    });

                    imageViewHolder.receiverImageMsg.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            quickAction.show(view);
                            return true;
                        }
                    });

                    quickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
                        @Override
                        public void onItemClick(ActionItem item) {
                            onMessageItemClickListener.onDeleteItem(message);
                        }
                    });

                }


            }

            long previousTs = 0;
            if (position > 1) {
                Message pm = messageList.get(position - 1);
                previousTs = pm.getCreatedAt();
            }
            setTimeTextVisibility(message.getCreatedAt(), previousTs, imageViewHolder.dateText, position);

        } else if (viewHolder instanceof AudioViewHolder) {
            final AudioViewHolder audioViewHolder = (AudioViewHolder) viewHolder;

            audioViewHolder.layoutRcvAudio.setVisibility(View.GONE);
            audioViewHolder.layoutSendAudio.setVisibility(View.GONE);

            ActionItem deleteItem = new ActionItem(ID_UP, "حذف");
            QuickAction quickAction = new QuickAction(context, QuickAction.HORIZONTAL);
            quickAction.setColorRes(R.color.black);
            quickAction.setTextColorRes(R.color.white);
            quickAction.addActionItem(deleteItem);

            if (from_user_ID != sender_UID) {
                audioViewHolder.layoutSendAudio.setVisibility(View.VISIBLE);
                playVoiceNote(APIClient.imageUrl + message.getMessage(), audioViewHolder.playAudioSender, audioViewHolder.audioLengthSender, audioViewHolder.audioSeekBarSender, audioViewHolder.audioProgress, position, audioViewHolder);
            } else {
                audioViewHolder.layoutRcvAudio.setVisibility(View.VISIBLE);

                if (isGroup) {
                    audioViewHolder.tvGroupUserName.setVisibility(View.VISIBLE);
                    audioViewHolder.tvGroupUserName.setText(getMemberName(message.getUserId()));
                }

                playVoiceNote(APIClient.imageUrl + message.getMessage(), audioViewHolder.playAudio, audioViewHolder.audioLength, audioViewHolder.audioSeekBar, audioViewHolder.audioProgressReciever, position, audioViewHolder);
            }

            audioViewHolder.layoutRcvAudio.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    quickAction.show(view);
                    return true;
                }
            });

            audioViewHolder.layoutSendAudio.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    quickAction.show(view);
                    return true;
                }
            });

            quickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
                @Override
                public void onItemClick(ActionItem item) {
                    onMessageItemClickListener.onDeleteItem(message);
                }
            });

            long previousTs = 0;
            if (position > 1) {
                Message pm = messageList.get(position - 1);
                previousTs = pm.getCreatedAt();
            }
            setTimeTextVisibility(message.getCreatedAt(), previousTs, audioViewHolder.dateText, position);

        } else if (viewHolder instanceof VideoViewHolder) {

            final VideoViewHolder videoViewHolder = (VideoViewHolder) viewHolder;

            ActionItem deleteItem = new ActionItem(ID_UP, "حذف");
            QuickAction quickAction = new QuickAction(context, QuickAction.HORIZONTAL);
            quickAction.setColorRes(R.color.black);
            quickAction.setTextColorRes(R.color.white);
            quickAction.addActionItem(deleteItem);

            String imgRef = "";

            if (message.getMessage() != null) {
                imgRef = APIClient.imageUrl + message.getMessage();
                videoViewHolder.videoProgress.setVisibility(View.GONE);
                videoViewHolder.btnPlayVideoSender.setVisibility(View.VISIBLE);
            } else {
                videoViewHolder.videoProgress.setVisibility(View.VISIBLE);
                videoViewHolder.btnPlayVideoSender.setVisibility(View.GONE);
            }

            videoViewHolder.layoutRcvVideo.setVisibility(View.GONE);
            videoViewHolder.layoutSendVideo.setVisibility(View.GONE);

            RequestOptions requestOptions = new RequestOptions().override(100, 100);

            if (from_user_ID == sender_UID) {

                videoViewHolder.tvSendTimeVideo.setText(dateFormat.format(new Date(message.getCreatedAt())));

                videoViewHolder.layoutSendVideo.setVisibility(View.VISIBLE);

                GlideApp.with(context)
                        .load(imgRef)
                        .apply(requestOptions)
                        .into(videoViewHolder.videoSendThumb);

                String finalImgRef2 = imgRef;
                videoViewHolder.btnPlayVideoSender.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        onMessageItemClickListener.onItemClick(view, position, finalImgRef2, message, VIEW_TYPE_VIDEO);

                    }
                });

                String finalImgRef3 = imgRef;
                videoViewHolder.layoutSendVideo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        onMessageItemClickListener.onItemClick(view, position, finalImgRef3, message, VIEW_TYPE_VIDEO);

                    }
                });

                videoViewHolder.layoutSendVideo.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        quickAction.show(view);
                        return true;
                    }
                });

                quickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
                    @Override
                    public void onItemClick(ActionItem item) {
                        onMessageItemClickListener.onDeleteItem(message);
                    }
                });

                if (message.isRead())
                    videoViewHolder.msgTickVideo.setImageResource(R.drawable.double_tick);
                else
                    videoViewHolder.msgTickVideo.setImageResource(R.drawable.ic_check);


            } else {

               /* if(!message.getGroupId().equals("")){
                    videoViewHolder.tvGroupUserName.setVisibility(View.VISIBLE);
                    videoViewHolder.tvGroupUserName.setText(message.getSenderFullname());
                }*/


                GlideApp.with(context)
                        .load(imgRef)
                        .apply(requestOptions)
                        .into(videoViewHolder.videoRcvThumb);


                videoViewHolder.layoutRcvVideo.setVisibility(View.VISIBLE);

                if (isGroup) {
                    videoViewHolder.tvGroupUserName.setVisibility(View.VISIBLE);
                    videoViewHolder.tvGroupUserName.setText(getMemberName(message.getUserId()));
                }

                String finalImgRef = imgRef;

                videoViewHolder.btnPlayVideoRcv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onMessageItemClickListener.onItemClick(view, position, finalImgRef, message, VIEW_TYPE_VIDEO);
                    }
                });

                String finalImgRef1 = imgRef;
                videoViewHolder.layoutRcvVideo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onMessageItemClickListener.onItemClick(view, position, finalImgRef1, message, VIEW_TYPE_VIDEO);
                    }
                });

                videoViewHolder.layoutRcvVideo.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        quickAction.show(view);
                        return true;
                    }
                });

                quickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
                    @Override
                    public void onItemClick(ActionItem item) {
                        onMessageItemClickListener.onDeleteItem(message);
                    }
                });

            }

            long previousTs = 0;
            if (position > 1) {
                Message pm = messageList.get(position - 1);
                previousTs = pm.getCreatedAt();
            }
            setTimeTextVisibility(message.getCreatedAt(), previousTs, videoViewHolder.dateText, position);

        } else if (viewHolder instanceof FileViewHolder) {

            final FileViewHolder fileViewHolder = (FileViewHolder) viewHolder;

            fileViewHolder.layoutRcvFile.setVisibility(View.GONE);
            fileViewHolder.layoutSendFile.setVisibility(View.GONE);

            ActionItem deleteItem = new ActionItem(ID_UP, "حذف");
            QuickAction quickAction = new QuickAction(context, QuickAction.HORIZONTAL);
            quickAction.setColorRes(R.color.black);
            quickAction.setTextColorRes(R.color.white);
            quickAction.addActionItem(deleteItem);

            String imgRef = APIClient.imageUrl + message.getMessage();

            if (from_user_ID == sender_UID) {

                fileViewHolder.tvSendFileTime.setText(dateFormat.format(new Date(message.getCreatedAt())));
                fileViewHolder.layoutSendFile.setVisibility(View.VISIBLE);

                if (message.isRead())
                    fileViewHolder.msgTickFile.setImageResource(R.drawable.double_tick);
                else
                    fileViewHolder.msgTickFile.setImageResource(R.drawable.ic_check);


                fileViewHolder.layoutSendFile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        onMessageItemClickListener.onItemClick(view, position, imgRef, message, VIEW_TYPE_FILE);

                    }
                });


                fileViewHolder.layoutSendFile.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        quickAction.show(view);
                        return true;
                    }
                });

                quickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
                    @Override
                    public void onItemClick(ActionItem item) {
                        onMessageItemClickListener.onDeleteItem(message);
                    }
                });

            } else {

                /*if(!message.getGroupId().equals("")){
                    fileViewHolder.tvGroupUserName.setVisibility(View.VISIBLE);
                    fileViewHolder.tvGroupUserName.setText(message.getSenderFullname());
                }*/

                fileViewHolder.layoutRcvFile.setVisibility(View.VISIBLE);
                fileViewHolder.tvRcvFileTime.setText(dateFormat.format(new Date(message.getCreatedAt())));

                if (isGroup) {
                    fileViewHolder.tvGroupUserName.setVisibility(View.VISIBLE);
                    fileViewHolder.tvGroupUserName.setText(getMemberName(message.getUserId()));
                }

                fileViewHolder.layoutRcvFile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        onMessageItemClickListener.onItemClick(view, position, imgRef, message, VIEW_TYPE_FILE);

                    }
                });

                fileViewHolder.layoutRcvFile.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        quickAction.show(view);
                        return true;
                    }
                });

                quickAction.setOnActionItemClickListener(item -> onMessageItemClickListener.onDeleteItem(message));


            }

            long previousTs = 0;
            if (position > 1) {
                Message pm = messageList.get(position - 1);
                previousTs = pm.getCreatedAt();
            }
            setTimeTextVisibility(message.getCreatedAt(), previousTs, fileViewHolder.dateText, position);
        }


    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    // Used to avoid the duplicate items recycling

    @Override
    public long getItemId(int position) {
        return position;
    }

    // Used to avoid the duplicate items recycling
    @Override
    public int getItemViewType(int position) {

        if (messageList.get(position).getType() == 1) {
            return VIEW_TYPE_MESSAGE;
        } else if (messageList.get(position).getType() == 4 || messageList.get(position).getType() == 6) {
            return VIEW_TYPE_IMAGE;
        } else if (messageList.get(position).getType() == 2) {
            return VIEW_TYPE_AUDIO;
        } else if (messageList.get(position).getType() == 3) {
            return VIEW_TYPE_VIDEO;
        } else {
            return VIEW_TYPE_FILE;
        }

    }



    public class VideoViewHolder extends RecyclerView.ViewHolder {

        LinearLayout layoutRcvVideo, layoutSendVideo;
        ImageButton btnPlayVideoRcv, btnPlayVideoSender;
        ImageView videoRcvThumb, videoSendThumb, msgTickVideo;
        TextView tvGroupUserName, tvSendTimeVideo;
        ProgressBar videoProgress;
        public TextView dateText;

        VideoViewHolder(View view) {
            super(view);

            layoutRcvVideo = view.findViewById(R.id.item_r_video);
            layoutSendVideo = view.findViewById(R.id.item_s_video);
            btnPlayVideoRcv = view.findViewById(R.id.btn_play_video_rcv);
            btnPlayVideoSender = view.findViewById(R.id.btn_play_video_send);
            videoRcvThumb = view.findViewById(R.id.videoRcvImage);
            videoSendThumb = view.findViewById(R.id.videoSendImage);
            tvGroupUserName = view.findViewById(R.id.tv_username_group);
            msgTickVideo = view.findViewById(R.id.msg_tick_video);
            tvSendTimeVideo = view.findViewById(R.id.tv_time_send_v);
            videoProgress = view.findViewById(R.id.progress_bar_video);
            dateText = view.findViewById(R.id.dateText);


        }

    }

    public class AudioViewHolder extends RecyclerView.ViewHolder {

        LinearLayout layoutRcvAudio, layoutSendAudio;

        public TextView messageTimeStamp, audioLength, audioLengthSender, tvGroupUserName;
        public View audioContainer;
        public ImageView playAudio, playAudioSender;
        public SeekBar audioSeekBar, audioSeekBarSender;
        public ProgressBar audioProgress, audioProgressReciever;
        public TextView dateText;

        AudioViewHolder(View view) {
            super(view);

            layoutRcvAudio = view.findViewById(R.id.layoutAudioReceiver);
            layoutSendAudio = view.findViewById(R.id.layoutAudioSender);
            dateText = view.findViewById(R.id.dateText);
            playAudio = view.findViewById(R.id.playButton);
            playAudioSender = view.findViewById(R.id.playButtonSender);
            audioLength = view.findViewById(R.id.audioLength);
            audioLengthSender = view.findViewById(R.id.audioLengthSender);
            audioSeekBar = view.findViewById(R.id.audioSeekBar);
            audioSeekBarSender = view.findViewById(R.id.audioSeekBarSender);
            audioProgress = view.findViewById(R.id.audioProgress);
            audioProgressReciever = view.findViewById(R.id.audioProgressRcv);
            tvGroupUserName = view.findViewById(R.id.tv_username_group);

        }

    }

    public class RightAudioViewHolder extends RecyclerView.ViewHolder {

        public TextView messageTimeStamp, audioLength;
        public View audioContainer;
        public ImageView playAudio;
        public Guideline rightGuideLine;
        public ProgressBar fileLoadingProgressBar;
        public SeekBar audioSeekBar;
        public TextView dateText;

        public RightAudioViewHolder(View rightAudioMessageView) {
            super(rightAudioMessageView);

            playAudio = rightAudioMessageView.findViewById(R.id.playButton);
            audioLength = rightAudioMessageView.findViewById(R.id.audioLength);
            audioSeekBar = rightAudioMessageView.findViewById(R.id.audioSeekBar);
            dateText = rightAudioMessageView.findViewById(R.id.dateText);

        }
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        LinearLayout layoutRcvImg, layoutSendImg;
        ImageView senderImageMsg, receiverImageMsg, msgTickImg;
        TextView tvGroupUserName, tvSendTimeImg, tvRcvLocTime;
        public TextView dateText;

        ImageViewHolder(View view) {
            super(view);

            tvGroupUserName = view.findViewById(R.id.tv_username_group);
            layoutRcvImg = view.findViewById(R.id.item_img_r_msg);
            layoutSendImg = view.findViewById(R.id.item_img_s_msg);
            senderImageMsg = view.findViewById(R.id.iv_sender_image);
            receiverImageMsg = view.findViewById(R.id.iv_reciever_image);
            msgTickImg = view.findViewById(R.id.msg_tick_img);
            tvSendTimeImg = view.findViewById(R.id.tv_time_send_i);
            tvRcvLocTime = view.findViewById(R.id.tv_time_rcv_loc);
            dateText = view.findViewById(R.id.dateText);


        }

    }

    public class FileViewHolder extends RecyclerView.ViewHolder {

        LinearLayout layoutSendFile, layoutRcvFile;
        TextView tvGroupUserName;
        ImageView msgTickFile;
        TextView tvSendFileTime, tvRcvFileTime;
        public TextView dateText;

        FileViewHolder(View view) {
            super(view);

            tvGroupUserName = view.findViewById(R.id.tv_username_group);
            layoutSendFile = view.findViewById(R.id.item_s_file);
            layoutRcvFile = view.findViewById(R.id.item_r_file);
            msgTickFile = view.findViewById(R.id.msg_tick_file);
            tvSendFileTime = view.findViewById(R.id.tv_time_send_file);
            tvRcvFileTime = view.findViewById(R.id.tv_time_rcv_file);
            dateText = view.findViewById(R.id.dateText);

        }

    }

    public class TextViewHolder extends RecyclerView.ViewHolder {

        LinearLayout layoutRcvMsg, layoutSendMsg, ll_orientation, ll_orientation_receiver;
        TextView sender_text_message, receiver_text_message;
        TextView tvGroupUserName;
        ImageView msgTickMsg;
        TextView tvTime, tvSendTime;
        public TextView dateText;

        TextViewHolder(View view) {
            super(view);

            tvGroupUserName = view.findViewById(R.id.tv_username_group);
            layoutRcvMsg = view.findViewById(R.id.item_r_msg);
            layoutSendMsg = view.findViewById(R.id.item_s_msg);
            ll_orientation = view.findViewById(R.id.ll_orientation);
            ll_orientation_receiver = view.findViewById(R.id.ll_orientation_receiver);
            sender_text_message = view.findViewById(R.id.tvSendMessage);
            receiver_text_message = view.findViewById(R.id.tvRecieveMessage);
            msgTickMsg = view.findViewById(R.id.msg_tick);
            tvSendTime = view.findViewById(R.id.tv_time_send);
            tvTime = view.findViewById(R.id.tv_time);
            dateText = view.findViewById(R.id.dateText);

        }

    }

    public interface OnMessageItemClickListener {
        void onItemClick(View view, int position, String path, Message message, int type);

        void onDeleteItem(Message message);
    }

    private void playVoiceNote(String url, ImageView playAudio, TextView audioLength, SeekBar audioSeekBar, ProgressBar audioProgress, int position, AudioViewHolder audioViewHolder) {

        // Initializing MediaPlayer

        MediaPlayer mediaPlayer1 = new MediaPlayer();
        mediaPlayer1.setAudioStreamType(AudioManager.STREAM_MUSIC);
        audioLength.setText("00:00");
        playAudio.setVisibility(View.VISIBLE);
        audioProgress.setVisibility(View.GONE);

        final int[] duration = {0}; //at the top inside Service class

        playAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (url != null) {

                    try {
                        if (url.contains("Media/Chat")) {

                            audioProgress.setVisibility(View.VISIBLE);
                            playAudio.setVisibility(View.GONE);

                            if (duration[0] < 1) {
                                mediaPlayer1.reset();
                                mediaPlayer1.setDataSource(Uri.parse(url).toString());
                                mediaPlayer1.prepareAsync();
                            } else {
                                playAudio.setVisibility(View.VISIBLE);
                                audioSeekBar.setMax(mediaPlayer1.getDuration());
                                audioSeekBar.setTag(position);
                                audioSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                        if (mediaPlayer1 != null && fromUser) {
                                            mediaPlayer1.seekTo(progress);
                                        }
                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {

                                    }

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {

                                    }
                                });


                                ///
                                if (!mediaPlayer1.isPlaying()) {

                                    audioProgress.setVisibility(View.GONE);

                                    mediaPlayer1.start();
                                    playAudio.setImageResource(R.drawable.ic_pause);
                                    playAudio.setEnabled(false);

                                    duration[0] = mediaPlayer1.getDuration();

                                    run = new Runnable() {
                                        @Override
                                        public void run() {
                                            // Updateing SeekBar every 100 miliseconds
                                            if (mediaPlayer1 != null) {

                                                if (mediaPlayer1.isPlaying())
                                                    audioSeekBar.setProgress(mediaPlayer1.getCurrentPosition());
                                                else
                                                    audioSeekBar.setProgress(0);

                                                seekHandler.postDelayed(run, 100);
                                                //For Showing time of audio(inside runnable)
                                                int miliSeconds = mediaPlayer1.getCurrentPosition();
                                                if (miliSeconds != 0) {
                                                    //if audio is playing, showing current time;
                                                    long minutes = TimeUnit.MILLISECONDS.toMinutes(miliSeconds);
                                                    long seconds = TimeUnit.MILLISECONDS.toSeconds(miliSeconds);
                                                    if (minutes == 0) {
                                                        audioLength.setText(calculateDuration(mediaPlayer1.getDuration()));
                                                    } else {
                                                        if (seconds >= 60) {
                                                            long sec = seconds - (minutes * 60);
                                                            audioLength.setText(calculateDuration(mediaPlayer1.getDuration()));
                                                        }
                                                    }
                                                } else {
                                                    //Displaying total time if audio not playing
                                                    int totalTime = mediaPlayer1.getDuration();
                                                    long minutes = TimeUnit.MILLISECONDS.toMinutes(totalTime);
                                                    long seconds = TimeUnit.MILLISECONDS.toSeconds(totalTime);
                                                    if (minutes == 0) {
                                                        audioLength.setText("0:" + seconds);
                                                    } else {
                                                        if (seconds >= 60) {
                                                            long sec = seconds - (minutes * 60);
                                                            audioLength.setText(minutes + ":" + sec);
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                    };
                                    run.run();
                                } else {
                                    mediaPlayer1.pause();
                                    mediaPlayer1.release();
                                    playAudio.setImageResource(R.drawable.ic_play_arrow);
                                    playAudio.setEnabled(true);
                                }
                                ///
                            }
                            mediaPlayer1.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mediaPlayer) {

                                    audioProgress.setVisibility(View.GONE);
                                    playAudio.setVisibility(View.VISIBLE);

                                    audioSeekBar.setMax(mediaPlayer.getDuration());
                                    audioSeekBar.setTag(position);
                                    audioSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                        @Override
                                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                            if (mediaPlayer1 != null && fromUser) {
                                                mediaPlayer1.seekTo(progress);
                                            }
                                        }

                                        @Override
                                        public void onStartTrackingTouch(SeekBar seekBar) {

                                        }

                                        @Override
                                        public void onStopTrackingTouch(SeekBar seekBar) {

                                        }
                                    });


                                    ///
                                    if (!mediaPlayer1.isPlaying()) {

                                        audioProgress.setVisibility(View.GONE);

                                        mediaPlayer1.start();
                                        playAudio.setImageResource(R.drawable.ic_pause);
                                        playAudio.setEnabled(false);

                                        duration[0] = mediaPlayer1.getDuration();

                                        run = new Runnable() {
                                            @Override
                                            public void run() {
                                                // Updateing SeekBar every 100 miliseconds
                                                if (mediaPlayer1 != null) {

                                                    if (mediaPlayer1.isPlaying())
                                                        audioSeekBar.setProgress(mediaPlayer.getCurrentPosition());
                                                    else
                                                        audioSeekBar.setProgress(0);

                                                    seekHandler.postDelayed(run, 100);
                                                    //For Showing time of audio(inside runnable)
                                                    int miliSeconds = mediaPlayer1.getCurrentPosition();
                                                    if (miliSeconds != 0) {
                                                        //if audio is playing, showing current time;
                                                        long minutes = TimeUnit.MILLISECONDS.toMinutes(miliSeconds);
                                                        long seconds = TimeUnit.MILLISECONDS.toSeconds(miliSeconds);
                                                        if (minutes == 0) {
                                                            audioLength.setText(calculateDuration(mediaPlayer1.getDuration()));
                                                        } else {
                                                            if (seconds >= 60) {
                                                                long sec = seconds - (minutes * 60);
                                                                audioLength.setText(calculateDuration(mediaPlayer1.getDuration()));
                                                            }
                                                        }
                                                    } else {
                                                        //Displaying total time if audio not playing
                                                        int totalTime = mediaPlayer1.getDuration();
                                                        long minutes = TimeUnit.MILLISECONDS.toMinutes(totalTime);
                                                        long seconds = TimeUnit.MILLISECONDS.toSeconds(totalTime);
                                                        if (minutes == 0) {
                                                            audioLength.setText("0:" + seconds);
                                                        } else {
                                                            if (seconds >= 60) {
                                                                long sec = seconds - (minutes * 60);
                                                                audioLength.setText(minutes + ":" + sec);
                                                            }
                                                        }
                                                    }
                                                }
                                            }

                                        };
                                        run.run();
                                    } else {
                                        mediaPlayer1.pause();
                                        mediaPlayer1.release();
                                        playAudio.setImageResource(R.drawable.ic_play_arrow);
                                        playAudio.setEnabled(true);
                                    }
                                    ///
                                }
                            });
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    mediaPlayer1.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {

                            playAudio.setImageResource(R.drawable.ic_play_arrow);
                            playAudio.setEnabled(true);

                        }
                    });

                }

            }
        });
    }

    private String calculateDuration(int duration) {
        String finalDuration = "";
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration);
        if (minutes == 0) {
            finalDuration = "0:" + seconds;
        } else {
            if (seconds >= 60) {
                long sec = seconds - (minutes * 60);
                finalDuration = minutes + ":" + sec;
            }
        }
        return finalDuration;
    }

    private boolean isInLoadedAudios(String path) {
        for (String loaded : loadedAudios) {
            if (path.equals(loaded))
                return true;
        }
        return false;
    }


    public void update(String path, String url) {
        int pos = 0;
        for (int i = 0; i < messageList.size(); i++) {
            if (messageList.get(i).getType() == 3) {
                if (messageList.get(i).getFileUrl().equals(path)) {
                    messageList.get(i).setMessage(url);
                    Log.v("V_PATH", messageList.get(i).getMessage());
                    break;
                }
            }
        }
        notifyDataSetChanged();
    }

    public ArrayList<Message> getData() {
        return (ArrayList<Message>) messageList;
    }

    private void setTimeTextVisibility(long currentMessageTime, long previuosMessageTime, TextView timeText, int position) {

        if (previuosMessageTime == 0) {
            timeText.setVisibility(View.VISIBLE);
            timeText.setText(Util.getDateHeader(currentMessageTime));
        } else {
            Calendar cal1 = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            cal1.setTimeInMillis(currentMessageTime);
            cal2.setTimeInMillis(previuosMessageTime);

            boolean sameMonth = cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
            boolean sameDay = cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);

            if (sameDay) {
                timeText.setVisibility(View.GONE);
                timeText.setText("");
            } else {
                timeText.setVisibility(View.VISIBLE);
                timeText.setText(Util.getDateHeader(currentMessageTime).equals(Util.getDateHeader(new Date().getTime())) ? "اليوم" : Util.getDateHeader(currentMessageTime));
            }

        }
    }

    private String getMemberName(int id) {
        for (User member : members) {
            if (member.getId() == id)
                return member.getName();
        }
        return "";
    }


}

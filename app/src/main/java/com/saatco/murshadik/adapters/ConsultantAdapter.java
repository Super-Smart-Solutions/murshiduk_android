package com.saatco.murshadik.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.saatco.murshadik.R;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.db.StorageHelper;
import com.saatco.murshadik.model.User;
import com.saatco.murshadik.utils.MyDiffUtilCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

public class ConsultantAdapter extends RecyclerView.Adapter<ConsultantAdapter.ViewHolder> {

    private ArrayList<User> consultants;
    private final ConsultantAdapter.OnSelectItemClickListener onFavItemClickListener;
    Context context;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();

    SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
    SimpleDateFormat dateFormatter = new SimpleDateFormat("dd, MMM", Locale.ENGLISH);
    SimpleDateFormat fullDateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View layout;
        public TextView tvTitle,tvDesc,tvCount,tvDate;
        public LinearLayout layoutBG;
        public ImageView bg,onlineStatus;

        public ViewHolder(View view) {
            super(view);
            layout = view.findViewById(R.id.layout_package);
            tvTitle = view.findViewById(R.id.tvTitle);
            tvDesc = view.findViewById(R.id.tvDesc);
            layoutBG = view.findViewById(R.id.layoutBG);
            tvCount = view.findViewById(R.id.tvCount);
            onlineStatus = view.findViewById(R.id.onlineStatus);
            tvDate = view.findViewById(R.id.tvDate);
            bg = view.findViewById(R.id.ivBG);
        }
    }

    public ConsultantAdapter(ArrayList<User> list, Context context,ConsultantAdapter.OnSelectItemClickListener listener) {
        consultants = list;
        this.context = context;
        this.onFavItemClickListener = listener;
    }

    @Override
    public ConsultantAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_consultant, parent, false);
        return new ConsultantAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ConsultantAdapter.ViewHolder holder, final int position) {

        final User user = consultants.get(position);

        holder.tvTitle.setText(user.getName());
       // setLastMessage(holder.tvDesc,user.getMessageType(),user.getLastMessage());
        holder.tvDesc.setText(user.getLastMessage());

        String amPm = "AM";

        Calendar cal = Calendar.getInstance();
        cal.setTime(user.getDate());

        dateFormatter.setTimeZone(TimeZone.getTimeZone("Asia/Riyadh"));
        timeFormatter.setTimeZone(TimeZone.getTimeZone("Asia/Riyadh"));

        if(dateFormatter.format(new Date()).equals(dateFormatter.format(user.getDate()))){
            holder.tvDate.setText(timeFormatter.format(user.getDate()) +" ");
        }else{
            holder.tvDate.setText(fullDateFormatter.format(user.getDate()));
        }


        if(user.getMsgCount() > 0){
            holder.tvCount.setVisibility(View.VISIBLE);
            holder.tvCount.setText(String.valueOf(user.getMsgCount()));
        }else{
            holder.tvCount.setVisibility(View.GONE);
        }

        if (user.isOnline()) {
            holder.onlineStatus.setColorFilter(context.getResources().getColor(R.color.colorGreen));
        } else {
            holder.onlineStatus.setColorFilter(context.getResources().getColor(R.color.btn_scroll_icon_color));
        }

        if(user.isConsultantUser())
            holder.onlineStatus.setVisibility(View.VISIBLE);
        else
            holder.onlineStatus.setVisibility(View.GONE);


        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(context);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();


        if(user.getPhotoUrl() != null) {

           holder.setIsRecyclable(false);

            RequestOptions requestOptions = new RequestOptions().override(100, 100).circleCrop();
            Glide.with(context)
                    .load(APIClient.imageUrl + user.getPhotoUrl())
                    .apply(requestOptions)
                    .placeholder(R.drawable.ag_logo)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(holder.bg);


        }else{
            holder.setIsRecyclable(true);
        }

        holder.layoutBG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                user.setMsgCount(0);
                notifyItemChanged(position);

                onFavItemClickListener.onConsultantClick(view,position,user);
            }
        });




    }

    private void setLastMessage(TextView textView,int type,String lastMessage){

        if(type == 1)
            textView.setText(lastMessage);
        else if(type == 2)
            textView.setText("[رسالة صوت]");
        else if(type == 3)
            textView.setText("[رسالة فيديو]");
        else if(type == 4)
            textView.setText("[رسالة صور]");
        else if(type == 5)
            textView.setText("[رسالة ملف]");
        else
            textView.setText("[موقع]");

    }


    public void updateList(ArrayList<User> list){
        consultants = list;
        notifyDataSetChanged();
    }

    /***
     * insert user to the recycle view list
     * @param user new user
     */
    public void insertItem(User user){
        consultants.add(user);
        notifyItemInserted(consultants.size() - 1);
    }

    /***
     * update user to the recycle view list
     * @param user updated user
     * @param at index of recycle view list
     */
    public void updateItem(User user, int at){
        consultants.set(at, user);
        notifyItemChanged(at);
    }

    public void updateSingle(int userId,String status){
        for (User user : consultants) {
            if ( user.getId() == userId) {
                 user.setOnline(status.equals("OnlineStatus_online"));
            }
        }
        notifyItemChanged(getPosition(userId));
    }

    public void updateLastMessage(int userId,String message,boolean isByMe){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss a", Locale.ENGLISH);
        String strDate = dateFormat.format(new Date().getTime());

        int index = 0;
        for (User user : consultants) {
            if (Integer.parseInt(user.getChatId()) == userId) {
                user.setLastMessage(message);
                user.setDateTime(strDate);
               // user.setOnline(true);
                user.setMsgCount(isByMe ? 0 : (user.getMsgCount() + 1));
                consultants.sort(Collections.reverseOrder());
                notifyItemRangeChanged(0, index +1);
                break;
            }
            index++;
        }
    }

    private int getPosition(int chatId){
        int index = 0;
        for(int i=0;i<consultants.size();i++){
            if(consultants.get(i).getId() == chatId){
                index = i;
                break;
            }
        }
        return index;
    }

    @Override
    public int getItemCount() {
        return consultants.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public interface OnSelectItemClickListener {
        void onConsultantClick(View view, int position, User user);
    }

    private int getUnreadMessageCount(int id){
           return StorageHelper.countUsers(id);
    }

    public static boolean isValidContextForGlide(final Context context) {
        if (context == null) {
            return false;
        }
        if (context instanceof Activity) {
            final Activity activity = (Activity) context;
            return !activity.isDestroyed() && !activity.isFinishing();
        }
        return true;
    }

    public void updateUserListItems(ArrayList<User> employees) {
        final MyDiffUtilCallback diffCallback = new MyDiffUtilCallback(this.consultants, employees);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        diffResult.dispatchUpdatesTo(this);
    }

    public ArrayList<User> getData() {
        return consultants;
    }

    public void removeItem(int position) {
        consultants.remove(position);
        notifyItemRemoved(position);
    }

}


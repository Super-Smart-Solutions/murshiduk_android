package com.saatco.murshadik.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.saatco.murshadik.Helpers.ProfileHelper;
import com.saatco.murshadik.R;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.model.Attachment;
import com.saatco.murshadik.model.Question;

import java.util.List;

public class AnswerAdapter extends RecyclerView.Adapter<AnswerAdapter.ViewHolder> {

    private List<Question> questions;
    private final AnswerAdapter.OnSelectItemClickListener onFavItemClickListener;
    Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView,btnUp,btnDown,ivVerified;
        public View layout;
        public TextView tvUser,tvRating,tvDate,tvAnswer;
        public LinearLayout layoutAttachment,layoutRating;
        public FrameLayout layoutBG;

        public ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.ivUser);
            tvUser = view.findViewById(R.id.tvUserName);
            tvRating = view.findViewById(R.id.tvRating);
            tvAnswer = view.findViewById(R.id.tvAnswer);
            tvDate = view.findViewById(R.id.tvDate);
            ivVerified = view.findViewById(R.id.ivVerified);
            btnUp = view.findViewById(R.id.btnUp);
            btnDown = view.findViewById(R.id.btnDown);
            layoutBG = view.findViewById(R.id.layoutBG);
            layoutRating = view.findViewById(R.id.layoutRating);
            layoutAttachment = view.findViewById(R.id.attachmentLayout);
        }
    }

    public AnswerAdapter(List<Question> list, Context context, AnswerAdapter.OnSelectItemClickListener listener) {
        questions = list;
        this.context = context;
        this.onFavItemClickListener = listener;
    }

    @NonNull
    @Override
    public AnswerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_answer, parent, false);
        return new AnswerAdapter.ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final AnswerAdapter.ViewHolder holder, int position) {

        int pos = holder.getAbsoluteAdapterPosition();

        final Question question = questions.get(pos);

        holder.tvUser.setText(question.getCreatedBy());
        holder.tvDate.setText(question.getDate());
        holder.tvAnswer.setText(question.getDescription());
        holder.tvRating.setText(question.getVoteCountString());

        if(question.isVerified())
            holder.ivVerified.setVisibility(View.VISIBLE);
        else
            holder.ivVerified.setVisibility(View.GONE);

        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(context);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();

        if(question.getAvatar() != null) {
            String image = APIClient.imageUrl + question.getAvatar();

            Glide.with(context)
                    .load(image)
                    .circleCrop()
                    .placeholder(circularProgressDrawable)
                    .into(holder.imageView);

        }

        if(question.getAttachments() != null){

            holder.layoutAttachment.removeAllViews();

            LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(150,150);
          //  iv.setLayoutParams(parms);
            parms.setMargins(10, 10, 10, 10);

            for (int i= 0;i < question.getAttachments().size();i++){

                Attachment attachment = question.getAttachments().get(i);
                ImageView imageView = new ImageView(context);
                Glide.with(context).load(APIClient.imageUrl + attachment.getUrl()).into(imageView);
                holder.layoutAttachment.addView(imageView,parms);
                int finalI = i;
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onFavItemClickListener.onImageClick(view, finalI,attachment);
                    }
                });
            }

        }

        holder.btnUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFavItemClickListener.onUpClick(view, pos, question);
            }
        });

        holder.btnDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFavItemClickListener.onDownClick(view, pos, question);
            }
        });

       /* if(question.getUserId() == ProfileHelper.getAccount(App.getInstance()).getId()){
            holder.layoutRating.setVisibility(View.GONE);
        }*/

        if (question.getUserId() == ProfileHelper.getAccount(context).getId()) {
            // holder.layoutRating.setVisibility(View.VISIBLE);
            holder.btnDown.setEnabled(false);
            holder.btnUp.setEnabled(false);
            holder.btnUp.setImageTintList(ColorStateList.valueOf(context.getColor(R.color.light_gray)));
            holder.btnDown.setImageTintList(ColorStateList.valueOf(context.getColor(R.color.light_gray)));

        }
        else{
            holder.btnDown.setEnabled(true);
            holder.btnUp.setEnabled(true);
            holder.btnUp.setImageTintList(ColorStateList.valueOf(context.getColor(R.color.themeColor)));
            holder.btnDown.setImageTintList(ColorStateList.valueOf(context.getColor(R.color.red)));
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateList(List<Question> list) {
        questions = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    public interface OnSelectItemClickListener {
        void onUpClick(View view, int position, Question question);
        void onDownClick(View view, int position, Question question);
        void onImageClick(View view, int position, Attachment attachment);

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


}

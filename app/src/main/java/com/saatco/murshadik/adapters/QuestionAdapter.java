package com.saatco.murshadik.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.saatco.murshadik.Helpers.ProfileHelper;
import com.saatco.murshadik.R;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.model.Question;
import com.saatco.murshadik.utils.Util;

import java.util.List;
import java.util.Locale;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder> {

    private List<Question> questions;
    private final QuestionAdapter.OnSelectItemClickListener onFavItemClickListener;
    Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView, btnUp, btnDown;
        public View layout;
        public TextView tvTitle, tvRating, tvTags, tvDate, tvCreatedBy, tvCategory;
        public LinearLayout layoutBG, layoutRating;

        public ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.ivQuestion);
            tvTitle = view.findViewById(R.id.tvTitle);
            tvRating = view.findViewById(R.id.tvRating);
            tvTags = view.findViewById(R.id.tvTags);
            tvDate = view.findViewById(R.id.tvDate);
            btnUp = view.findViewById(R.id.btnUp);
            tvCreatedBy = view.findViewById(R.id.tvCreatedBy);
            tvCategory = view.findViewById(R.id.tvCategory);
            layoutRating = view.findViewById(R.id.layoutRating);
            btnDown = view.findViewById(R.id.btnDown);
            layoutBG = view.findViewById(R.id.layoutBG);
        }
    }

    public QuestionAdapter(List<Question> list, Context context, QuestionAdapter.OnSelectItemClickListener listener) {
        questions = list;
        this.context = context;
        this.onFavItemClickListener = listener;
    }

    @NonNull
    @Override
    public QuestionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question, parent, false);
        return new QuestionAdapter.ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final QuestionAdapter.ViewHolder holder, int position) {

        int pos = holder.getAbsoluteAdapterPosition();
        final Question question = questions.get(pos);

        holder.tvTitle.setText(question.getTitle());
        holder.tvDate.setText(String.format(Locale.US, question.getDate()));
        holder.tvTags.setText(question.getKeywords());
        holder.tvRating.setText(question.getVoteCountString());
        holder.tvCategory.setText(question.getCategoryId() == 2 ? "النباتات" : "الأمراض");
        holder.tvCreatedBy.setText(question.getCreatedBy());

       /* if(ProfileHelper.getAccount(context) != null) {
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
                holder.btnUp.setImageTintList(ColorStateList.valueOf(context.getColor(R.color.colorPrimary)));
                holder.btnDown.setImageTintList(ColorStateList.valueOf(context.getColor(R.color.colorPrimary)));
            }
        }*/

        if (question.getUserId() == ProfileHelper.getAccount(context).getId()) {
            holder.btnDown.setEnabled(false);
            holder.btnUp.setEnabled(false);
            holder.btnUp.setImageTintList(ColorStateList.valueOf(context.getColor(R.color.light_gray)));
            holder.btnDown.setImageTintList(ColorStateList.valueOf(context.getColor(R.color.light_gray)));
        }

        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(context);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();

        if (!question.getAttachments().isEmpty()) {
            String image = APIClient.imageUrl + question.getAttachments().get(0).getUrl();

            RequestOptions requestOptions = new RequestOptions().override(200, 200).transform(new CenterCrop(), new RoundedCorners(Util.dpToPixels(context, 36)));

            Glide.with(context)
                    .asBitmap()
                    .load(image)
                    .apply(requestOptions)
                    .placeholder(circularProgressDrawable)
                    .into(holder.imageView);
        }

        holder.layoutBG.setOnClickListener(view -> onFavItemClickListener.onItemClick(view, pos, question));

        holder.btnUp.setOnClickListener(view -> onFavItemClickListener.onUpClick(view, pos, question));

        holder.btnDown.setOnClickListener(view -> onFavItemClickListener.onDownClick(view, pos, question));

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
        void onItemClick(View view, int position, Question question);

        void onUpClick(View view, int position, Question question);

        void onDownClick(View view, int position, Question question);

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

}


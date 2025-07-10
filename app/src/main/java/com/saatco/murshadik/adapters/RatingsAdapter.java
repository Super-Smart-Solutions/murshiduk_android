package com.saatco.murshadik.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.saatco.murshadik.R;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.model.Attachment;
import com.saatco.murshadik.model.ConsultantRatings;

import java.util.List;

public class RatingsAdapter extends RecyclerView.Adapter<RatingsAdapter.ViewHolder> {

    private List<ConsultantRatings> ratings;
    private RatingsAdapter.OnSelectItemClickListener onFavItemClickListener;
    Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView,btnUp,btnDown,ivVerified;
        public View layout;
        public TextView tvUser,tvComment;
        public RatingBar ratingBar;

        public ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.ivUser);
            tvUser = view.findViewById(R.id.tvUserName);
            ratingBar = view.findViewById(R.id.rating);
            tvComment = view.findViewById(R.id.tvComment);
        }
    }

    public RatingsAdapter(List<ConsultantRatings> list, Context context) {
        ratings = list;
        this.context = context;
        //this.onFavItemClickListener = listener;
    }

    @Override
    public RatingsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rating, parent, false);
        return new RatingsAdapter.ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final RatingsAdapter.ViewHolder holder, final int position) {

        final ConsultantRatings rating = ratings.get(position);

        holder.tvUser.setText(rating.getName());
        holder.tvComment.setText(rating.getComment());
        holder.ratingBar.setRating((float) rating.getRating());


        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(context);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();

        if(rating.getAvatar() != null) {
            String image = APIClient.imageUrl + rating.getAvatar();

            Glide.with(context)
                    .load(image)
                    .circleCrop()
                    .placeholder(circularProgressDrawable)
                    .into(holder.imageView);

        }


    }

    public void updateList(List<ConsultantRatings> list) {
        ratings = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return ratings.size();
    }

    public interface OnSelectItemClickListener {
        void onRatingClick(View view, int position, Attachment attachment);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


}

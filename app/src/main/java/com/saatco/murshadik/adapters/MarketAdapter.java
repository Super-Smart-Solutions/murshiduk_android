package com.saatco.murshadik.adapters;

import android.content.Context;
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
import com.saatco.murshadik.R;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.databinding.ItemMarketBinding;
import com.saatco.murshadik.model.Market;

import java.util.ArrayList;

public class MarketAdapter extends RecyclerView.Adapter<MarketAdapter.ViewHolder> {

    private ArrayList<Market> markets;
    private final MarketAdapter.OnSelectItemClickListener onFavItemClickListener;
    Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ItemMarketBinding binding;


        public ViewHolder(ItemMarketBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public MarketAdapter(ArrayList<Market> list, Context context, MarketAdapter.OnSelectItemClickListener listener) {
        markets = list;
        this.context = context;
        this.onFavItemClickListener = listener;
    }

    @NonNull
    @Override
    public MarketAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMarketBinding itemView = ItemMarketBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MarketAdapter.ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final MarketAdapter.ViewHolder holder, int position) {

        int pos = holder.getAbsoluteAdapterPosition();
        final Market market = markets.get(pos);

        holder.binding.tvName.setText(market.getName());
        holder.binding.tvLocation.setText(market.getAddress());
        String workingTime = market.getOpenAt() + " - " + market.getCloseAt();
        holder.binding.tvWorkTime.setText(workingTime);
        String openOrClose = market.isOpened()? context.getString(R.string.open) : context.getString(R.string.close);
        holder.binding.tvOpen.setText(openOrClose);
        holder.binding.tvOpen.setTextColor(market.isOpened()? context.getColor(R.color.colorPrimaryTextDark) : context.getColor(R.color.red));

        if(market.getMarketImage() != null){

            CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(context);
            circularProgressDrawable.setStrokeWidth(5f);
            circularProgressDrawable.setCenterRadius(30f);
            circularProgressDrawable.start();

            String image = APIClient.imageUrl + market.getMarketImage();

            RequestOptions requestOptions = new RequestOptions().transform(new RoundedCorners(10));

            Glide.with(context)
                        .asBitmap()
                        .load(image)
                        .apply(requestOptions)
                        .placeholder(circularProgressDrawable)
                        .into(holder.binding.ivMarket);

        }

        holder.binding.layoutUser.setOnClickListener(view -> onFavItemClickListener.onMarketClick(view, pos, market));


    }

    public void updateList(ArrayList<Market> list) {
        markets = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return markets.size();
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
        void onMarketClick(View view, int position, Market market);
    }

}

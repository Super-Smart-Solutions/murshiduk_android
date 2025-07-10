package com.saatco.murshadik.adapters;

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

import com.saatco.murshadik.Helpers.DataHelper;
import com.saatco.murshadik.R;
import com.saatco.murshadik.model.FarmVisit.FarmVisit;
import com.saatco.murshadik.model.GuideNotification;
import com.saatco.murshadik.model.Item;

import java.util.ArrayList;
import java.util.List;

public class FarmVisitRequestAdapter extends RecyclerView.Adapter<FarmVisitRequestAdapter.ViewHolder> {

    private List<FarmVisit> farmVisits;
    private final Context context;
    private final OnClickListener onClickListener;
    private final int[] colors;
    public FarmVisitRequestAdapter(List<FarmVisit> list, Context context, OnClickListener onClickListener) {
        farmVisits = list;
        this.context = context;
        this.onClickListener = onClickListener;

        colors = new int[] {context.getColor(R.color.f_light_brown)
                , context.getColor(R.color.f_light_gray)
                , context.getColor(R.color.f_light_green)
                , context.getColor(R.color.f_light_yellow)};
    }

    @NonNull
    @Override
    public FarmVisitRequestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_farm_visit, parent, false);
        return new FarmVisitRequestAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FarmVisitRequestAdapter.ViewHolder holder, int position) {
        final FarmVisit farmVisit = farmVisits.get(position);
        holder.tv_date.setText(farmVisit.getVisitDateString().split("T")[0]);


        if (farmVisit.getOrderStatus() != null) {
            holder.tv_request_state.setText(farmVisit.getOrderStatus().getDescription());

            int status = farmVisit.getOrderStatus().getStatus();
            ColorStateList csl = ColorStateList.valueOf(colors[status]);

            holder.ll_request_state_bg.setBackgroundTintList(csl.withAlpha(100));
            holder.iv_circle_request_state.setImageTintList(csl);
        }
        holder.tv_city.setText(farmVisit.getCity());
        holder.tv_region.setText(farmVisit.getCity());
        holder.tv_farm_name.setText(farmVisit.getFarmName());
        holder.ll_item_farm_visit.setOnClickListener(v -> {
            this.onClickListener.onClick(v, position, farmVisit);
        });

        String ministryBranch = "فرع الوزاة في " + farmVisit.getRegion();
        holder.tv_ministry_branch.setText(ministryBranch);

    }

    @Override
    public int getItemCount() {
        return farmVisits.size();
    }

    public void update(ArrayList<FarmVisit> farmVisits) {
        this.farmVisits = farmVisits;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_date, tv_request_state, tv_ministry_branch, tv_city, tv_region, tv_farm_name;
        public LinearLayout ll_item_farm_visit, ll_request_state_bg;
        public ImageView iv_circle_request_state;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_request_state = itemView.findViewById(R.id.tv_request_state);
            tv_ministry_branch = itemView.findViewById(R.id.tv_ministry_branch);
            tv_city = itemView.findViewById(R.id.tv_city);
            tv_region = itemView.findViewById(R.id.tv_region);
            tv_farm_name = itemView.findViewById(R.id.tv_farm_name);
            ll_item_farm_visit = itemView.findViewById(R.id.ll_item_farm_visit);
            ll_request_state_bg = itemView.findViewById(R.id.ll_request_state_bg);
            iv_circle_request_state = itemView.findViewById(R.id.iv_circle_request_state);

        }
    }


    public interface OnClickListener {
        void onClick(View view, int position, FarmVisit item);
    }

}

package com.saatco.murshadik.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.saatco.murshadik.ChatMessageHolder;
import com.saatco.murshadik.R;
import com.saatco.murshadik.model.User;

import java.util.ArrayList;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.ViewHolder> {

    Context context;
    private ArrayList<ChatMessageHolder> demoActivityMessageHolderList = null;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View layout;
        public CardView cardviewSendMesssage,cardviewReciveMesssage;
        public TextView textviewMsgSend,textviewSendTime,textviewMsgRecive,textviewReciveTime;

        public ViewHolder(View view) {
            super(view);
            textviewMsgSend = view.findViewById(R.id.textview_msg_send);
            textviewSendTime = view.findViewById(R.id.textview_send_time);
            textviewMsgRecive = view.findViewById(R.id.textview_msg_recive);
            textviewReciveTime = view.findViewById(R.id.textview_recive_time);
            cardviewSendMesssage = view.findViewById(R.id.cardview_send_messsage);
            cardviewReciveMesssage = view.findViewById(R.id.cardview_recive_messsage);
        }
    }

    public ChatMessageAdapter(ArrayList<ChatMessageHolder> demoActivityMessageHolderList, Context context) {
        this.demoActivityMessageHolderList = demoActivityMessageHolderList;
        this.context = context;
    }

    @Override
    public ChatMessageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_message_show, parent, false);
        return new ChatMessageAdapter.ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final ChatMessageAdapter.ViewHolder holder, final int position) {

        if (demoActivityMessageHolderList.get(position).isSentMessage() == true){
            holder.cardviewSendMesssage.setVisibility(View.VISIBLE);
            holder.textviewMsgSend.setText(demoActivityMessageHolderList.get(position).getMessage());
            holder.textviewSendTime.setText(demoActivityMessageHolderList.get(position).getTime());
        }else{
            holder.cardviewReciveMesssage.setVisibility(View.VISIBLE);
            holder.textviewMsgRecive.setText(demoActivityMessageHolderList.get(position).getMessage());
            holder.textviewReciveTime.setText(demoActivityMessageHolderList.get(position).getTime());
        }

        holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount() {
        return demoActivityMessageHolderList.size();
    }

    public interface OnSelectItemClickListener {
        void onConsultantClick(View view, int position, User user);
    }

}

package com.saatco.murshadik.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.saatco.murshadik.R;
import com.saatco.murshadik.model.Item;
import com.saatco.murshadik.model.consultantvideos.CommentOfConsultantVideo;

import java.util.ArrayList;
import java.util.List;

public class CommentVideoClipAdapter extends RecyclerView.Adapter<CommentVideoClipAdapter.ViewHolder> {

    private List<CommentOfConsultantVideo> commentVideoClips;
    private final Context context;
    private final OnReplayItemClickListener onReplayItemClickListener;

    public CommentVideoClipAdapter(List<CommentOfConsultantVideo> list, Context context, OnReplayItemClickListener onReplayItemClickListener) {
        commentVideoClips = list;
        this.context = context;
        this.onReplayItemClickListener = onReplayItemClickListener;
    }

    @NonNull
    @Override
    public CommentVideoClipAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comments_on_consultant_clips, parent, false);
        return new CommentVideoClipAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentVideoClipAdapter.ViewHolder holder, int position) {
        final CommentOfConsultantVideo commentVideoClip = commentVideoClips.get(position);
        holder.tv_date.setText(commentVideoClip.getStringDate().split("T")[0]);
        holder.tv_commenter_name.setText(commentVideoClip.getUser());
        holder.tv_comment.setText(commentVideoClip.getComment());

        holder.btn_replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onReplayItemClickListener.onReplayItemClick(v, position, commentVideoClip);
            }
        });

        if (commentVideoClip.getReply() != null)
            setRecyclerViewAdapter(commentVideoClip.getReply(), holder.rv_nested_comments);

    }

    @Override
    public int getItemCount() {
        return commentVideoClips.size();
    }


    void setRecyclerViewAdapter(ArrayList<CommentOfConsultantVideo> commentVideoClips, RecyclerView rv_comments) {
        rv_comments.setLayoutManager(new LinearLayoutManager(context));
        NestedCommentVideoClipAdapter adapter = new NestedCommentVideoClipAdapter(commentVideoClips, context);
        rv_comments.setAdapter(adapter);
        rv_comments.setItemAnimator(new DefaultItemAnimator());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_commenter_name, tv_comment, tv_date;
        public Button btn_replay;
        public RecyclerView rv_nested_comments;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_commenter_name = itemView.findViewById(R.id.tv_commenter_name_and_comment);
            tv_comment = itemView.findViewById(R.id.tv_comment);
            tv_date = itemView.findViewById(R.id.tv_date);
            btn_replay = itemView.findViewById(R.id.btn_replay);
            rv_nested_comments = itemView.findViewById(R.id.rv_nested_comments);
        }
    }

    public void updateList(List<CommentOfConsultantVideo> list) {
        commentVideoClips = list;
        notifyDataSetChanged();
    }

    public void addMoreData(List<CommentOfConsultantVideo> list){
        commentVideoClips.addAll(list);
        notifyItemRangeChanged(commentVideoClips.size() - list.size(), list.size());
    }

    public interface OnReplayItemClickListener {
        void onReplayItemClick(View view, int position, CommentOfConsultantVideo item);
    }

}

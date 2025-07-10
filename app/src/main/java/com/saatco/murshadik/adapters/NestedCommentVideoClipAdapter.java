package com.saatco.murshadik.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.saatco.murshadik.R;
import com.saatco.murshadik.model.consultantvideos.CommentOfConsultantVideo;
import com.saatco.murshadik.utils.Util;

import java.util.List;

public class NestedCommentVideoClipAdapter extends RecyclerView.Adapter<NestedCommentVideoClipAdapter.ViewHolder> {

    private final List<CommentOfConsultantVideo> commentVideoClips;
    private final Context context;

    public NestedCommentVideoClipAdapter(List<CommentOfConsultantVideo> list, Context context) {
        commentVideoClips = list;
        this.context = context;
    }

    @NonNull
    @Override
    public NestedCommentVideoClipAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comments_on_comments_of_consultant_clips, parent, false);
        return new NestedCommentVideoClipAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NestedCommentVideoClipAdapter.ViewHolder holder, int position) {
        final CommentOfConsultantVideo commentVideoClip = commentVideoClips.get(position);
        holder.tv_date.setText(commentVideoClip.getStringDate().split("T")[0]);
        holder.tv_commenter_name_and_comment.setText(Util.makeBoldAndNormalText(commentVideoClip.getUser(), commentVideoClip.getComment(), true));
//        Util.makeBoldAndNormalText("user name", commentVideoClip.getComment(), holder.tv_commenter_name);
    }

    @Override
    public int getItemCount() {
        return commentVideoClips.size();
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_commenter_name_and_comment, tv_date;
        public Button btn_replay;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_commenter_name_and_comment = itemView.findViewById(R.id.tv_commenter_name_and_comment);
            tv_date = itemView.findViewById(R.id.tv_date);
            btn_replay = itemView.findViewById(R.id.btn_replay);
        }
    }


}

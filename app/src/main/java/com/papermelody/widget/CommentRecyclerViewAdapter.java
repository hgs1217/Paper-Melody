package com.papermelody.widget;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.papermelody.R;
import com.papermelody.activity.OnlineListenActivity;
import com.papermelody.model.Comment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by HgS_1217_ on 2017/5/22.
 */

public class CommentRecyclerViewAdapter extends RecyclerView.Adapter<CommentRecyclerViewAdapter.ViewHolder> {
    /**
     * 用于网络作品显示评论的RecyclerView的Adapter
     */


    private LayoutInflater layoutInflater;
    private Context context;
    private OnItemClickListener onItemClickListener;
    private List<Comment> comments;

    public CommentRecyclerViewAdapter(Context context) {
        this.context = context;
        comments = new ArrayList<>();
        layoutInflater = LayoutInflater.from(context);
    }

    public CommentRecyclerViewAdapter(Context context, List<Comment> comments) {
        this.context = context;
        this.comments = new ArrayList<>(comments);
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(layoutInflater.inflate(R.layout.item_comment, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setView(comments.get(position));
        holder.setContext(context);
        holder.itemView.setOnClickListener((View view) -> {
            int pos = holder.getLayoutPosition();
            onItemClickListener.OnItemClick();
            // TODO:
        });
        holder.setReply(context, comments.get(position));
    }

    @Override
    public int getItemCount() {
        if (comments == null) {
            return 0;
        }
        return comments.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    public interface OnItemClickListener {
        void OnItemClick();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        /* 持有每个item的所有界面元素 */

        @BindView(R.id.item_this_comment_context)
        TextView textComment;
        @BindView(R.id.item_this_user_name)
        TextView textUserName;
        @BindView(R.id.item_this_comment_time)
        TextView textCommentTime;
        @BindView(R.id.reply_this_comment)
        LinearLayout replyButton;
        @BindView(R.id.item_this_comment_context2)
        TextView textComment2;
        @BindView(R.id.item_this_user_name2)
        TextView textUserName2;
        @BindView(R.id.item_this_comment_time2)
        TextView textCommentTine2;
        @BindView(R.id.subComment)
        CardView subComment;

        Context contextViewH = null;
        //@BindView User ICON

        public ViewHolder(View view, Context context) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setReply(Context contextViewH, Comment commentToThisGuy) {
            replyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((OnlineListenActivity) contextViewH).focusOnEdit(commentToThisGuy);
                    //ToastUtil.showShort("REPLY to " + name);
                }
            });
        }

        public void setContext(Context context) {
            contextViewH = context;
        }

        private Comment splitSonCommnet(String[] info) {
            ////// TODO: 2017-6-16 0016 commnet升级！

            String[] commentX = info[2].split("@.+?:\\s?");
            String comment = "";
            for (int i = 0; i < commentX.length; ++i) {
                if (commentX[i] != "")
                    comment = commentX[i];
            }
            Comment res = new Comment(-1, -1, info[0], info[1], comment);
            return res;
        }

        private void setView(Comment comment) {
            String __label = context.getString(R.string.__label);
            String __labelForSplit = context.getString(R.string.__labelForSplit);
            Date date = new Date();
            SimpleDateFormat sDateFormat = new SimpleDateFormat(
                    "yyyy-MM-dd hh:mm:ss", Locale.CHINA);
            Comment replyToThis = null;
            String curComment = comment.getContent();

            if (curComment.contains(__label)) {
                int idx = curComment.indexOf(__label);
                String sub = curComment.substring(idx + __label.length(), curComment.length());
                curComment = curComment.substring(0, idx);
                if (curComment == "") curComment = " ";
                //process sub comment
                Log.d("FILEE", "cur_comment:" + curComment);
                Log.d("FILEE", "parenr_comment:" + sub);

                boolean valid = false;

                if (!sub.contains(__label)) {
                    valid = false;
                    Log.d("FILEE", "NO label??");
                    subComment.setVisibility(View.GONE);
                } else {
                    Log.d("FILEE", "has label");
                    try {
                        Log.d("FILEE", "SPLIT:");
                        // Log.d("FILEE", sub);
                        if (sub.split(__labelForSplit).length < 3) {
                            valid = false;
                            Log.d("FILEE", "LENGTH<3");
                            subComment.setVisibility(View.GONE);
                        } else {
                            valid = true;
                        }
                    } catch (Exception e) {
                        Log.d("FILEE", e.toString());
                        e.printStackTrace();
                        valid = false;
                    }
                }
                if (valid) {
                    String[] infox = sub.split(__labelForSplit);
                    replyToThis = splitSonCommnet(infox);
                    textComment2.setText(replyToThis.getContent());
                    textUserName2.setText(replyToThis.getAuthor());
                    date.setTime(Long.parseLong(replyToThis.getCreateTime()));
                    textCommentTine2.setText(sDateFormat.format(date));
                    subComment.setVisibility(View.VISIBLE);
                } else {
                    subComment.setVisibility(View.GONE);
                }
            }

            if (textUserName2.getText().toString().equals(context.getString(R.string.__label)) ||
                    textUserName2.getText().toString().equals(context.getString(R.string.__labelForSplit)))
                subComment.setVisibility(View.GONE);

            textComment.setText(curComment);
            date.setTime(Long.parseLong(comment.getCreateTime()));
            textCommentTime.setText(sDateFormat.format(date));
            textUserName.setText(comment.getAuthor());
        }
    }
}

package ch.epfl.sweng.team7.hikingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ch.epfl.sweng.team7.network.RawHikeComment;

public class CommentListAdapter extends ArrayAdapter<RawHikeComment> {
    public CommentListAdapter(Context context, List<RawHikeComment> rawHikeComments) {
        super(context, 0, rawHikeComments);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).
                    inflate(R.layout.activity_comment_list_adapter, parent, false);
            ViewHolder holder = new ViewHolder();
            holder.userId = (TextView) convertView.findViewById(R.id.comment_userid);
            holder.comment = (TextView) convertView.findViewById(R.id.comment_display_text);
            convertView.setTag(holder);
            RawHikeComment rawHikeComment = getItem(position);
            holder = (ViewHolder)convertView.getTag();
            long userId = rawHikeComment.getCommentOwnerId();
            String comment = rawHikeComment.getCommentText();
            holder.userId.setText(Long.toString(userId));
            holder.comment.setText(comment);
        }
        return convertView;
    }

    final class ViewHolder {
        public TextView userId;
        public TextView comment;
    }
}


package ch.epfl.sweng.team7.hikingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ch.epfl.sweng.team7.database.HikeComment;

public class CommentListAdapter extends ArrayAdapter<HikeComment> {
    public CommentListAdapter(Context context, List<HikeComment> hikeComments) {
        super(context, 0, hikeComments);
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
            HikeComment hikeComment = getItem(position);
            holder = (ViewHolder)convertView.getTag();
            long userId = hikeComment.getCommentOwnerId();
            String comment = hikeComment.getCommentText();
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


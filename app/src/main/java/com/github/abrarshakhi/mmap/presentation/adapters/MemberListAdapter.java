package com.github.abrarshakhi.mmap.presentation.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.abrarshakhi.mmap.R;
import com.github.abrarshakhi.mmap.domain.model.MessMember;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MemberListAdapter extends ArrayAdapter<MessMember> {

    private final LayoutInflater inflater;
    private OnClickListener listener;

    public MemberListAdapter(@NonNull Context context, @NonNull List<MessMember> objects, OnClickListener listener) {
        super(context, R.layout.member_list, objects);
        inflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    public void setListener(OnClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.member_list, parent, false);

            holder = new ViewHolder();
            holder.ivAvatar = convertView.findViewById(R.id.ivAvatar);
            holder.tvFullName = convertView.findViewById(R.id.tvFullName);
            holder.tvRole = convertView.findViewById(R.id.tvRole);
            holder.tvJoinedAt = convertView.findViewById(R.id.tvJoinedAt);
            holder.llMemberCard = convertView.findViewById(R.id.llMemberCard);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        MessMember member = getItem(position);
        if (member != null) {
            holder.tvFullName.setText(member.fullName);
            holder.tvRole.setText(member.role);

            // Format joined date
            String joinedDate = formatDate(member.joinedAt);
            holder.tvJoinedAt.setText(String.format("Joined: %s", joinedDate));
            holder.llMemberCard.setOnClickListener(v -> listener.onClick(member));
        }

        return convertView;
    }

    private String formatDate(long timestamp) {
        if (timestamp <= 0) return "--:--:----";
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    public interface OnClickListener {
        void onClick(MessMember messMember);
    }

    static class ViewHolder {
        ImageView ivAvatar;
        TextView tvFullName;
        TextView tvRole;
        TextView tvJoinedAt;
        LinearLayout llMemberCard;
    }
}

package com.nazmul.finalyearproject.ViewHolder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nazmul.finalyearproject.R;

public class FriendRequestViewHolder extends RecyclerView.ViewHolder {

    public TextView text_user_email;
    public ImageView btn_accept,btn,btn_decline;


    public FriendRequestViewHolder(@NonNull View itemView) {
        super(itemView);

        text_user_email = itemView.findViewById(R.id.text_user_email);
        btn_accept = itemView.findViewById(R.id.btn_accept);
        btn_decline = itemView.findViewById(R.id.btn_decline);

    }

}

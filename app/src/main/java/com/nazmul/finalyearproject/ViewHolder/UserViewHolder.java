package com.nazmul.finalyearproject.ViewHolder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.nazmul.finalyearproject.Interface.IRecyclerItemClickListener;
import com.nazmul.finalyearproject.R;

public class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView text_user_email;
    IRecyclerItemClickListener iRecyclerItemClickListener;

    public void setiRecyclerItemClickListener(IRecyclerItemClickListener iRecyclerItemClickListener) {
        this.iRecyclerItemClickListener = iRecyclerItemClickListener;
    }

    public UserViewHolder(@NonNull View itemView) {
        super(itemView);

        text_user_email = itemView.findViewById(R.id.text_user_email);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        iRecyclerItemClickListener.onItemClickListener(v,getAdapterPosition());

    }
}

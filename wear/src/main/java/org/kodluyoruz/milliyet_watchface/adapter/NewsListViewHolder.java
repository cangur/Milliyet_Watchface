package org.kodluyoruz.milliyet_watchface.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.kodluyoruz.milliyet_watchface.R;

class NewsListViewHolder extends RecyclerView.ViewHolder {

    public ImageView newsImage;
    public TextView newsTitle;

    public NewsListViewHolder(@NonNull View itemView) {
        super(itemView);

        newsImage = itemView.findViewById(R.id.rv_imageview_image);
        newsTitle = itemView.findViewById(R.id.rv_tv_title);

        Log.d("Adasda", "" + itemView.getTag());


    }

}

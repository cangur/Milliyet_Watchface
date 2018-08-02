package org.kodluyoruz.milliyet_watchface.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.kodluyoruz.milliyet_watchface.NewsDTO;
import org.kodluyoruz.milliyet_watchface.R;
import org.kodluyoruz.milliyet_watchface.ui.NewsDetailActivity;

import java.io.ByteArrayOutputStream;

public class NewsListAdapter extends RecyclerView.Adapter<NewsListViewHolder> {

    NewsDTO[] newsDTO;
    Context mContext;
    int position;


    public NewsListAdapter(NewsDTO[] newsDTO, Context applicationContext) {
        this.newsDTO = newsDTO;
        this.mContext = applicationContext;
    }


    @NonNull
    @Override
    public NewsListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View masterView = LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.newsdetail_adapter_item, viewGroup, false);

        return new NewsListViewHolder(masterView);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsListViewHolder holder, final int position) {
        holder.newsTitle.setText(newsDTO[position].newsTitle);
        holder.newsImage.setImageBitmap(newsDTO[position].newsBitmap);

        holder.newsImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, NewsDetailActivity.class);

                Bitmap bmp = newsDTO[position].newsBitmap;

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                intent.putExtra("title", newsDTO[position].newsTitle);
                intent.putExtra("summary", newsDTO[position].newsSummary);
                intent.putExtra("content", newsDTO[position].newsContent);
                intent.putExtra("id", newsDTO[position].newsID);
                intent.putExtra("bitmap", byteArray);

                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return newsDTO.length;
    }


}

package org.kodluyoruz.milliyet_watchface;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class SliderAdapter extends PagerAdapter {

    Context context;
    NewsDTO newsDTO;
    LayoutInflater layoutInflater;


    public SliderAdapter(Context context, NewsDTO newsDTO) {
        this.context = context;
        this.newsDTO = newsDTO;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (newsDTO != null) {
            //return newsDTO.newsID.size();
        }
        return 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = layoutInflater.inflate(R.layout.image_viewpager_layout, container, false);

        ImageView imageView = itemView.findViewById(R.id.viewPagerItem_image1);


        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }

}

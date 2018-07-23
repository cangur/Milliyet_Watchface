package org.kodluyoruz.milliyet_watchface.ui;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.kodluyoruz.milliyet_watchface.R;


/****************************************
 * Created by ${DERYA_YANAL}            *
 * 10.07.2018.                          *
 ***************************************/
public class SliderAdapter extends PagerAdapter {

    public int[] slide_images = {
            R.drawable.baslik,
            R.drawable.milliyet_background,
            R.drawable.baslik,
            R.drawable.milliyet_background,
            R.drawable.baslik,
            R.drawable.bg
    };
    public String[] slide_headings = {
            "Derya",
            "Ali",
            "Sezgi",
            "Deneme",
            "Yanal",
            "Besiktas"
    };
    public String[] slide_descs = {
            "sdşfsşfşksoidvşsmdims,",
            "şsdkşekopwepıppıpoıepm.x c ",
            "kepwpokmç.öçx.clpwjmç.ç.xzmş.mşili",
            "deryayana deyranşş",
            "fpikowpmcl.mkilk",
            "kşsdmlmdlmfismlkd"
    };
    private Context context;
    private LayoutInflater layoutInflater;

    SliderAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return slide_headings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull final ViewGroup container, final int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert layoutInflater != null;
        View view = layoutInflater.inflate(R.layout.slide_layout, container, false);

        ImageView slideImageView = view.findViewById(R.id.slide_image);
        TextView slideHeading = view.findViewById(R.id.slide_heading);
        TextView slideDescription = view.findViewById(R.id.slide_desc);

        slideImageView.setImageResource(slide_images[position]);
        slideHeading.setText(slide_headings[position]);
        slideDescription.setText(slide_descs[position]);

        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

//                BlankFragment fragment=new BlankFragment();
//                android.app.FragmentManager manager=((Activity) context).getFragmentManager();
//                manager.beginTransaction().replace(R.id.activity_main,fragment).commit();

                Intent i = new Intent(context.getApplicationContext(), DetailActivity.class);
                context.startActivity(i);


            }
        });

        container.addView(view);

        return view;
    }


    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        container.removeView((RelativeLayout) object);
    }
}

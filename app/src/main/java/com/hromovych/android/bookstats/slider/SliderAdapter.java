package com.hromovych.android.bookstats.slider;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.hromovych.android.bookstats.R;

public class SliderAdapter extends PagerAdapter {

    Context mContext;
    LayoutInflater mLayoutInflater;

    public SliderAdapter(Context context) {
        mContext = context;
    }

    public int[] slide_images = {
            R.drawable.slider_book,
            R.drawable.slider,
            R.drawable.edit,
            };

    public int[] slide_heading = {
            R.string.slide1_heading,
            R.string.slide2_heading,
            R.string.slide3_heading,
    };

    public int[] slide_description = {
            R.string.slide1_description,
            R.string.slide2_description,
            R.string.slide3_description,
    };


    @Override
    public int getCount() {
        return slide_heading.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (RelativeLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
        View view = mLayoutInflater.inflate(R.layout.slide_layout, container, false);

        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        TextView heading = (TextView) view.findViewById(R.id.slide_heading);
        TextView description = (TextView) view.findViewById(R.id.slide_desctiption);

        imageView.setBackgroundResource(slide_images[position]);
        heading.setText(slide_heading[position]);
        description.setText(slide_description[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout) object);
    }
}

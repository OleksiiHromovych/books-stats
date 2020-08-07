package com.hromovych.android.bookstats.slider;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.hromovych.android.bookstats.R;

public class IntroSlider extends AppCompatActivity {

    private ViewPager mViewPager;
    private LinearLayout layoutDots;

    private TextView[] mDots;

    private SliderAdapter mSliderAdapter;

    private Button mBackBtn;
    private Button mNextBtn;

    private int currentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider);

        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        layoutDots = (LinearLayout) findViewById(R.id.layoutDots);

        mBackBtn = (Button) findViewById(R.id.button);
        mNextBtn = (Button) findViewById(R.id.button2);

        mSliderAdapter = new SliderAdapter(this);
        mViewPager.setAdapter(mSliderAdapter);

        addDotsIndicator(0);
        mViewPager.addOnPageChangeListener(viewListener);

        mNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPage == 2)
                    finish();
                mViewPager.setCurrentItem(currentPage + 1);
            }
        });

        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(currentPage - 1);

            }
        });
    }

    public void addDotsIndicator(int position) {

        mDots = new TextView[3];
        layoutDots.removeAllViews();
        for (int i = 0; i < mDots.length; i++) {

            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.colorTransparentWhite));

            layoutDots.addView(mDots[i]);
        }

        if (mDots.length > 0) {

            mDots[position].setTextColor(getResources().getColor(R.color.colorAccent));
        }
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);
            currentPage = position;

            if (position == 0) {
                mNextBtn.setEnabled(true);
                mBackBtn.setEnabled(false);
                mBackBtn.setVisibility(View.INVISIBLE);

                mNextBtn.setText(R.string.slide_next_title);
                mBackBtn.setText("");
            } else if (position == mDots.length - 1) {

                mNextBtn.setEnabled(true);
                mBackBtn.setEnabled(true);
                mBackBtn.setVisibility(View.VISIBLE);

                mNextBtn.setText(R.string.slide_finish_title);
                mBackBtn.setText(R.string.slide_back_title);
            } else {

                mNextBtn.setEnabled(true);
                mBackBtn.setEnabled(true);
                mBackBtn.setVisibility(View.VISIBLE);

                mNextBtn.setText(R.string.slide_next_title);
                mBackBtn.setText(R.string.slide_back_title);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}
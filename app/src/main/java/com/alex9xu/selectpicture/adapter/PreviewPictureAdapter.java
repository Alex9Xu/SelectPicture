package com.alex9xu.selectpicture.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.alex9xu.selectpicture.R;
import com.alex9xu.selectpicture.utils.ConstValues;

import java.util.ArrayList;

/**
 * Created by Alex on 2016/5/11
 */
public class PreviewPictureAdapter extends PagerAdapter {
    private static final String TAG = "PreviewPictureAdapter";

    private ArrayList<String> list;

    private Context context;
    private ImageView mImage;
    private ITogglePhotoDetail listener;
    private int mPictureWidth;

    private long mPreClickMillSecond = 0;
    private boolean mPreClicked = false;

    public PreviewPictureAdapter(Context context, ArrayList<String> list, ITogglePhotoDetail listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
        mPictureWidth = ConstValues.UPLOAD_PHOTO_WIDTH;
    }

    public void setList(ArrayList<String> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        if(null == list) {
            return 0;
        } else {
            return list.size();
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }

    @SuppressLint("InflateParams")
    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.widget_preview_picture, null);
        mImage = (ImageView) view.findViewById(R.id.widget_preview_picture_image);
        final ImageView imgLoading = (ImageView) view.findViewById(R.id.widget_preview_picture_img_loading);
        final AnimationDrawable animDrawable = (AnimationDrawable) imgLoading.getDrawable();
        animDrawable.start();

        if(null != list) {
            String url;
            if(list.get(position).contains("http://")) {
                url = list.get(position);
            } else {
                url="file:///" + list.get(position);
            }

            Picasso.with(context).load(url).resize(720, 1280).centerInside()
                    .into(mImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            animDrawable.stop();
                            imgLoading.setVisibility(View.GONE);
                        }
                        @Override
                        public void onError() {
                        }
                    });
        }

        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPreClicked = true;
                long nowClickMillSecond = System.currentTimeMillis();
                if (nowClickMillSecond - mPreClickMillSecond < 500) {
                    if (mPreClicked) {
                        mPreClicked = false;
                        // Do
                        listener.toggle();
                    }
                } else {
                    mPreClicked = false;
                }
                mPreClickMillSecond = nowClickMillSecond;
            }
        });

        view.setTag(position);
        container.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public interface ITogglePhotoDetail {
        void toggle();
    }

    private int mChildCount = 0;

    @Override
    public void notifyDataSetChanged() {
        mChildCount = getCount();
        super.notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object object) {
        if (mChildCount > 0) {
            mChildCount--;
            return POSITION_NONE;
        }
        return super.getItemPosition(object);
    }

}


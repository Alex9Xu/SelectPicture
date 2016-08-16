package com.alex9xu.selectpicture.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alex9xu.selectpicture.R;
import com.alex9xu.selectpicture.adapter.PreviewPictureAdapter;
import com.alex9xu.selectpicture.utils.ConstValues;
import com.alex9xu.selectpicture.utils.StringUtils;

import java.util.ArrayList;

/**
 * Created by Alex on 2016/5/10
 */
public class PreviewPictureActivity extends AppCompatActivity implements PreviewPictureAdapter.ITogglePhotoDetail {
    private static final String TAG = "PreviewPictureActivity";

    public static final String PIC_STR_LIST = "pic_str_list";
    public static final String CUR_PIC_POSITION = "current_picture_position";
    public static final String IS_SHOW_TOP_NUM = "is_show_top_num";
    public static final String IS_SELECT_MODE = "is_select_mode";
    public static final String PIC_SELECTED_STR_LIST = "pic_selected_list";

    private ViewPager mViewPager;
    private PreviewPictureAdapter mAdapter;
    private TextView mViewCount;
    private LinearLayout mLaySelect;
    private Button mButtomSelect;

    private ArrayList<String> mAllPicStrList;
    private ArrayList<String> mSelectedPicStrList;
    private int mCurPostion;
    private boolean mFlagShowPicNum;
    private boolean mIsSelectMode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_picture);

        initViews();
    }

    protected void initViews() {
        initViewItems();
        initData();
        initListener();
    }

    private void initViewItems() {
        mViewPager = (ViewPager) findViewById(R.id.viewpager_photo_background);
        mViewCount = (TextView) findViewById(R.id.textview_widget_photo_view_viewcount);
        mLaySelect = (LinearLayout) findViewById(R.id.preview_pic_lay_select);
        mButtomSelect = (Button) findViewById(R.id.preview_pic_button_check);
    }

    private void initData() {
        // 获取要显示的图片, 本地或网络均可
        mAllPicStrList = getIntent().getStringArrayListExtra(PIC_STR_LIST);
        if(null == mAllPicStrList) {
            mAllPicStrList = new ArrayList<>();
        }
        // 获取起始位置 默认0
        mCurPostion = getIntent().getIntExtra(CUR_PIC_POSITION, 0);
        mViewPager.setCurrentItem(mCurPostion);
        mFlagShowPicNum = getIntent().getBooleanExtra(IS_SHOW_TOP_NUM, true);
        mIsSelectMode = getIntent().getBooleanExtra(IS_SELECT_MODE, false);
        if(mIsSelectMode) {
            mLaySelect.setVisibility(View.VISIBLE);
            mSelectedPicStrList = getIntent().getStringArrayListExtra(PIC_SELECTED_STR_LIST);
        } else {
            mLaySelect.setVisibility(View.GONE);
            mSelectedPicStrList = null;
        }

        // init other stuff
        mAdapter = new PreviewPictureAdapter(this, mAllPicStrList, this);

        mViewPager.setPageMargin(40);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(mCurPostion);
        mViewPager.setBackgroundColor(getResources().getColor(R.color.gray_dark));
        mViewPager.setOffscreenPageLimit(9);  //设置缓存页数
        setViewData(mCurPostion);

//        mViewCount.getBackground().setAlpha(100);
    }

    private void setViewData(int position) {
        if(null == mAllPicStrList || mAllPicStrList.size() < 1) {
            return;
        }
        mCurPostion = position;
        // 设置头部页码
        if (mFlagShowPicNum) {
            mViewCount.setText(position + 1 + "/" + mAllPicStrList.size());
        }

        if(mIsSelectMode && null != mSelectedPicStrList) {
            for(String str : mSelectedPicStrList) {
                if(!StringUtils.isStringEmpty(str) && str.equals(mAllPicStrList.get(position))) {
                    mButtomSelect.setSelected(true);
                } else {
                    mButtomSelect.setSelected(false);
                }
            }
        }
    }

    private void initListener() {
        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mCurPostion = position;
                setViewData(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

        mButtomSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!v.isSelected() && mSelectedPicStrList.size() + 1 > ConstValues.SELECT_PIC_MAX_NUMS) {
                    String strInfo = "Most Select " + ConstValues.SELECT_PIC_MAX_NUMS + " Pics";
                    Toast.makeText(PreviewPictureActivity.this, strInfo, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mSelectedPicStrList.contains(mAllPicStrList.get(mCurPostion))) {
                    mSelectedPicStrList.remove(mAllPicStrList.get(mCurPostion));
                    mButtomSelect.setSelected(false);
                } else {
                    mSelectedPicStrList.add(mAllPicStrList.get(mCurPostion));
                    mButtomSelect.setSelected(true);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putStringArrayListExtra(PIC_SELECTED_STR_LIST, mSelectedPicStrList);

        if (getSupportFragmentManager().findFragmentByTag("replyList") != null) {
            getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager()
                    .findFragmentByTag("replyList")).commit();
        } else {
//            super.onBackPressed();
        }

        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void toggle() {

    }

}

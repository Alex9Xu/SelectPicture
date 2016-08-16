package com.alex9xu.selectpicture.activities;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alex9xu.selectpicture.R;
import com.alex9xu.selectpicture.utils.ImageDealUtils;
import com.alex9xu.selectpicture.utils.ImageSizeUtil;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Alex on 2016/5/9
 */
public class SelectPictureActivity extends AppCompatActivity {
    private static final String TAG = "SelectPictureActivity";
    /**
     * 最多选择图片的个数
     */
    private static int MAX_NUM = 9;
    private static final int TAKE_PICTURE = 550;
    private static final int REQUEST_SELECT_PIC = 11;

    public static final String INTENT_MAX_NUM = "intent_max_num";

    private Activity mActivity;
    private GridView gridview;
    private PictureAdapter mPicGridAdapter;
    private ArrayList<ImageFolder> mDirPaths = new ArrayList<>();

    private ContentResolver mContentResolver;
    private Button btn_select;
    private TextView mTvwSelectedPics;
    private ListView listview;
    private FolderAdapter folderAdapter;
    private ImageFolder imageAll, currentImageFolder;
    private String mSelected;
    private TextView mTvPreview;

    private ArrayList<String> mSelectedPicture = new ArrayList<>();
    private String cameraPath = null;
    public Handler mHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_photo);

        initViews();
    }

    protected void initViews() {
        MAX_NUM = getIntent().getIntExtra(INTENT_MAX_NUM, 9);
        mSelected = getIntent().getStringExtra("selected");
        if (null != mSelected) {
            String[] s = mSelected.split(",");
            for (int i = 0; i < s.length; i++) {
                mSelectedPicture.add(s[i]);
            }
        }

        mActivity = this;
        mContentResolver = getContentResolver();

        initViewItems();
    }

    public void select(View v) {
        if (listview.getVisibility() == View.VISIBLE) {
            hideListAnimation();
        } else {
            listview.setVisibility(View.VISIBLE);
            showListAnimation();
            folderAdapter.notifyDataSetChanged();
        }
    }

    public void showListAnimation() {
        TranslateAnimation ta = new TranslateAnimation(1, 0f, 1, 0f, 1, 1f, 1, 0f);
        ta.setDuration(200);
        listview.startAnimation(ta);
    }

    public void hideListAnimation() {
        TranslateAnimation ta = new TranslateAnimation(1, 0f, 1, 0f, 1, 0f, 1, 1f);
        ta.setDuration(200);
        listview.startAnimation(ta);
        ta.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                listview.setVisibility(View.GONE);
            }
        });
    }

    public void ok(View v) {
        Intent intent = new Intent();
        StringBuffer selected = new StringBuffer();
        for (int i = 0; i < mSelectedPicture.size(); i++) {
            selected.append(mSelectedPicture.get(i));
            selected.append(",");
        }
        if (selected.length() != 0) {
            selected = selected.deleteCharAt(selected.length() - 1);
            String result = selected.toString();
            intent.putExtra("selected", result);
        }
        intent.putExtra("result", "select");
        setResult(RESULT_OK, intent);
        finish();
    }

    private void initFolder() {
        imageAll = new ImageFolder();
        imageAll.setDir(getString(R.string.all_image));
        currentImageFolder = imageAll;
        mDirPaths.add(imageAll);
    }

    private void initViewItems() {
        initFolder();
        mTvwSelectedPics = (TextView) findViewById(R.id.tvw_selected_pics);
        btn_select = (Button) findViewById(R.id.btn_select);
        mTvPreview = (TextView) findViewById(R.id.tv_preview);
        mTvwSelectedPics.setText("0");

        gridview = (GridView) findViewById(R.id.gridview);
        mPicGridAdapter = new PictureAdapter();
        gridview.setAdapter(mPicGridAdapter);

        gridview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    goCamera();
                }
            }
        });

        listview = (ListView) findViewById(R.id.listview);
        folderAdapter = new FolderAdapter();
        listview.setAdapter(folderAdapter);
        listview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentImageFolder = mDirPaths.get(position);
                hideListAnimation();
                mPicGridAdapter.notifyDataSetChanged();
                btn_select.setText(currentImageFolder.getName());
            }
        });
        getThumbnail();

        mTvPreview.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> selectedPicList = new ArrayList<>();
                for (String url : mSelectedPicture) {
                    selectedPicList.add(url);
                }
                toPreviewAct(selectedPicList);
            }
        });
    }

    protected void goCamera() {
        if (mSelectedPicture.size() + 1 > MAX_NUM) {
            String strInfo = "Most Select " + MAX_NUM + " Pics";
            Toast.makeText(SelectPictureActivity.this, strInfo, Toast.LENGTH_SHORT).show();
            return;
        }
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        openCameraIntent.addCategory(Intent.CATEGORY_DEFAULT);
        Uri imageUri = getOutputMediaFileUri();
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(openCameraIntent, TAKE_PICTURE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("filePath", cameraPath);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (TextUtils.isEmpty(cameraPath)) {
            cameraPath = savedInstanceState.getString("filePath");
        }
    }

    protected Uri getOutputMediaFileUri() {
        String mediaStorageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir + File.separator + "IMG_" + timeStamp + ".png");
        cameraPath = mediaFile.getAbsolutePath();
        return Uri.fromFile(mediaFile);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && cameraPath != null) {
            int degree = ImageDealUtils.readPictureDegree(cameraPath);
            if (degree != 0) {
                ImageDealUtils.rotatingImageViewByPath(degree, cameraPath);
            }
            Intent intent = new Intent();
            mSelectedPicture.add(cameraPath);
            StringBuffer selected = new StringBuffer();
            for (int i = 0; i < mSelectedPicture.size(); i++) {
                selected.append(mSelectedPicture.get(i));
                selected.append(",");
            }
            if (selected.length() != 0) {
                selected = selected.deleteCharAt(selected.length() - 1);
                String result = selected.toString();
                intent.putExtra("selected", result);
            }
            intent.putExtra("result", "select");
            setResult(RESULT_OK, intent);
            finish();
        } else if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_SELECT_PIC) {
            if(null != data.getStringArrayListExtra(PreviewPictureActivity.PIC_SELECTED_STR_LIST)) {
                mSelectedPicture = data.getStringArrayListExtra(PreviewPictureActivity.PIC_SELECTED_STR_LIST);
                mPicGridAdapter.notifyDataSetChanged();
            }
        }

    }


    public void back(View v) {
        onBackPressed();
    }

    private HashMap<Integer, View> viewMap = new HashMap<>();

    private void getThumbnail() {
        mDirPaths.clear();
        folderAdapter.notifyDataSetChanged();

        initFolder();
        HashMap<String, Integer> tmpDir = new HashMap<String, Integer>();
        Cursor mCursor = mContentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.ImageColumns.DATA}, "", null,
                MediaStore.MediaColumns.DATE_ADDED + " DESC");
        if(mCursor==null) {
            Toast.makeText(mActivity, getString(R.string.cant_find_picture), Toast.LENGTH_SHORT).show();
            return;
        }
        if (mCursor.moveToFirst()) {
            int _date = mCursor.getColumnIndex(MediaStore.Images.Media.DATA);
            do {
                String path = mCursor.getString(_date);
                imageAll.images.add(new ImageItem(path));
                File parentFile = new File(path).getParentFile();
                if (parentFile == null) {
                    continue;
                }
                ImageFolder imageFolder;
                String dirPath = parentFile.getAbsolutePath();
                if (!tmpDir.containsKey(dirPath)) {
                    imageFolder = new ImageFolder();
                    imageFolder.setDir(dirPath);
                    imageFolder.setFirstImagePath(path);
                    mDirPaths.add(imageFolder);
                    tmpDir.put(dirPath, mDirPaths.indexOf(imageFolder));
                } else {
                    imageFolder = mDirPaths.get(tmpDir.get(dirPath));
                }
                imageFolder.images.add(new ImageItem(path));
            } while (mCursor.moveToNext());
        }
        mCursor.close();
        folderAdapter.notifyDataSetChanged();
        for (int i = 0; i < mDirPaths.size(); i++) {
            ImageFolder f = mDirPaths.get(i);
        }
    }

    private void toPreviewAct(ArrayList<String> selectedPicStrList) {
        ArrayList<String> allPicStrList = new ArrayList<>();
        for(int i=0; i<currentImageFolder.images.size(); i++) {
            allPicStrList.add(currentImageFolder.images.get(i).path);
        }
        Intent previewPicIntent = new Intent(this, PreviewPictureActivity.class);
        previewPicIntent.putStringArrayListExtra(PreviewPictureActivity.PIC_STR_LIST, allPicStrList);
        previewPicIntent.putExtra(PreviewPictureActivity.IS_SELECT_MODE, true);
        previewPicIntent.putStringArrayListExtra(PreviewPictureActivity.PIC_SELECTED_STR_LIST, selectedPicStrList);
        startActivityForResult(previewPicIntent, REQUEST_SELECT_PIC);
    }

    class PictureAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return currentImageFolder.images.size() + 1;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (!viewMap.containsKey(position) || viewMap.get(position) == null) {
                holder = new ViewHolder();
                convertView = View.inflate(mActivity, R.layout.grid_item_picture, null);
                holder.iv = (ImageView) convertView.findViewById(R.id.slelct_picture_img_view);
                holder.checkBox = (Button) convertView.findViewById(R.id.slelct_picture_check_button);
                convertView.setTag(holder);
                viewMap.put(position, convertView);
            } else {
                convertView = viewMap.get(position);
                holder = (ViewHolder) convertView.getTag();
            }

            if (viewMap.size() > 20) {
                synchronized (convertView) {
                    for (int i = 1; i < gridview.getFirstVisiblePosition() - 3; i++) {
                        viewMap.remove(i);
                    }
                    for (int i = gridview.getLastVisiblePosition() + 3; i < getCount(); i++) {
                        viewMap.remove(i);
                    }
                }
            }

            if (position == 0) {
                holder.iv.setImageResource(R.mipmap.icon_camera_red);
                int padding = ImageSizeUtil.dp2px(SelectPictureActivity.this, 40);
                holder.iv.setPadding(padding, padding, padding, padding);
                holder.checkBox.setVisibility(View.INVISIBLE);
            } else {
                position = position - 1;
                holder.checkBox.setVisibility(View.VISIBLE);

                final ImageItem item = currentImageFolder.images.get(position);
                Picasso.with(SelectPictureActivity.this).load("file://" + item.path).placeholder(
                        R.mipmap.ease_chat_image_normal).fit().centerCrop().into(holder.iv);
                boolean isSelected = mSelectedPicture.contains(item.path);
                if (mSelectedPicture.size() > 0) {
                    mTvwSelectedPics.setText(String.valueOf(mSelectedPicture.size()));
                }

                holder.iv.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mSelectedPicture.size() < 1) {
                            return;
                        }
                        ArrayList<String> selectedPicList = new ArrayList<>();
                        for (String url : mSelectedPicture) {
                            selectedPicList.add(url);
                        }
                        toPreviewAct(selectedPicList);
                    }
                });

                holder.checkBox.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!v.isSelected() && mSelectedPicture.size() + 1 > MAX_NUM) {
                            String strInfo = "Most Select " + MAX_NUM + " Pics";
                            Toast.makeText(SelectPictureActivity.this, strInfo, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (mSelectedPicture.contains(item.path)) {
                            mSelectedPicture.remove(item.path);
                        } else {
                            mSelectedPicture.add(item.path);
                        }
                        mTvwSelectedPics.setText(String.valueOf(mSelectedPicture.size()));
                        v.setSelected(mSelectedPicture.contains(item.path));

                    }
                });
                holder.checkBox.setSelected(isSelected);

                final ViewHolder finalHolder = holder;
                convertView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finalHolder.checkBox.performClick();
                    }
                });
            }

            return convertView;
        }
    }

    class ViewHolder {
        ImageView iv;
        Button checkBox;
    }

    class FolderAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mDirPaths.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            FolderViewHolder holder = null;
            if (convertView == null) {
                convertView = View.inflate(mActivity, R.layout.widget_list_dir_item, null);
                holder = new FolderViewHolder();
                holder.id_dir_item_image = (ImageView) convertView.findViewById(R.id.id_dir_item_image);
                holder.id_dir_item_name = (TextView) convertView.findViewById(R.id.id_dir_item_name);
                holder.id_dir_item_count = (TextView) convertView.findViewById(R.id.id_dir_item_count);
                holder.choose = (ImageView) convertView.findViewById(R.id.choose);
                convertView.setTag(holder);
            } else {
                holder = (FolderViewHolder) convertView.getTag();
            }
            ImageFolder item = mDirPaths.get(position);
            Picasso.with(SelectPictureActivity.this).load("file://" + item.getFirstImagePath()).placeholder(
                    R.mipmap.ease_chat_image_normal).fit().centerCrop().into(holder.id_dir_item_image);
            holder.id_dir_item_count.setText(item.images.size() + "Pics");
            holder.id_dir_item_name.setText(item.getName());
            holder.choose.setVisibility(currentImageFolder == item ? View.VISIBLE : View.GONE);
            return convertView;
        }
    }

    class FolderViewHolder {
        ImageView id_dir_item_image;
        ImageView choose;
        TextView id_dir_item_name;
        TextView id_dir_item_count;
    }

    class ImageFolder {
        private String dir;
        private String firstImagePath;
        private String name;
        public List<ImageItem> images = new ArrayList<>();
        public String getDir() {
            return dir;
        }

        public void setDir(String dir) {
            this.dir = dir;
            int lastIndexOf = this.dir.lastIndexOf("/");
            if(lastIndexOf > -1) {
                this.name = this.dir.substring(lastIndexOf);
            }
        }

        public String getFirstImagePath() {
            return firstImagePath;
        }

        public void setFirstImagePath(String firstImagePath) {
            this.firstImagePath = firstImagePath;
        }

        public String getName() {
            return name;
        }

    }

    class ImageItem {
        String path;

        public ImageItem(String p) {
            this.path = p;
        }
    }

}
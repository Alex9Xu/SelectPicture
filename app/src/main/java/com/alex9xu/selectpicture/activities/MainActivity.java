package com.alex9xu.selectpicture.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.alex9xu.selectpicture.R;
import com.alex9xu.selectpicture.utils.StringUtils;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    protected static final int REQUEST_DISPLAY_PIC = 11;
    protected static final int REQUEST_SELECT_PIC = 12;

    private final static int SELECT_PIC_MAX_NUMS = 6;

    private TextView mTvwInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initViewItems();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initViewItems() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Alex9Xu@hotmail.com", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Button btnDisplayPics = (Button) findViewById(R.id.main_btn_display_pics);
        btnDisplayPics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayPics();
            }
        });

        Button btnSelectPics = (Button) findViewById(R.id.main_btn_select_pics);
        btnSelectPics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPics();
            }
        });

        mTvwInfo = (TextView) findViewById(R.id.main_tvw_selected_pics);
    }

    private void displayPics() {
        Intent selectPicIntent = new Intent(this, SelectPictureActivity.class);
        selectPicIntent.putExtra("mode_display", true);
        startActivityForResult(selectPicIntent, REQUEST_DISPLAY_PIC);
    }

    private void selectPics() {
        Intent selectPicIntent = new Intent(this, SelectPictureActivity.class);
        selectPicIntent.putExtra("intent_max_num", SELECT_PIC_MAX_NUMS);
        startActivityForResult(selectPicIntent, REQUEST_SELECT_PIC);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_SELECT_PIC) {
            if (data != null && data.getStringExtra("result").equals("select")) {
                if (data.getStringExtra("selected") != null) {
                    String selected = data.getStringExtra("selected");
                    mTvwInfo.setText("selected = " + selected);
                    List<String> selectedPics = Arrays.asList(selected.split(","));
                    if (selectedPics.size() > 0) {
                        for (String str : selectedPics) {
                            if (!StringUtils.isStringEmpty(str)) {
                                File file = new File(str);
                                Uri uri = Uri.fromFile(file);
                                if (null != uri) {
                                    // Do With Uri
                                }
                            }
                        }
                    }
                }
            }
        }

    }

}

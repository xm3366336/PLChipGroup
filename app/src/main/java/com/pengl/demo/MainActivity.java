package com.pengl.demo;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.pengl.OnChipCheckListener;
import com.pengl.PLChipGroup;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private String[] items = new String[]{"陈赫", "陈坤", "邓超", "特雷-杨", "肯巴-沃克", "吉安尼斯-安特托孔波", "西亚卡姆", "乔尔-恩比德", "杜淳", "冯绍峰", "韩庚", "胡歌", "何炅", "黄渤", "黄晓明", "贾乃亮", "李晨", "李易峰李易峰李易峰李易峰李易峰李易峰李易峰李易峰李易峰李易峰李易峰李易峰李易峰李易峰李易峰李易峰李易峰", "鹿晗", "井柏然", "刘烨", "陆毅"};

    private PLChipGroup mPLChipGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPLChipGroup = findViewById(R.id.mPLChipGroup1);
        mPLChipGroup.setData(items);
        mPLChipGroup.setOnChipCheckListener(new OnChipCheckListener() {
            @Override
            public void onClick(PLChipGroup view, int position, String text) {
                Toast.makeText(MainActivity.this, position + "\n" + text, Toast.LENGTH_SHORT).show();
            }
        });
        mPLChipGroup.show();

        ((MaterialCheckBox) findViewById(R.id.mCheckBox_single)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mPLChipGroup.setSingleSelection(b);
                mPLChipGroup.show();
            }
        });

        findViewById(R.id.btn_show).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, mPLChipGroup.getCheckedValuesToString(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
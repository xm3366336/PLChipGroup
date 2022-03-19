package com.pengl.demo;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.pengl.BeanChipItems;
import com.pengl.OnChipCheckListener;
import com.pengl.PLChipChooseDialog;
import com.pengl.PLChipGroup;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final String[] items = new String[]{
            "陈赫", "陈坤", "邓超", "特雷-杨", "肯巴-沃克", "吉安尼斯-安特托孔波", "西亚卡姆", "乔尔-恩比德",
            "杜淳", "冯绍峰", "韩庚", "胡歌", "何炅", "黄渤", "黄晓明", "贾乃亮", "李晨",
            "李易峰李易峰李易峰李易峰李易峰李易峰李易峰李易峰李易峰李易峰李易峰李易峰李易峰李易峰李易峰李易峰李易峰",
            "鹿晗", "井柏然", "刘烨", "陆毅"};


    private PLChipGroup mPLChipGroup;
    private String checkDatasByDialog;// 已选择的项

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPLChipGroup = findViewById(R.id.mPLChipGroup1);
        mPLChipGroup.setData(items);
        mPLChipGroup.setOnChipCheckListener((PLChipGroup view, int position, String text) ->
                Toast.makeText(MainActivity.this, position + "\n" + text, Toast.LENGTH_SHORT).show());
        mPLChipGroup.show();

        ((MaterialCheckBox) findViewById(R.id.mCheckBox_single)).setOnCheckedChangeListener(
                (CompoundButton compoundButton, boolean b) -> {
                    mPLChipGroup.setSingleSelection(b);
                    mPLChipGroup.show();
                });

        findViewById(R.id.btn_show).setOnClickListener(view ->
                Toast.makeText(MainActivity.this, mPLChipGroup.getCheckedLabelToString(), Toast.LENGTH_LONG).show());

        findViewById(R.id.btn_choose_all).setOnClickListener(v -> mPLChipGroup.setChooseAll());
        findViewById(R.id.btn_clean_all).setOnClickListener(v -> mPLChipGroup.cleanAll());
        findViewById(R.id.btn_clean_id).setOnClickListener(v -> mPLChipGroup.setChoose(0, false));


        ArrayList<BeanChipItems> listData = new ArrayList<>();
        int i = 0;
        for (String item : items) {
            listData.add(new BeanChipItems(item, "携带的对象" + i++));
        }

        findViewById(R.id.btn_dialog_choose_single).setOnClickListener(v -> {
            new PLChipChooseDialog(this)
                    .setTitle("选择一项")
                    .setTitleSub("这是单选")
                    .setSingleSelection(true)
                    .setSingleClickClose(true)
                    .setItems(listData)
                    .setCheckDatas(checkDatasByDialog)
                    .setOnChipChooseListener((checkLabels, checkDatas) -> {
                        checkDatasByDialog = checkDatas;
                        Toast.makeText(MainActivity.this, checkDatasByDialog, Toast.LENGTH_SHORT).show();
                    }).show();
        });

        findViewById(R.id.btn_dialog_choose_multi).setOnClickListener(v -> {
            new PLChipChooseDialog(this)
                    .setTitle("选择一项")
                    .setTitleSub("这是多选")
                    .setSingleSelection(false)
                    .setItems(listData)
                    .setCheckDatas(checkDatasByDialog)
                    .setOnChipChooseListener((checkLabels, checkDatas) -> {
                        checkDatasByDialog = checkDatas;
                        Toast.makeText(MainActivity.this, checkDatasByDialog, Toast.LENGTH_SHORT).show();
                    }).show();
        });
    }
}
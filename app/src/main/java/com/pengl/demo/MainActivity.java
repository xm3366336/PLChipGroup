package com.pengl.demo;

import android.os.Bundle;
import android.widget.CompoundButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.pengl.BeanChipItems;
import com.pengl.PLChipChooseDialog;
import com.pengl.PLChipGroup;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final String[] items = new String[]{
            "陈赫", "陈坤", "邓超", "特雷-杨", "肯巴-沃克", "吉安尼斯-安特托孔波", "西亚卡姆", "乔尔-恩比德",
            "杜淳", "冯绍峰", "韩庚", "胡歌", "何炅", "黄渤", "黄晓明", "贾乃亮", "李晨",
            "李易峰李易峰李易峰李易峰李易峰李易峰李易峰李易峰李易峰李易峰李易峰李易峰李易峰李易峰李易峰李易峰ABC",
            "鹿晗", "井柏然", "刘烨", "陆毅"};


    private PLChipGroup mPLChipGroup;
    private AppCompatTextView tvLog;
    private String checkDatasByDialog;// 已选择的项

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvLog = findViewById(R.id.tvLog);
        mPLChipGroup = findViewById(R.id.mPLChipGroup1);
        mPLChipGroup.setData(items);
        mPLChipGroup.setOnChipCheckListener((view, isCheck, position, mBeanChipItems) -> {
            if (isCheck) {
                tvLog.setText("选中：[" + position + "]" + mBeanChipItems.getLabel());
            } else {
                tvLog.setText("取消：[" + position + "]" + mBeanChipItems.getLabel());
            }
        });
        mPLChipGroup.show();

        ((MaterialCheckBox) findViewById(R.id.mCheckBox_single)).setOnCheckedChangeListener(
                (CompoundButton compoundButton, boolean b) -> {
                    mPLChipGroup.setSingleSelection(b);
                    mPLChipGroup.show();
                });

        ((MaterialCheckBox) findViewById(R.id.mCheckBox_disable)).setOnCheckedChangeListener(
                (CompoundButton compoundButton, boolean b) -> mPLChipGroup.setDisableCheck(b));

        findViewById(R.id.btn_show).setOnClickListener(view -> tvLog.setText(mPLChipGroup.getCheckedLabelToString()));
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
                    .setBgRounded(16)
                    .setItems(listData)
                    .setCheckDatas(checkDatasByDialog)
                    .setOnChipChooseListener((checkLabels, checkDatas) -> {
                        checkDatasByDialog = checkDatas;
                        tvLog.setText(checkDatasByDialog);
                    }).show();
        });

        findViewById(R.id.btn_dialog_choose_multi).setOnClickListener(v -> {
            new PLChipChooseDialog(this)
                    .setTitle("选择一项")
                    .setTitleSub("这是多选")
                    .setSingleSelection(false)
                    .setBgRounded(16)
                    .setItems(listData)
                    .setCheckDatas(checkDatasByDialog)
                    .setOnChipChooseListener((checkLabels, checkDatas) -> {
                        checkDatasByDialog = checkDatas;
                        tvLog.setText(checkDatasByDialog);
                    }).show();
        });
    }
}
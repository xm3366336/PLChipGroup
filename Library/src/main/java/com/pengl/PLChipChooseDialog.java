package com.pengl;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.pengl.plchipgroup.R;

import java.util.ArrayList;

public class PLChipChooseDialog extends BottomSheetDialog {

    private final AppCompatTextView tv_title, tv_title_sub;
    private final PLChipGroup mPLChipGroup;
    private final AppCompatButton btn_confirm, btn_cancel, btn_reset;
    private OnChipChooseListener mOnChipChooseListener;

    public interface OnChipChooseListener {
        void onChipClickOK(String checkLabels, String checkDatas);
    }

    public PLChipChooseDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.pl_chip_choose_dialog);
        tv_title = findViewById(R.id.tv_title);
        tv_title_sub = findViewById(R.id.tv_title_sub);
        mPLChipGroup = findViewById(R.id.mPLChipGroup);
        btn_confirm = findViewById(R.id.btn_confirm);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_reset = findViewById(R.id.btn_reset);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        btn_confirm.setOnClickListener(v -> onClickOK());
        btn_cancel.setOnClickListener(v -> dismiss());
        btn_reset.setOnClickListener(v -> {
            if (mPLChipGroup.isSingleSelection())
                return;
            mPLChipGroup.cleanAll();
        });
    }

    private void onClickOK() {
        if (null != mOnChipChooseListener) {
            mOnChipChooseListener.onChipClickOK(mPLChipGroup.getCheckedLabelToString(), mPLChipGroup.getCheckedDataToString());
        }
        dismiss();
    }

    public PLChipGroup getPLChipGroup() {
        return mPLChipGroup;
    }

    public PLChipChooseDialog setTitle(String title) {
        tv_title.setText(title);
        return this;
    }

    public PLChipChooseDialog setTitleSub(String titleSub) {
        tv_title_sub.setText(titleSub);
        return this;
    }

    /**
     * 设置选择模式
     *
     * @param singleSelection true单选模式，false多选模式
     */
    public PLChipChooseDialog setSingleSelection(boolean singleSelection) {
        mPLChipGroup.setSingleSelection(singleSelection);
        btn_reset.setVisibility(singleSelection ? View.GONE : View.VISIBLE);
        return this;
    }

    /**
     * 单选模式下，选择一项后，是否直接回调并退出
     *
     * @param isSingleClickClose true是的
     * @return this
     */
    public PLChipChooseDialog setSingleClickClose(boolean isSingleClickClose) {
        if (isSingleClickClose) {
            mPLChipGroup.setOnChipCheckListener((view, position, text) -> onClickOK());
            btn_confirm.setVisibility(View.GONE);
            btn_cancel.setVisibility(View.GONE);
        } else {
            btn_confirm.setVisibility(View.VISIBLE);
            btn_cancel.setVisibility(View.VISIBLE);
        }
        return this;
    }

    public PLChipChooseDialog setItems(ArrayList<BeanChipItems> items) {
        mPLChipGroup.setData(items);
        return this;
    }

    /**
     * 设置已选中的项
     *
     * @param checkPositions 默认选中的position，多个以逗号分隔。若空，则默认0，即第1个
     */
    public PLChipChooseDialog setCheckPosition(String checkPositions) {
        mPLChipGroup.setCheckPosition(checkPositions);
        return this;
    }

    /**
     * 设置已选中的项
     *
     * @param checkDatas 选中的值，以值来反推position
     */
    public PLChipChooseDialog setCheckDatas(String checkDatas) {
        mPLChipGroup.setCheckDatas(checkDatas);
        return this;
    }

    public PLChipChooseDialog setOnChipChooseListener(OnChipChooseListener mOnChipChooseListener) {
        this.mOnChipChooseListener = mOnChipChooseListener;
        return this;
    }

    @Override
    public void show() {
        mPLChipGroup.show();
        super.show();
    }
}

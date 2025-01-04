package com.pengl;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.ShapeAppearanceModel;
import com.pengl.plchipgroup.R;

import java.util.ArrayList;

public class PLChipChooseDialog extends Dialog {

    private final AppCompatTextView tv_title, tv_title_sub;
    private final PLChipGroup mPLChipGroup;
    private final AppCompatButton btn_confirm, btn_cancel, btn_reset;
    private final ShapeableImageView bg;
    private OnChipChooseListener mOnChipChooseListener;

    private final ShowType showType;

    public enum ShowType {
        bottom, center
    }

    public interface OnChipChooseListener {
        void onChipClickOK(String checkLabels, String checkDatas);
    }

    public PLChipChooseDialog(@NonNull Context context) {
        this(context, ShowType.bottom);
    }

    public PLChipChooseDialog(@NonNull Context context, ShowType showType) {
        super(context, R.style.PLCG_Dialog);
        this.showType = showType;
        setContentView(R.layout.plcg_choose_dialog);
        tv_title = findViewById(R.id.tv_title);
        tv_title_sub = findViewById(R.id.tv_title_sub);
        mPLChipGroup = findViewById(R.id.mPLChipGroup);
        btn_confirm = findViewById(R.id.btn_confirm);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_reset = findViewById(R.id.btn_reset);
        bg = findViewById(R.id.bg);
        setBgRounded(false, context.getResources().getDimensionPixelSize(R.dimen.plcg_dp_8));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (showType == ShowType.center) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N && null != getWindow()) {
                getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                getWindow().setGravity(Gravity.CENTER);
            }
        } else {// 默认底部
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            lp.gravity = Gravity.BOTTOM;
            getWindow().setAttributes(lp);
            getWindow().setWindowAnimations(R.style.PLCG_AnimBottomIn);
        }

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
            mPLChipGroup.setOnChipCheckListener((view, isCheck, position, mBeanChipItems) -> onClickOK());
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

    /**
     * 设置圆角的大小
     *
     * @param isDip      是否为dip
     * @param cornerSize 默认是4dip，单位dip
     */
    public PLChipChooseDialog setBgRounded(boolean isDip, int cornerSize) {
        float px = TypedValue.applyDimension(isDip ? TypedValue.COMPLEX_UNIT_DIP : TypedValue.COMPLEX_UNIT_PX, cornerSize,
                getContext().getResources().getDisplayMetrics());
        if (showType == ShowType.center) {
            bg.setShapeAppearanceModel(ShapeAppearanceModel.builder()
                    .setAllCorners(CornerFamily.ROUNDED, Math.max(px, 0))
                    .build());
        } else {
            bg.setShapeAppearanceModel(ShapeAppearanceModel.builder()
                    .setTopLeftCorner(CornerFamily.ROUNDED, Math.max(px, 0))
                    .setTopRightCorner(CornerFamily.ROUNDED, Math.max(px, 0))
                    .build());
        }
        return this;
    }

    public ShapeableImageView getBg() {
        return bg;
    }

    @Override
    public void show() {
        mPLChipGroup.show();
        super.show();
    }
}

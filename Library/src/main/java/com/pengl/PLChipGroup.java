package com.pengl;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.Dimension;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.shape.ShapeAppearanceModel;
import com.pengl.plchipgroup.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class PLChipGroup extends FrameLayout {

    private final String TAG = PLChipGroup.class.getSimpleName();

    private final ChipGroup mChipGroup;
    private OnChipCheckListener mOnChipCheckListener;

    /**
     * 最多显示多少条，默认0，小于=0时，不限制。如果超过xx条，则显示：收缩和展开
     */
    private int maxCount;

    private final int _def_shrink_position = -10001;
    private final int _def_expand_position = -10002;

    private int mChipHeight;// Chip的高度
    private float mTextSize;// 字体大小
    private boolean isDisableCheck;// 是否禁用选择事件

    @ColorInt
    private int plcg_color_stroke;
    @ColorInt
    private int plcg_color_stroke_un;
    @ColorInt
    private int plcg_color_text;
    @ColorInt
    private int plcg_color_text_un;
    @ColorInt
    private int plcg_color_bg;
    @ColorInt
    private int plcg_color_bg_un;

    /**
     * 已选中的位置点
     */
    private LinkedHashSet<Integer> checkPositionSet = new LinkedHashSet<>();
    /**
     * 所有的Chip的集合，key为id
     */
    private HashMap<Integer, Chip> Chips = new HashMap<>();

    private List<BeanChipItems> DATA;

    public PLChipGroup(Context context) {
        this(context, null);
    }

    public PLChipGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PLChipGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mChipGroup = new ChipGroup(context);
        addView(mChipGroup, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        if (null != attrs) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PLChipGroup);

            setChipHeight(a.getDimensionPixelSize(R.styleable.PLChipGroup_plcg_height, dp2px(24)));
            setChipSpacingHorizontal(a.getDimensionPixelOffset(R.styleable.PLChipGroup_chipSpacingHorizontal, 0));
            setChipSpacingVertical(a.getDimensionPixelOffset(R.styleable.PLChipGroup_chipSpacingVertical, 0));
            setSingleLine(a.getBoolean(R.styleable.PLChipGroup_singleLine, false));
            setSingleSelection(a.getBoolean(R.styleable.PLChipGroup_singleSelection, false));
            setSelectionRequired(a.getBoolean(R.styleable.PLChipGroup_selectionRequired, false));
            setMaxCount(a.getInteger(R.styleable.PLChipGroup_plcg_maxCount, -1));
            setTextSize(a.getDimension(R.styleable.PLChipGroup_plcg_textSize, dp2px(12)));

            setColorStroke(a.getColor(R.styleable.PLChipGroup_plcg_color_stroke, getResources().getColor(R.color.plcg_default_color_stroke)));
            setColorStrokeUn(a.getColor(R.styleable.PLChipGroup_plcg_color_stroke_un, getResources().getColor(R.color.plcg_default_color_stroke_un)));
            setColorText(a.getColor(R.styleable.PLChipGroup_plcg_color_stroke, getResources().getColor(R.color.plcg_default_color_text)));
            setColorTextUn(a.getColor(R.styleable.PLChipGroup_plcg_color_stroke_un, getResources().getColor(R.color.plcg_default_color_text_un)));
            setColorBg(a.getColor(R.styleable.PLChipGroup_plcg_color_stroke, getResources().getColor(R.color.plcg_default_color_bg)));
            setColorBgUn(a.getColor(R.styleable.PLChipGroup_plcg_color_stroke_un, getResources().getColor(R.color.plcg_default_color_bg_un)));

            setDisableCheck(a.getBoolean(R.styleable.PLChipGroup_plcg_disable, false));
            String data = a.getString(R.styleable.PLChipGroup_plcg_data);
            if (!TextUtils.isEmpty(data)) {
                setData(data.split(","));
                String dataChoose = a.getString(R.styleable.PLChipGroup_plcg_data_choose);
                if (!TextUtils.isEmpty(dataChoose)) {
                    setCheckPosition(dataChoose);
                }
            }

            a.recycle();
        } else {
            setColorStroke(getResources().getColor(R.color.plcg_default_color_stroke));
            setColorStrokeUn(getResources().getColor(R.color.plcg_default_color_stroke_un));
            setColorText(getResources().getColor(R.color.plcg_default_color_text));
            setColorTextUn(getResources().getColor(R.color.plcg_default_color_text_un));
            setColorBg(getResources().getColor(R.color.plcg_default_color_bg));
            setColorBgUn(getResources().getColor(R.color.plcg_default_color_bg_un));
        }

        if (null != DATA && !DATA.isEmpty())
            show();
    }

    public void setChipHeight(@Dimension int height) {
        this.mChipHeight = height;
    }

    public void setDisableCheck(boolean isDisable) {
        this.isDisableCheck = isDisable;
    }

    public void setChipSpacingHorizontal(@Dimension int chipSpacingHorizontal) {
        mChipGroup.setChipSpacingHorizontal(chipSpacingHorizontal);
    }

    /**
     * 设置竖向距离，因为默认最小是48，而我们支持48以下，所以根据高度适当调整
     *
     * @param chipSpacingVertical 竖向间距
     */
    public void setChipSpacingVertical(@Dimension int chipSpacingVertical) {
        mChipGroup.setChipSpacingVertical(chipSpacingVertical - (dp2px(48) - mChipHeight));
    }

    public void setSingleLine(boolean singleLine) {
        mChipGroup.setSingleLine(singleLine);
    }

    /**
     * 设置选择模式
     *
     * @param singleSelection true单选模式，false多选模式
     */
    public void setSingleSelection(boolean singleSelection) {
        mChipGroup.setSingleSelection(singleSelection);
    }

    public void setSelectionRequired(boolean selectionRequired) {
        mChipGroup.setSelectionRequired(selectionRequired);
    }

    /**
     * 设置最多显示xx条，超过则显示收缩和展开
     *
     * @param maxCount 条数
     */
    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

    /**
     * 设置字体的大小
     *
     * @param textSize 字体大小
     */
    public void setTextSize(float textSize) {
        this.mTextSize = textSize;
    }

    public void setColorStroke(@ColorInt int plcg_color_stroke) {
        this.plcg_color_stroke = plcg_color_stroke;
    }

    public void setColorStrokeUn(@ColorInt int plcg_color_stroke_un) {
        this.plcg_color_stroke_un = plcg_color_stroke_un;
    }

    public void setColorText(@ColorInt int plcg_color_text) {
        this.plcg_color_text = plcg_color_text;
    }

    public void setColorTextUn(@ColorInt int plcg_color_text_un) {
        this.plcg_color_text_un = plcg_color_text_un;
    }

    public void setColorBg(@ColorInt int plcg_color_bg) {
        this.plcg_color_bg = plcg_color_bg;
    }

    public void setColorBgUn(@ColorInt int plcg_color_bg_un) {
        this.plcg_color_bg_un = plcg_color_bg_un;
    }

    /**
     * 设置已选中的项
     *
     * @param checkPosition 已选中的项
     */
    public void setCheckPosition(int... checkPosition) {
        if (null == checkPosition)
            return;
        if (null == DATA || DATA.isEmpty()) {
            setLog("setCheckPosition");
            return;
        }
        checkPositionSet = new LinkedHashSet<>();
        for (int x : checkPosition) {
            checkPositionSet.add(x);
        }
    }

    /**
     * 设置已选中的项
     *
     * @param defaultCheckIds 默认选中的id，多个以逗号分隔。默认0，即第1个
     * @deprecated 请使用 setCheckPosition(checkPositions) 或 setCheckPosition(int... checkPosition)
     */
    @Deprecated
    public void setDefaultCheckIds(String defaultCheckIds) {
        setCheckPosition(defaultCheckIds);
    }

    /**
     * 设置已选中的项
     *
     * @param checkPositions 默认选中的position，多个以逗号分隔。
     */
    public void setCheckPosition(String checkPositions) {
        if (TextUtils.isEmpty(checkPositions)) {
            return;
        }
        if (null == DATA || DATA.isEmpty()) {
            setLog("setCheckPosition");
            return;
        }

        checkPositionSet = new LinkedHashSet<>();
        String[] items = checkPositions.split(",");
        for (String item : items) {
            int x = -1;
            try {
                x = Integer.parseInt(item);
            } catch (Exception ignored) {
            }
            if (x != -1 && x < DATA.size()) {
                checkPositionSet.add(x);
            }
        }
    }

    /**
     * 设置已选中的项
     *
     * @param checkDatas 选中的值，以值来反推position
     */
    public void setCheckDatas(String checkDatas) {
        if (TextUtils.isEmpty(checkDatas))
            return;
        if (null == DATA || DATA.isEmpty()) {
            setLog("setCheckDatas");
            return;
        }
        checkPositionSet = new LinkedHashSet<>();
        HashMap<Object, Integer> map = new HashMap<>();
        int i = 0;
        for (BeanChipItems bean : DATA) {
            map.put(bean.getData(), i++);
        }
        String[] items = checkDatas.split(",");
        for (String item : items) {
            if (map.containsKey(item)) {
                checkPositionSet.add(map.get(item));
            }
        }
    }

    /**
     * 设置要显示的数据
     *
     * @param data 数据
     */
    public void setData(String[] data) {
        this.DATA = new ArrayList<>();
        for (String label : data) {
            this.DATA.add(new BeanChipItems(label, null));
        }
    }

    public void setData(ArrayList<BeanChipItems> items) {
        DATA = new ArrayList<>();
        DATA.addAll(items);
    }

    public void addData(BeanChipItems item) {
        if (null == DATA)
            DATA = new ArrayList<>();
        DATA.add(item);
    }

    public void addDataAll(ArrayList<BeanChipItems> items) {
        if (null == DATA)
            DATA = new ArrayList<>();
        DATA.addAll(items);
    }

    /**
     * 清空所有选项
     */
    public void cleanAll() {
        if (null == Chips) {
            return;
        }

        for (Map.Entry<Integer, Chip> entry : Chips.entrySet()) {
            setChipStatus(entry.getValue(), false);
        }

        checkPositionSet = new LinkedHashSet<>();
        mChipGroup.clearCheck();
    }

    /**
     * 全选，只有多选的情况下，才支持全选
     */
    public void setChooseAll() {
        if (null == Chips) {
            return;
        }

        if (isSingleSelection()) {
            return;
        }

        checkPositionSet = new LinkedHashSet<>();
        boolean isNeedShowShrinkAndExpand = maxCount > 0 && maxCount < DATA.size();
        if (isNeedShowShrinkAndExpand) {
            showExpand();
        }

        for (Map.Entry<Integer, Chip> entry : Chips.entrySet()) {
            if (entry.getKey() == _def_shrink_position || entry.getKey() == _def_expand_position) {
                continue;
            }
            setChipStatus(entry.getValue(), true);
            checkPositionSet.add(entry.getKey());
        }
    }

    /**
     * 设置指定的Chip的勾选
     *
     * @param position 位置
     * @param isCheck  是否选择
     */
    public void setChoose(int position, boolean isCheck) {
        if (null == Chips) {
            return;
        }

        if (!Chips.containsKey(position)) {
            return;
        }

        setChipStatus(Chips.get(position), isCheck);
        checkPositionSet.remove(position);
    }

    /**
     * 点击Chip的监听
     *
     * @param mOnChipCheckListener 监听
     */
    public void setOnChipCheckListener(OnChipCheckListener mOnChipCheckListener) {
        this.mOnChipCheckListener = mOnChipCheckListener;
    }

    /**
     * 显示，默认以收缩状态显示
     */
    public void show() {
        if (null == DATA || DATA.isEmpty()) {
            setLog("show");
            return;
        }

        showShrink();
    }

    /**
     * 显示收缩状态
     */
    private void showShrink() {
        // 是否显示收缩或展开的按钮
        // 只有在配置了最多显示项，以及最多显示项少于总数量的情况下，才需要
        boolean isNeedShowShrinkAndExpand = maxCount > 0 && maxCount < DATA.size();
        int showRealCount;// 本次真实显示的量

        if (isNeedShowShrinkAndExpand) {
            showRealCount = maxCount;
        } else {
            showRealCount = DATA.size();
        }

        mChipGroup.removeAllViews();
        for (int i = 0; i < showRealCount; i++) {
            mChipGroup.addView(createChip(i, DATA.get(i)), i,
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        // 收缩时，显示的文字
        if (isNeedShowShrinkAndExpand) {
            mChipGroup.addView(createChip(_def_shrink_position, null), showRealCount,
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }

    /**
     * 显示展开状态
     */
    private void showExpand() {
        mChipGroup.removeAllViews();
        int size = DATA.size();
        for (int i = 0; i < size; i++) {
            mChipGroup.addView(createChip(i, DATA.get(i)), i,
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        boolean isNeedShowShrinkAndExpand = maxCount > 0 && maxCount < size;
        if (isNeedShowShrinkAndExpand) {
            mChipGroup.addView(createChip(_def_expand_position, null), size,
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }

    /**
     * 创建一个 Chip
     *
     * @param position 第几个
     * @param item     对象
     * @return Chip
     */
    private Chip createChip(final int position, final BeanChipItems item) {
        final Chip mChip = new Chip(getContext());
        ChipDrawable chipDrawable = ChipDrawable.createFromAttributes(getContext(), null,
                0, com.google.android.material.R.style.Widget_MaterialComponents_Chip_Choice);
        mChip.setChipDrawable(chipDrawable);

        mChip.setId(position);// 位置记录

        mChip.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        mChip.setPadding(dp2px(6), 0, dp2px(6), 0);
        mChip.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        mChip.setShapeAppearanceModel(ShapeAppearanceModel.builder().setAllCornerSizes(mChipHeight / 2.0f).build());
        mChip.setChipMinHeight(mChipHeight);
        mChip.setChipStrokeWidth(dp2px(0.8f));
        setChipStatus(mChip, checkPositionSet.contains(position));

        if (position == _def_shrink_position) {
            mChip.setText("显示更多");
            mChip.setCloseIconVisible(true);
            mChip.setCloseIconResource(R.drawable.ic_arrow_drop_down_24dp);
            mChip.setOnClickListener(view -> showExpand());
            mChip.setOnCloseIconClickListener(view -> showExpand());
        } else if (position == _def_expand_position) {
            mChip.setText("折叠");
            mChip.setCloseIconVisible(true);
            mChip.setCloseIconResource(R.drawable.ic_arrow_drop_up_24dp);
            mChip.setOnClickListener(view -> showShrink());
            mChip.setOnCloseIconClickListener(view -> showShrink());
        } else {
            mChip.setText(item.getLabel());
            if (isSingleSelection()) {
                mChip.setOnClickListener(view -> {
                    if (!isDisableCheck) {
                        if (null != checkPositionSet && !checkPositionSet.isEmpty()) {
                            for (int old_id : checkPositionSet) {
                                if (Chips.containsKey(old_id)) {
                                    Chip c = Chips.get(old_id);
                                    if (null != c) {
                                        setChipStatus(c, false);
                                    }
                                    break;
                                }
                            }
                        }
                        checkPositionSet = new LinkedHashSet<>();
                        checkPositionSet.add(position);
                        setChipStatus(mChip, true);

                        if (null != mOnChipCheckListener)
                            mOnChipCheckListener.onClick(PLChipGroup.this, true, position, item);
                    }
                });
            } else {
                mChip.setOnClickListener(v -> {
                    if (!isDisableCheck) {
                        if (null == checkPositionSet) {
                            checkPositionSet = new LinkedHashSet<>();
                        }

                        if (checkPositionSet.contains(position)) {
                            checkPositionSet.remove(position);
                            setChipStatus(mChip, false);
                            if (null != mOnChipCheckListener)
                                mOnChipCheckListener.onClick(PLChipGroup.this, false, position, item);
                        } else {
                            checkPositionSet.add(position);
                            setChipStatus(mChip, true);
                            if (null != mOnChipCheckListener)
                                mOnChipCheckListener.onClick(PLChipGroup.this, true, position, item);
                        }
                    }
                });
            }
        }

        if (null == Chips) {
            Chips = new HashMap<>();
        }
        Chips.put(position, mChip);

        return mChip;
    }

    /**
     * 设置Chip的显示状态
     *
     * @param mChip       Chip
     * @param isCheckable 是否选中
     */
    private void setChipStatus(Chip mChip, boolean isCheckable) {
        if (isCheckable) {
            mChip.setTextColor(plcg_color_text);
            mChip.setChipBackgroundColor(ColorStateList.valueOf(plcg_color_bg));
            mChip.setChipStrokeColor(ColorStateList.valueOf(plcg_color_stroke));
        } else {
            mChip.setTextColor(plcg_color_text_un);
            mChip.setChipBackgroundColor(ColorStateList.valueOf(plcg_color_bg_un));
            mChip.setChipStrokeColor(ColorStateList.valueOf(plcg_color_stroke_un));
        }
    }

    /**
     * 是否为单选
     *
     * @return true 是
     */
    public boolean isSingleSelection() {
        return mChipGroup.isSingleSelection();
    }

    /**
     * 单选模式下，选择的项
     *
     * @return 位置
     */
    public int getSingleSelectionPosition() {
        if (null != checkPositionSet && !checkPositionSet.isEmpty()) {
            return checkPositionSet.iterator().next();
        } else {
            return -1;
        }
    }

    /**
     * 多选的模式下，选择的项
     *
     * @return list
     */
    public List<Integer> getCheckedPositions() {
        if (null != checkPositionSet && !checkPositionSet.isEmpty()) {
            return new ArrayList<>(checkPositionSet);
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * 获取已选择项的label
     *
     * @return list列表
     * @deprecated 使用 getCheckedLabel()
     */
    @Deprecated
    public List<String> getCheckedValues() {
        return getCheckedLabel();
    }

    /**
     * 获取已选择项的label
     *
     * @return list列表
     */
    public List<String> getCheckedLabel() {
        if (null != checkPositionSet && !checkPositionSet.isEmpty()) {
            List<String> list = new ArrayList<>();
            for (int i : checkPositionSet) {
                list.add(DATA.get(i).getLabel());
            }
            return list;
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * 获取已选择项的data
     *
     * @return list
     */
    public List<Object> getCheckedData() {
        if (null != checkPositionSet && !checkPositionSet.isEmpty()) {
            List<Object> list = new ArrayList<>();
            for (int i : checkPositionSet) {
                list.add(DATA.get(i).getData());
            }
            return list;
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * 获取已选择项的label，多个以逗号分隔
     *
     * @return 已选择的项
     */
    public String getCheckedLabelToString() {
        if (null != checkPositionSet && !checkPositionSet.isEmpty()) {
            StringBuilder buf = new StringBuilder();
            for (int i : checkPositionSet) {
                buf.append(DATA.get(i).getLabel()).append(",");
            }
            return buf.substring(0, buf.length() - 1);
        } else {
            return "";
        }
    }

    /**
     * 获取已选择项的data，多个以逗号分隔
     *
     * @return 已选择的项
     */
    public String getCheckedDataToString() {
        if (null != checkPositionSet && !checkPositionSet.isEmpty()) {
            StringBuilder buf = new StringBuilder();
            for (int i : checkPositionSet) {
                buf.append(DATA.get(i).getData()).append(",");
            }
            return buf.substring(0, buf.length() - 1);
        } else {
            return "";
        }
    }

    public int dp2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private void setLog(String tag) {
        Log.w(TAG + "." + tag, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        Log.w(TAG + "." + tag, "!!!!!!!!请先调用.setData方法传入数据!!!!!!!!!");
        Log.w(TAG + "." + tag, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    }
}

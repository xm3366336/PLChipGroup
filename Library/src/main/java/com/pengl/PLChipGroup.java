package com.pengl;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.shape.ShapeAppearanceModel;
import com.pengl.plchipgroup.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;

import androidx.annotation.Dimension;
import androidx.constraintlayout.widget.ConstraintLayout;

public class PLChipGroup extends ConstraintLayout {

    private Context mContext;
    private ChipGroup mChipGroup;
    private OnChipCheckListener mOnChipCheckListener;

    /**
     * 最多显示多少条，默认0，小于=0时，不限制。如果超过xx条，则显示：收缩和展开
     */
    private int maxCount;

    private final String _def_shrink_text = "显示更多";// 收缩时，显示的文字
    private final int _def_shrink_id = -10001;
    private final String _def_expand_text = "折叠";// 展开时，显示的文字
    private final int _def_expand_id = -10002;

    private int mChipHeight;// Chip的高度

    /**
     * 已选中的id
     */
    private LinkedHashSet<Integer> checkIds = new LinkedHashSet<>();
    /**
     * 所有的Chip的集合，key为id
     */
    private HashMap<Integer, Chip> Chips = new HashMap<>();

    private String[] DATA;

    public PLChipGroup(Context context) {
        super(context);
        this.mContext = context;
        init(null);
    }

    public PLChipGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init(attrs);
    }

    public PLChipGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init(attrs);
    }

    public void init(AttributeSet attrs) {
        View.inflate(mContext, R.layout.pl_view_chipgroup, this);
        mChipGroup = findViewById(R.id.mChipGroup);

        if (null != attrs && null != mContext) {
            TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.PLChipGroup);

            setChipHeight(a.getDimensionPixelSize(R.styleable.PLChipGroup_chipHeight, dp2px(24)));
            setChipSpacingHorizontal(a.getDimensionPixelOffset(R.styleable.PLChipGroup_chipSpacingHorizontal, 0));
            setChipSpacingVertical(a.getDimensionPixelOffset(R.styleable.PLChipGroup_chipSpacingVertical, 0));
            setSingleLine(a.getBoolean(R.styleable.PLChipGroup_singleLine, false));
            setSingleSelection(a.getBoolean(R.styleable.PLChipGroup_singleSelection, false));
            setSelectionRequired(a.getBoolean(R.styleable.PLChipGroup_selectionRequired, false));
            setMaxCount(a.getInteger(R.styleable.PLChipGroup_maxCount, -1));

            a.recycle();
        }
    }

    public void setChipHeight(@Dimension int height) {
        this.mChipHeight = height;
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
     * 设置要显示的数据
     *
     * @param data 数据
     */
    public void setData(String[] data) {
        this.DATA = data;
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
        showShrink();
    }

    /**
     * 显示收缩状态
     */
    private void showShrink() {
        // 是否显示收缩或展开的按钮
        // 只有在配置了最多显示项，以及最多显示项少于总数量的情况下，才需要
        boolean isNeedShowShrinkAndExpand = maxCount > 0 && maxCount < DATA.length;
        int showRealCount;// 本次真实显示的量

        if (isNeedShowShrinkAndExpand) {
            showRealCount = maxCount;
        } else {
            showRealCount = DATA.length;
        }

        mChipGroup.removeAllViews();
        for (int i = 0; i < showRealCount; i++) {
            mChipGroup.addView(createChip(i, DATA[i]), i, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        if (isNeedShowShrinkAndExpand) {
            mChipGroup.addView(createChip(_def_shrink_id, _def_shrink_text), showRealCount, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }

    /**
     * 显示展开状态
     */
    private void showExpand() {
        mChipGroup.removeAllViews();
        for (int i = 0; i < DATA.length; i++) {
            mChipGroup.addView(createChip(i, DATA[i]), i, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        boolean isNeedShowShrinkAndExpand = maxCount > 0 && maxCount < DATA.length;
        if (isNeedShowShrinkAndExpand) {
            mChipGroup.addView(createChip(_def_expand_id, _def_expand_text), DATA.length, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }

    /**
     * 创建一个 Chip
     *
     * @param id   id
     * @param text 显示的文字
     * @return Chip
     */
    private Chip createChip(final int id, final String text) {
        final Chip mChip = new Chip(mContext);
        ChipDrawable chipDrawable = ChipDrawable.createFromAttributes(mContext, null, 0, R.style.Widget_MaterialComponents_Chip_Choice);
        mChip.setChipDrawable(chipDrawable);

        mChip.setId(id);// id以位置记录
        mChip.setText(text);
        mChip.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        mChip.setPadding(dp2px(6), 0, dp2px(6), 0);
        mChip.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        mChip.setShapeAppearanceModel(ShapeAppearanceModel.builder().setAllCornerSizes(mChipHeight / 2.0f).build());
        mChip.setChipMinHeight(mChipHeight);
        mChip.setChipStrokeWidth(dp2px(0.8f));

        setChipStatus(mChip, checkIds.contains(id));

        if (id == _def_shrink_id) {
            mChip.setCloseIconVisible(true);
            mChip.setCloseIconResource(R.drawable.ic_arrow_drop_down_24dp);
            mChip.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    showExpand();
                }
            });
            mChip.setOnCloseIconClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    showExpand();
                }
            });
        } else if (id == _def_expand_id) {
            mChip.setCloseIconVisible(true);
            mChip.setCloseIconResource(R.drawable.ic_arrow_drop_up_24dp);
            mChip.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    showShrink();
                }
            });
            mChip.setOnCloseIconClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    showShrink();
                }
            });
        } else {
            if (isSingleSelection()) {
                mChip.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (null != checkIds && checkIds.size() >= 1) {
                            for (int old_id : checkIds) {
                                if (Chips.containsKey(old_id)) {
                                    Chip c = Chips.get(old_id);
                                    if (null != c) {
                                        setChipStatus(c, false);
                                    }
                                    break;
                                }
                            }
                        }
                        checkIds = new LinkedHashSet<>();
                        checkIds.add(id);
                        setChipStatus(mChip, true);
                        mOnChipCheckListener.onClick(PLChipGroup.this, id, text);
                    }
                });
            } else {
                mChip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isCheck) {
                        if (null == checkIds) {
                            checkIds = new LinkedHashSet<>();
                        }

                        if (isCheck) {
                            checkIds.add(id);
                            mOnChipCheckListener.onClick(PLChipGroup.this, id, text);
                        } else {
                            checkIds.remove(id);
                        }

                        setChipStatus(mChip, isCheck);
                    }
                });
            }
        }

        if (null == Chips) {
            Chips = new HashMap<>();
        }
        Chips.put(id, mChip);

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
            mChip.setTextColor(getResources().getColor(R.color.pl_chipgroup_color_text));
            mChip.setChipBackgroundColorResource(R.color.pl_chipgroup_color_bg);
            mChip.setChipStrokeColorResource(R.color.pl_chipgroup_color_stroke);
        } else {
            mChip.setTextColor(getResources().getColor(R.color.pl_chipgroup_color_text_un));
            mChip.setChipBackgroundColorResource(R.color.pl_chipgroup_color_bg_un);
            mChip.setChipStrokeColorResource(R.color.pl_chipgroup_color_stroke_un);
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
        if (null != checkIds && checkIds.size() > 0) {
            return checkIds.iterator().next();
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
        if (null != checkIds && checkIds.size() > 0) {
            return new ArrayList<>(checkIds);
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * 显示选择的项
     *
     * @return list列表
     */
    public List<String> getCheckedValues() {
        if (null != checkIds && checkIds.size() > 0) {
            List<String> list = new ArrayList<>();
            for (int i : checkIds) {
                list.add(DATA[i]);
            }
            return list;
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * 显示选择的项，多个以逗号分隔
     *
     * @return 已选择的项
     */
    public String getCheckedValuesToString() {
        if (null != checkIds && checkIds.size() > 0) {
            StringBuilder buf = new StringBuilder();
            for (int i : checkIds) {
                buf.append(DATA[i]).append(",");
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
}

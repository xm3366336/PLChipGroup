package com.pengl;

public interface OnChipCheckListener {
    /**
     * @param view           当前
     * @param isCheck        是选中还是取消选中，对于单选，都是true
     * @param position       位置
     * @param mBeanChipItems 对象
     */
    void onClick(PLChipGroup view, boolean isCheck, int position, BeanChipItems mBeanChipItems);
}

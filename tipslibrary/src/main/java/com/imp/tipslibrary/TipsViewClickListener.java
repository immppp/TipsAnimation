package com.imp.tipslibrary;

public interface TipsViewClickListener {
    /**
     * 控件点击回调
     *
     * @param repeatTimes 重复的次数
     * @param radium      圆圈的半径
     * @param typeIcon    当前绘制的图标内容
     */
    void onClickListener(int repeatTimes, int radium, String typeIcon);
}

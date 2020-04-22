package com.imp.tipslibrary;

/**
 * @ClassName: TipsEnum
 * @Author: imp
 * @Description: 根据类型执行不同的动画
 * @Date: 2020/4/22 1:45 PM
 * @Version: 1.0
 */

public enum TipsEnum {
    RIGHT("right", "对号"),
    TRIANGLE("triangle", "三角形"),
    LAMENT("lament", "惊叹号"),
    START("start", "开始"),
    STOP("stop", "暂停"),
    ;

    TipsEnum(String code, String des) {
        this.code = code;
        this.des = des;
    }

    private String code;
    private String des;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }
}

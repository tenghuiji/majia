package com.majia.enumerate;

public enum HuType {
    NORMAL(2),PENG_GPENG_HU(5),QING_YI_SE(10),SEVEN_PAIR(10),PENG_QING_YI_SE(15),QING_SEVEN_PAIR(20),LONG_SEVEN_PAIR(20);
    private int value;

    public int getValue() {
        return value;
    }

    private HuType(int _value)
    {
        this.value=_value;
    }
    @Override
    public String toString()
    {
        return this.getValue()+"";
    }
}

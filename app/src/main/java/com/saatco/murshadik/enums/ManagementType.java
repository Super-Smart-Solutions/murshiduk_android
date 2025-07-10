package com.saatco.murshadik.enums;

public enum ManagementType {
    BRANCHES(10077),
    MINISTRY(10086),
    ALL(-1);

    private final int value;
    ManagementType(int i) {
        value = i;
    }

    public int getValue() {
        return value;
    }
}

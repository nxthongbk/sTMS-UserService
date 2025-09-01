package com.scity.user.model.constant;

public enum EDefaultValue {
    TENANT_CODE("DEFAULT");

    private String value;
    EDefaultValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

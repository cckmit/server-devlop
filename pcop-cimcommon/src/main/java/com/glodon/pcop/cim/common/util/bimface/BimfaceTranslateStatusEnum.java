package com.glodon.pcop.cim.common.util.bimface;

public enum BimfaceTranslateStatusEnum {
    PROCESS(0), SUCCESS(1), FAILED(2);

    private int status;

    BimfaceTranslateStatusEnum(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

}

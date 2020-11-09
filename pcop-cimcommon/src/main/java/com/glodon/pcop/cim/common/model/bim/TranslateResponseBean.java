package com.glodon.pcop.cim.common.model.bim;

import com.bimface.sdk.bean.response.TranslateBean;

public class TranslateResponseBean extends TranslateBean {
    private String databagId;

    public String getDatabagId() {
        return databagId;
    }

    public void setDatabagId(String databagId) {
        this.databagId = databagId;
    }
}

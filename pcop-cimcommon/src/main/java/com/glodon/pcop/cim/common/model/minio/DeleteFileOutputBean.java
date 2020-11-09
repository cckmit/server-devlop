package com.glodon.pcop.cim.common.model.minio;

import java.util.Map;

public class DeleteFileOutputBean {
    private String bucket;
    private Map<String, Boolean> deleteResults;

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public Map<String, Boolean> getDeleteResults() {
        return deleteResults;
    }

    public void setDeleteResults(Map<String, Boolean> deleteResults) {
        this.deleteResults = deleteResults;
    }

    @Override
    public String toString() {
        return "DeleteFileOutputBean{" +
                "bucket='" + bucket + '\'' +
                ", deleteResults=" + deleteResults +
                '}';
    }
}

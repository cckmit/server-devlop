package com.glodon.pcop.cim.common.model.minio;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

public class DeleteFileInputBean {
    @Size(min = 1, message = "bucket name is mandatory")
    private String bucket;
    @NotEmpty
    private List<String> fileNames;

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public List<String> getFileNames() {
        return fileNames;
    }

    public void setFileNames(List<String> fileNames) {
        this.fileNames = fileNames;
    }

    @Override
    public String toString() {
        return "DeleteFileInputBean{" +
                "bucket='" + bucket + '\'' +
                ", fileNames=" + fileNames +
                '}';
    }
}

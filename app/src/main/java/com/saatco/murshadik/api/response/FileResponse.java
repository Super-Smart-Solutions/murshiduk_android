package com.saatco.murshadik.api.response;

import com.google.gson.annotations.SerializedName;

public class FileResponse extends BaseResponse {

    @SerializedName("uploaded_file_url")
    private String fileUrl;

    @SerializedName("file_name")
    private String fileName;

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}

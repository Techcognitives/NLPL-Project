package com.nlpl.model.UpdateModel.Models.UpdateDriverDetails;

public class UpdateDriverUploadLicense {

    private String upload_lc;

    public UpdateDriverUploadLicense(String upload_lc) {
        this.upload_lc = upload_lc;
    }

    @Override
    public String toString() {
        return "UpdateDriverUploadLicense{" +
                "upload_lc='" + upload_lc + '\'' +
                '}';
    }

    public String getUpload_lc() {
        return upload_lc;
    }

    public void setUpload_lc(String upload_lc) {
        this.upload_lc = upload_lc;
    }
}

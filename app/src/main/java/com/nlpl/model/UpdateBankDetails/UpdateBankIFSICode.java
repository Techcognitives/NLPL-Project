package com.nlpl.model.UpdateBankDetails;

public class UpdateBankIFSICode {

    private String IFSI_CODE;

    public UpdateBankIFSICode(String IFSI_CODE) {
        this.IFSI_CODE = IFSI_CODE;
    }

    @Override
    public String toString() {
        return "UpdateBankIFSICode{" +
                "IFSI_CODE='" + IFSI_CODE + '\'' +
                '}';
    }

    public String getIFSI_CODE() {
        return IFSI_CODE;
    }

    public void setIFSI_CODE(String IFSI_CODE) {
        this.IFSI_CODE = IFSI_CODE;
    }
}

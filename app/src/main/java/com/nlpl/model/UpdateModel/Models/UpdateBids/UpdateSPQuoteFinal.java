package com.nlpl.model.UpdateModel.Models.UpdateBids;

public class UpdateSPQuoteFinal {
    private String sp_quote;

    public UpdateSPQuoteFinal(String sp_quote) {
        this.sp_quote = sp_quote;
    }

    @Override
    public String toString() {
        return "UpdateSPQuoteFinal{" +
                "sp_quote='" + sp_quote + '\'' +
                '}';
    }

    public String getSp_quote() {
        return sp_quote;
    }

    public void setSp_quote(String sp_quote) {
        this.sp_quote = sp_quote;
    }
}

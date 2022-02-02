package com.nlpl.model.UpdateModel.Models.UpdateLoadPost;

public class UpdateLoadStatusSubmitted {
    private String bid_status;

    public UpdateLoadStatusSubmitted(String bid_status) {
        this.bid_status = bid_status;
    }

    @Override
    public String toString() {
        return "UpdateLoadStatusSubmitted{" +
                "bid_status='" + bid_status + '\'' +
                '}';
    }

    public String getBid_status() {
        return bid_status;
    }

    public void setBid_status(String bid_status) {
        this.bid_status = bid_status;
    }
}

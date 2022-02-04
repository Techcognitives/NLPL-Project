package com.nlpl.model.UpdateModel.Models.UpdateBids;

public class UpdateBidStatus {
    private String bid_status;

    public UpdateBidStatus(String bid_status) {
        this.bid_status = bid_status;
    }

    @Override
    public String toString() {
        return "UpdateBidStatus{" +
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

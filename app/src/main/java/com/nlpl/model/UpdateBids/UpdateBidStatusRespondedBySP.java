package com.nlpl.model.UpdateBids;

public class UpdateBidStatusRespondedBySP {
    private String bid_status;

    public UpdateBidStatusRespondedBySP(String bid_status) {
        this.bid_status = bid_status;
    }

    @Override
    public String toString() {
        return "UpdateBidStatusRespondedBySP{" +
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

package com.nlpl.model.UpdateBids;

public class UpdateBidStatusFinalAccepted {
    private String bid_status;

    public UpdateBidStatusFinalAccepted(String bid_status) {
        this.bid_status = bid_status;
    }

    @Override
    public String toString() {
        return "UpdateBidStatusFinalAccepted{" +
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

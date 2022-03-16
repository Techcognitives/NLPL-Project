package com.nlpl.model.Requests;

public class BidLoadRequest {
    String user_id, is_bid_accpted_by_sp, bid_status, idpost_load, capacity, notes, sp_quote, body_type,  assigned_truck_id, assigned_driver_id;
    Boolean is_negatiable;
    public BidLoadRequest() {
        this.user_id = user_id;
        this.idpost_load = idpost_load;
        this.sp_quote = sp_quote;
        this.is_negatiable = is_negatiable;
        this.assigned_truck_id = assigned_truck_id;
        this.assigned_driver_id = assigned_driver_id;
        this.capacity = capacity;
        this.body_type = body_type;
        this.notes = notes;
        this.bid_status = bid_status;
        this.is_bid_accpted_by_sp = is_bid_accpted_by_sp;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getIs_bid_accpted_by_sp() {
        return is_bid_accpted_by_sp;
    }

    public void setIs_bid_accpted_by_sp(String is_bid_accpted_by_sp) {
        this.is_bid_accpted_by_sp = is_bid_accpted_by_sp;
    }

    public String getBid_status() {
        return bid_status;
    }

    public void setBid_status(String bid_status) {
        this.bid_status = bid_status;
    }

    public String getIdpost_load() {
        return idpost_load;
    }

    public void setIdpost_load(String idpost_load) {
        this.idpost_load = idpost_load;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getSp_quote() {
        return sp_quote;
    }

    public void setSp_quote(String sp_quote) {
        this.sp_quote = sp_quote;
    }

    public String getBody_type() {
        return body_type;
    }

    public void setBody_type(String body_type) {
        this.body_type = body_type;
    }

    public Boolean getIs_negatiable() {
        return is_negatiable;
    }

    public void setIs_negatiable(Boolean is_negatiable) {
        this.is_negatiable = is_negatiable;
    }

    public String getAssigned_truck_id() {
        return assigned_truck_id;
    }

    public void setAssigned_truck_id(String assigned_truck_id) {
        this.assigned_truck_id = assigned_truck_id;
    }

    public String getAssigned_driver_id() {
        return assigned_driver_id;
    }

    public void setAssigned_driver_id(String assigned_driver_id) {
        this.assigned_driver_id = assigned_driver_id;
    }
}

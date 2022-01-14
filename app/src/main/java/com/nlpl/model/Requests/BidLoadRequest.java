package com.nlpl.model.Requests;

public class BidLoadRequest {
    String user_id, is_bid_accpted_by_sp, bid_status, idpost_load, feet, capacity, notes, sp_quote, body_type, is_negatiable, assigned_truck_id, assigned_driver_id, vehicle_model   ;

    public BidLoadRequest() {
        this.user_id = user_id;
        this.idpost_load = idpost_load;
        this.sp_quote = sp_quote;
        this.is_negatiable = is_negatiable;
        this.assigned_truck_id = assigned_truck_id;
        this.assigned_driver_id = assigned_driver_id;
        this.vehicle_model = vehicle_model;
        this.feet = feet;
        this.capacity = capacity;
        this.body_type = body_type;
        this.notes = notes;
        this.bid_status = bid_status;
        this.is_bid_accpted_by_sp = is_bid_accpted_by_sp;
    }
}

package com.nlpl.model.UpdateModel.Models;

public class UpdateTripDetails {
    private String trip_date;
    private String trip_start_time;
    private String trip_budget;
    private String revised_trip_budget_one;
    private String revised_trip_budget_two;
    private String trip_status;
    private String vehicle_model;
    private String feet;
    private String capacity;
    private String body_type;
    private String pick_add;
    private String pick_pin_code;
    private String pick_city;
    private String pick_state;
    private String pick_country;
    private String drop_add;
    private String drop_pin_code;
    private String drop_city;
    private String drop_state;
    private String drop_country;
    private String notes_meterial_des;
    private float customer_count;
    private String payment_type;

    @Override
    public String toString() {
        return "Update_Trip_Details{" +
                "trip_date='" + trip_date + '\'' +
                ", trip_start_time='" + trip_start_time + '\'' +
                ", trip_budget='" + trip_budget + '\'' +
                ", revised_trip_budget_one='" + revised_trip_budget_one + '\'' +
                ", revised_trip_budget_two='" + revised_trip_budget_two + '\'' +
                ", trip_status='" + trip_status + '\'' +
                ", vehicle_model='" + vehicle_model + '\'' +
                ", feet='" + feet + '\'' +
                ", capacity='" + capacity + '\'' +
                ", body_type='" + body_type + '\'' +
                ", pick_add='" + pick_add + '\'' +
                ", pick_pin_code='" + pick_pin_code + '\'' +
                ", pick_city='" + pick_city + '\'' +
                ", pick_state='" + pick_state + '\'' +
                ", pick_country='" + pick_country + '\'' +
                ", drop_add='" + drop_add + '\'' +
                ", drop_pin_code='" + drop_pin_code + '\'' +
                ", drop_city='" + drop_city + '\'' +
                ", drop_state='" + drop_state + '\'' +
                ", drop_country='" + drop_country + '\'' +
                ", notes_meterial_des='" + notes_meterial_des + '\'' +
                ", customer_count=" + customer_count +
                ", payment_type='" + payment_type + '\'' +
                '}';
    }

    public UpdateTripDetails(String trip_date, String trip_start_time, String trip_budget, String revised_trip_budget_one, String revised_trip_budget_two, String trip_status, String vehicle_model, String feet, String capacity, String body_type, String pick_add, String pick_pin_code, String pick_city, String pick_state, String pick_country, String drop_add, String drop_pin_code, String drop_city, String drop_state, String drop_country, String notes_meterial_des, float customer_count, String payment_type) {
        this.trip_date = trip_date;
        this.trip_start_time = trip_start_time;
        this.trip_budget = trip_budget;
        this.revised_trip_budget_one = revised_trip_budget_one;
        this.revised_trip_budget_two = revised_trip_budget_two;
        this.trip_status = trip_status;
        this.vehicle_model = vehicle_model;
        this.feet = feet;
        this.capacity = capacity;
        this.body_type = body_type;
        this.pick_add = pick_add;
        this.pick_pin_code = pick_pin_code;
        this.pick_city = pick_city;
        this.pick_state = pick_state;
        this.pick_country = pick_country;
        this.drop_add = drop_add;
        this.drop_pin_code = drop_pin_code;
        this.drop_city = drop_city;
        this.drop_state = drop_state;
        this.drop_country = drop_country;
        this.notes_meterial_des = notes_meterial_des;
        this.customer_count = customer_count;
        this.payment_type = payment_type;
    }

// Getter Methods

    public String getTrip_date() {
        return trip_date;
    }

    public String getTrip_start_time() {
        return trip_start_time;
    }

    public String getTrip_budget() {
        return trip_budget;
    }

    public String getRevised_trip_budget_one() {
        return revised_trip_budget_one;
    }

    public String getRevised_trip_budget_two() {
        return revised_trip_budget_two;
    }

    public String getVehicle_model() {
        return vehicle_model;
    }

    public String getFeet() {
        return feet;
    }

    public String getCapacity() {
        return capacity;
    }

    public String getBody_type() {
        return body_type;
    }

    public String getPick_add() {
        return pick_add;
    }

    public String getPick_pin_code() {
        return pick_pin_code;
    }

    public String getPick_city() {
        return pick_city;
    }

    public String getPick_state() {
        return pick_state;
    }

    public String getPick_country() {
        return pick_country;
    }

    public String getDrop_add() {
        return drop_add;
    }

    public String getDrop_pin_code() {
        return drop_pin_code;
    }

    public String getDrop_city() {
        return drop_city;
    }

    public String getDrop_state() {
        return drop_state;
    }

    public String getDrop_country() {
        return drop_country;
    }

    public String getNotes_meterial_des() {
        return notes_meterial_des;
    }

    public float getCustomer_count() {
        return customer_count;
    }

    public String getPayment_type() {
        return payment_type;
    }

    // Setter Methods

    public void setTrip_date(String trip_date) {
        this.trip_date = trip_date;
    }

    public void setTrip_start_time(String trip_start_time) {
        this.trip_start_time = trip_start_time;
    }

    public void setTrip_budget(String trip_budget) {
        this.trip_budget = trip_budget;
    }

    public void setRevised_trip_budget_one(String revised_trip_budget_one) {
        this.revised_trip_budget_one = revised_trip_budget_one;
    }

    public void setRevised_trip_budget_two(String revised_trip_budget_two) {
        this.revised_trip_budget_two = revised_trip_budget_two;
    }

    public void setVehicle_model(String vehicle_model) {
        this.vehicle_model = vehicle_model;
    }

    public void setFeet(String feet) {
        this.feet = feet;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public void setBody_type(String body_type) {
        this.body_type = body_type;
    }

    public void setPick_add(String pick_add) {
        this.pick_add = pick_add;
    }

    public void setPick_pin_code(String pick_pin_code) {
        this.pick_pin_code = pick_pin_code;
    }

    public void setPick_city(String pick_city) {
        this.pick_city = pick_city;
    }

    public void setPick_state(String pick_state) {
        this.pick_state = pick_state;
    }

    public void setPick_country(String pick_country) {
        this.pick_country = pick_country;
    }

    public void setDrop_add(String drop_add) {
        this.drop_add = drop_add;
    }

    public void setDrop_pin_code(String drop_pin_code) {
        this.drop_pin_code = drop_pin_code;
    }

    public void setDrop_city(String drop_city) {
        this.drop_city = drop_city;
    }

    public void setDrop_state(String drop_state) {
        this.drop_state = drop_state;
    }

    public void setDrop_country(String drop_country) {
        this.drop_country = drop_country;
    }

    public void setNotes_meterial_des(String notes_meterial_des) {
        this.notes_meterial_des = notes_meterial_des;
    }

    public void setCustomer_count(float customer_count) {
        this.customer_count = customer_count;
    }

    public void setPayment_type(String payment_type) {
        this.payment_type = payment_type;
    }
}

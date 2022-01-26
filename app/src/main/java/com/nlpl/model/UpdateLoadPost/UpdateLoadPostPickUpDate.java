package com.nlpl.model.UpdateLoadPost;

public class UpdateLoadPostPickUpDate {

    private String pick_up_date, pick_up_time, budget, vehicle_model, feet, capacity, body_type, pick_add, pick_pin_code, pick_city, pick_state, pick_country, drop_add, drop_pin_code, drop_city, drop_state, drop_country, km_approx, notes_meterial_des;

    public UpdateLoadPostPickUpDate(String pick_up_date, String pick_up_time, String budget, String vehicle_model, String feet, String capacity, String body_type, String pick_add, String pick_pin_code, String pick_city, String pick_state, String pick_country, String drop_add, String drop_pin_code, String drop_city, String drop_state, String drop_country, String km_approx, String notes_meterial_des) {
        this.pick_up_date = pick_up_date;
        this.pick_up_time = pick_up_time;
        this.budget = budget;
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
        this.km_approx = km_approx;
        this.notes_meterial_des = notes_meterial_des;
    }

    @Override
    public String toString() {
        return "UpdateLoadPost{" +
                "pick_up_date='" + pick_up_date + '\'' +
                ", pick_up_time='" + pick_up_time + '\'' +
                ", budget='" + budget + '\'' +
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
                ", km_approx='" + km_approx + '\'' +
                ", notes_meterial_des='" + notes_meterial_des + '\'' +
                '}';
    }

    public String getPick_up_date() {
        return pick_up_date;
    }

    public void setPick_up_date(String pick_up_date) {
        this.pick_up_date = pick_up_date;
    }

    public String getPick_up_time() {
        return pick_up_time;
    }

    public void setPick_up_time(String pick_up_time) {
        this.pick_up_time = pick_up_time;
    }

    public String getBudget() {
        return budget;
    }

    public void setBudget(String budget) {
        this.budget = budget;
    }

    public String getVehicle_model() {
        return vehicle_model;
    }

    public void setVehicle_model(String vehicle_model) {
        this.vehicle_model = vehicle_model;
    }

    public String getFeet() {
        return feet;
    }

    public void setFeet(String feet) {
        this.feet = feet;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getBody_type() {
        return body_type;
    }

    public void setBody_type(String body_type) {
        this.body_type = body_type;
    }

    public String getPick_add() {
        return pick_add;
    }

    public void setPick_add(String pick_add) {
        this.pick_add = pick_add;
    }

    public String getPick_pin_code() {
        return pick_pin_code;
    }

    public void setPick_pin_code(String pick_pin_code) {
        this.pick_pin_code = pick_pin_code;
    }

    public String getPick_city() {
        return pick_city;
    }

    public void setPick_city(String pick_city) {
        this.pick_city = pick_city;
    }

    public String getPick_state() {
        return pick_state;
    }

    public void setPick_state(String pick_state) {
        this.pick_state = pick_state;
    }

    public String getPick_country() {
        return pick_country;
    }

    public void setPick_country(String pick_country) {
        this.pick_country = pick_country;
    }

    public String getDrop_add() {
        return drop_add;
    }

    public void setDrop_add(String drop_add) {
        this.drop_add = drop_add;
    }

    public String getDrop_pin_code() {
        return drop_pin_code;
    }

    public void setDrop_pin_code(String drop_pin_code) {
        this.drop_pin_code = drop_pin_code;
    }

    public String getDrop_city() {
        return drop_city;
    }

    public void setDrop_city(String drop_city) {
        this.drop_city = drop_city;
    }

    public String getDrop_state() {
        return drop_state;
    }

    public void setDrop_state(String drop_state) {
        this.drop_state = drop_state;
    }

    public String getDrop_country() {
        return drop_country;
    }

    public void setDrop_country(String drop_country) {
        this.drop_country = drop_country;
    }

    public String getKm_approx() {
        return km_approx;
    }

    public void setKm_approx(String km_approx) {
        this.km_approx = km_approx;
    }

    public String getNotes_meterial_des() {
        return notes_meterial_des;
    }

    public void setNotes_meterial_des(String notes_meterial_des) {
        this.notes_meterial_des = notes_meterial_des;
    }
}

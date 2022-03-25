package com.nlpl.model.Responses;

import java.util.List;

public class PreferedLocationResponse {
    private List<UserList> data;

    public List<UserList> getData() {
        return data;
    }

    public void setData(List<UserList> data) {
        this.data = data;
}

    public class UserList {
        private String pref_state,
                pref_city,
                pref_pin_code,
                latitude,
                longitude,
                user_id, pref_locations_id;

        //getters and setters
        public String getPref_state() {
            return pref_state;
        }

        public void setPref_state(String pref_state) {
            this.pref_state = pref_state;
        }

        public String getPref_city() {
            return pref_city;
        }

        public void setPref_city(String pref_city) {
            this.pref_city = pref_city;
        }

        public String getPref_pin_code() {
            return pref_pin_code;
        }

        public void setPref_pin_code(String pref_pin_code) {
            this.pref_pin_code = pref_pin_code;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getPref_locations_id() {
            return pref_locations_id;
        }

        public void setPref_locations_id(String pref_locations_id) {
            this.pref_locations_id = pref_locations_id;
        }
    }
    }

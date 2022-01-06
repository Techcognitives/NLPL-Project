package com.nlpl.model.Responses;

public class AddTruckResponse {
    private String success;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    private AddTruckResponse.Data data;

    public AddTruckResponse.Data getData() {
        return data;
    }

    public void setData(AddTruckResponse.Data data) {
        this.data = data;
    }

    public class Data {

        private String user_id, vehicle_no, vehicle_body_type, truck_id;

        public Data(String user_id, String vehicle_no, String vehicle_body_type, String truck_id) {
            this.user_id = user_id;
            this.vehicle_no = vehicle_no;
            this.vehicle_body_type = vehicle_body_type;
            this.truck_id = truck_id;
        }

        public String getTruck_id() {
            return truck_id;
        }

        public void setTruck_id(String truck_id) {
            this.truck_id = truck_id;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getVehicle_no() {
            return vehicle_no;
        }

        public void setVehicle_no(String vehicle_no) {
            this.vehicle_no = vehicle_no;
        }

        public String getVehicle_body_type() {
            return vehicle_body_type;
        }

        public void setVehicle_body_type(String vehicle_body_type) {
            this.vehicle_body_type = vehicle_body_type;
        }
    }
}

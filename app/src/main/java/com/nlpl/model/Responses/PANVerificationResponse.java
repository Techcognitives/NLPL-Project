package com.nlpl.model.Responses;

import java.util.List;

public class PANVerificationResponse {
    private String success;
    private List<UserList> data;

    public List<UserList> getData() {
        return data;
    }

    public void setData(List<UserList> data) {
        this.data = data;
    }

    public class UserList {
        private String id,
                success,
                response_code,
                response_message,
                pan_number,
                pan_status,
                user_full_name,
                request_timestamp,
                user_id,
                pan_id,
                created_at;

        //getters and setters
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getSuccess() {
            return success;
        }

        public void setSuccess(String success) {
            this.success = success;
        }

        public String getResponse_code() {
            return response_code;
        }

        public void setResponse_code(String response_code) {
            this.response_code = response_code;
        }

        public String getResponse_message() {
            return response_message;
        }

        public void setResponse_message(String response_message) {
            this.response_message = response_message;
        }

        public String getPan_number() {
            return pan_number;
        }

        public void setPan_number(String pan_number) {
            this.pan_number = pan_number;
        }

        public String getPan_status() {
            return pan_status;
        }

        public void setPan_status(String pan_status) {
            this.pan_status = pan_status;
        }

        public String getUser_full_name() {
            return user_full_name;
        }

        public void setUser_full_name(String user_full_name) {
            this.user_full_name = user_full_name;
        }

        public String getRequest_timestamp() {
            return request_timestamp;
        }

        public void setRequest_timestamp(String request_timestamp) {
            this.request_timestamp = request_timestamp;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getPan_id() {
            return pan_id;
        }

        public void setPan_id(String pan_id) {
            this.pan_id = pan_id;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }
    }
}

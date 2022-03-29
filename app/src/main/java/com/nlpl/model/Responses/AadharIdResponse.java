package com.nlpl.model.Responses;

import java.util.List;

public class AadharIdResponse {
    private String success;
    private List<UserList> data;

    public List<UserList> getData() {
        return data;
    }

    public void setData(List<UserList> data) {
        this.data = data;
    }

    public class UserList{
    private String request_id;

    //getters and setters

        public String getRequest_id() {
            return request_id;
        }

        public void setRequest_id(String request_id) {
            this.request_id = request_id;
        }
    }
}

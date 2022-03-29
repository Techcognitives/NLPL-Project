package com.nlpl.model.Responses;

import java.util.List;

public class DLVerificationResponse {
    private String success;
    private List<UserList> data;

    public List<UserList> getData() {
        return data;
    }

    public void setData(List<UserList> data) {
        this.data = data;
    }

    public class UserList {
        private String success;

        public String getSuccess() {
            return success;
        }

        public void setSuccess(String success) {
            this.success = success;
        }
    }
}

package com.nlpl.model.Responses;

import java.util.List;

public class AadharIdResponse {
    private String success;
    private List<aadharDetailList> data;

    public List<aadharDetailList> getData() {
        return data;
    }

    public void setData(List<aadharDetailList> data) {
        this.data = data;
    }

    public class aadharDetailList{
        private String request_id,task_id ,group_id, success, response_code, response_message;

        public String getTask_id() {
            return task_id;
        }

        public void setTask_id(String task_id) {
            this.task_id = task_id;
        }

        public String getGroup_id() {
            return group_id;
        }

        public void setGroup_id(String group_id) {
            this.group_id = group_id;
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

        public String getRequest_id() {
            return request_id;
        }

        public void setRequest_id(String request_id) {
            this.request_id = request_id;
        }
    }
}

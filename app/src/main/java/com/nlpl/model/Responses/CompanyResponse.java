package com.nlpl.model.Responses;

public class CompanyResponse {
    private String success;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data {

        private String company_id, company_name, company_gst_no, company_pan, comp_state, comp_city, comp_add, user_id, comp_zip;

        public Data(String company_id, String company_name, String company_gst_no, String company_pan, String comp_state, String comp_city, String comp_add, String user_id, String comp_zip) {
            this.company_id = company_id;
            this.company_name = company_name;
            this.company_gst_no = company_gst_no;
            this.company_pan = company_pan;
            this.comp_state = comp_state;
            this.comp_city = comp_city;
            this.comp_add = comp_add;
            this.user_id = user_id;
            this.comp_zip = comp_zip;
        }

        public String getCompany_id() {
            return company_id;
        }

        public void setCompany_id(String company_id) {
            this.company_id = company_id;
        }

        public String getCompany_name() {
            return company_name;
        }

        public void setCompany_name(String company_name) {
            this.company_name = company_name;
        }

        public String getCompany_gst_no() {
            return company_gst_no;
        }

        public void setCompany_gst_no(String company_gst_no) {
            this.company_gst_no = company_gst_no;
        }

        public String getCompany_pan() {
            return company_pan;
        }

        public void setCompany_pan(String company_pan) {
            this.company_pan = company_pan;
        }

        public String getComp_state() {
            return comp_state;
        }

        public void setComp_state(String comp_state) {
            this.comp_state = comp_state;
        }

        public String getComp_city() {
            return comp_city;
        }

        public void setComp_city(String comp_city) {
            this.comp_city = comp_city;
        }

        public String getComp_add() {
            return comp_add;
        }

        public void setComp_add(String comp_add) {
            this.comp_add = comp_add;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getComp_zip() {
            return comp_zip;
        }

        public void setComp_zip(String comp_zip) {
            this.comp_zip = comp_zip;
        }
    }

}

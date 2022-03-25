package com.nlpl.model.Responses;

import java.util.List;

public class AdminResponse {
    private String success;
    private List<adminFeesList> data;

    public List<adminFeesList> getData() {
        return data;
    }

    public void setData(List<adminFeesList> data) {
        this.data = data;
    }

    public class adminFeesList{
        private String base_platform_fees, platform_fee_one, created_at, updated_by, updated_at, admin_id;

        public String getBase_platform_fees() {
            return base_platform_fees;
        }

        public void setBase_platform_fees(String base_platform_fees) {
            this.base_platform_fees = base_platform_fees;
        }

        public String getPlatform_fee_one() {
            return platform_fee_one;
        }

        public void setPlatform_fee_one(String platform_fee_one) {
            this.platform_fee_one = platform_fee_one;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getUpdated_by() {
            return updated_by;
        }

        public void setUpdated_by(String updated_by) {
            this.updated_by = updated_by;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
        }

        public String getAdmin_id() {
            return admin_id;
        }

        public void setAdmin_id(String admin_id) {
            this.admin_id = admin_id;
        }
    }
}

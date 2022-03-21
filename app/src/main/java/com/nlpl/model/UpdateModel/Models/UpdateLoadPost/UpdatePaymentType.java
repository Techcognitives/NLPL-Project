package com.nlpl.model.UpdateModel.Models.UpdateLoadPost;

public class UpdatePaymentType {

    private String payment_type;

    public UpdatePaymentType(String payment_type) {
        this.payment_type = payment_type;
    }

    @Override
    public String toString() {
        return "UpdatePaymentType{" +
                "payment_type='" + payment_type + '\'' +
                '}';
    }

    public String getPayment_type() {
        return payment_type;
    }

    public void setPayment_type(String payment_type) {
        this.payment_type = payment_type;
    }
}

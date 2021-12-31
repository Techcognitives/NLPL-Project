package com.nlpl.model.UpdateBankDetails;

public class UpdateBankReEnterAccountNumber {

    private String re_enter_acc_num;

    public UpdateBankReEnterAccountNumber(String re_enter_acc_num) {
        this.re_enter_acc_num = re_enter_acc_num;
    }

    @Override
    public String toString() {
        return "UpdateBankReEnterAccountNumber{" +
                "re_enter_acc_num='" + re_enter_acc_num + '\'' +
                '}';
    }

    public String getRe_enter_acc_num() {
        return re_enter_acc_num;
    }

    public void setRe_enter_acc_num(String re_enter_acc_num) {
        this.re_enter_acc_num = re_enter_acc_num;
    }
}

package com.nlpl.model.UpdateUserDetails;

public class UpdateUserIsRegistrationDone {

    private String isRegistration_done;

    public UpdateUserIsRegistrationDone(String isRegistration_done) {
        this.isRegistration_done = isRegistration_done;
    }

    @Override
    public String toString() {
        return "UpdateUserIsRegistrationDone{" +
                "isRegistration_done='" + isRegistration_done + '\'' +
                '}';
    }

    public String getIsRegistration_done() {
        return isRegistration_done;
    }

    public void setIsRegistration_done(String isRegistration_done) {
        this.isRegistration_done = isRegistration_done;
    }
}

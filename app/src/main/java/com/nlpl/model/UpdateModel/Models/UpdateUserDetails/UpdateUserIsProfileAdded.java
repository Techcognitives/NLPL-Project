package com.nlpl.model.UpdateModel.Models.UpdateUserDetails;

public class UpdateUserIsProfileAdded {
    String isProfile_pic_added;

    public UpdateUserIsProfileAdded(String isProfile_pic_added) {
        this.isProfile_pic_added = isProfile_pic_added;
    }

    @Override
    public String toString() {
        return "UpdateUserIsProfileAdded{" +
                "isProfile_pic_added='" + isProfile_pic_added + '\'' +
                '}';
    }
}

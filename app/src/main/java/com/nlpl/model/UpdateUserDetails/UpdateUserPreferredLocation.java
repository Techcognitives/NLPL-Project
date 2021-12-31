package com.nlpl.model.UpdateUserDetails;

public class UpdateUserPreferredLocation {

    private String preferred_location;

    public UpdateUserPreferredLocation(String preferred_location) {
        this.preferred_location = preferred_location;
    }

    @Override
    public String toString() {
        return "UpdateUserPreferredLocation{" +
                "preferred_location='" + preferred_location + '\'' +
                '}';
    }

    public String getPreferred_location() {
        return preferred_location;
    }

    public void setPreferred_location(String preferred_location) {
        this.preferred_location = preferred_location;
    }
}

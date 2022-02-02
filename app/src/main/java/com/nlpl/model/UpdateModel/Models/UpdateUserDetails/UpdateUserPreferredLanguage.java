package com.nlpl.model.UpdateModel.Models.UpdateUserDetails;

public class UpdateUserPreferredLanguage {

    private String preferred_language;

    public UpdateUserPreferredLanguage(String preferred_language) {
        this.preferred_language = preferred_language;
    }

    @Override
    public String toString() {
        return "UpdateUserPreferredLanguage{" +
                "preferred_language='" + preferred_language + '\'' +
                '}';
    }

    public String getPreferred_language() {
        return preferred_language;
    }

    public void setPreferred_language(String preferred_language) {
        this.preferred_language = preferred_language;
    }
}

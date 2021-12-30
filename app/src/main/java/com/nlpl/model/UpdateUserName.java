package com.nlpl.model;

public class UpdateUserName {

    private String name;

    public UpdateUserName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "UserUpdate{" +
                "name='" + name + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}

package com.nlpl.model;

public class ImageRequest {
    String user_id, image_type;

    public ImageRequest() {
        this.user_id = user_id;
        this.image_type = image_type ;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getImage_type() {
        return image_type;
    }

    public void setImage_type(String image_type) {
        this.image_type = image_type;
    }
}

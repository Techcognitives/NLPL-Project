package com.nlpl.model.UpdateModel.Models.UpdateLoadPost;

public class UpdateCount {
    int sp_count;

    public UpdateCount(int sp_count) {
        this.sp_count = sp_count;
    }

    @Override
    public String toString() {
        return "UpdateCount{" +
                "sp_count=" + sp_count +
                '}';
    }
}

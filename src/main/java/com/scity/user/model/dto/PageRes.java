package com.scity.user.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class PageRes<T> {
    private List<T> content;
    private int page;
    private int size;
    private int total;

    public PageRes(List<T> content, int page, int size, int total) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.total = total;
    }
}

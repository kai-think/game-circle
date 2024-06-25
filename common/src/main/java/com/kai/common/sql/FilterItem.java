package com.kai.common.sql;

import lombok.Data;

@Data
public class FilterItem {
    String column;
    String compare;
    Object value;

    public FilterItem() {
    }

    public FilterItem(String column, String compare, Object value) {
        this.column = column;
        this.compare = compare;
        this.value = value;
    }
}
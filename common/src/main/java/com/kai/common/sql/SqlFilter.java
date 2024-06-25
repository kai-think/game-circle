package com.kai.common.sql;

import lombok.Data;

import java.util.Iterator;
import java.util.List;

@Data
public class SqlFilter implements Iterable<FilterItem>{
    public static final Integer EQ = 0;

    List<FilterItem> items;

    public SqlFilter(List<FilterItem> items) {
        this.items = items;
    }

    public SqlFilter() {
    }

    @Override
    public Iterator<FilterItem> iterator() {
        return items.iterator();
    }
}

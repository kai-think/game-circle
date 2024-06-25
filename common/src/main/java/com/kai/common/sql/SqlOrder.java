package com.kai.common.sql;

import lombok.Data;
import lombok.NonNull;

import java.util.*;

@Data
public class SqlOrder implements Iterable<Map.Entry<String, Boolean>>{
    Map<String, Boolean> orderMap;

    public SqlOrder() {
    }

    public void put(@NonNull String orderBy, Boolean asc) {
        if (orderMap == null)
            orderMap = new LinkedHashMap<String, Boolean>();

        orderMap.put(orderBy, asc == null ? true : asc);
    }

    @Override
    public Iterator<Map.Entry<String, Boolean>> iterator() {
        return orderMap.entrySet().iterator();
    }
}

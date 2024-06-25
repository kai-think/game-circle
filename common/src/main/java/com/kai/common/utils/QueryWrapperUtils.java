package com.kai.common.utils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.common.sql.FilterItemSuper;
import com.example.demo.utils.converter.HumpLineConverter;

import java.util.List;

public class QueryWrapperUtils {

    public static void setQueryWrapperOrderByList(QueryWrapper<?> wrapper, List<String> orderByList) {
        if (orderByList == null)
            return;

        orderByList.forEach(o -> {
            String[] part = o.split(" ");
            if (part.length == 0)
                return;
            if (part.length == 1)
                wrapper.orderByAsc(part[0]);
            else
                wrapper.orderBy(true, !part[1].toLowerCase().equals("desc"), part[0]);
        });
    }

    public static String setQueryWrapperFilterList(QueryWrapper<?> wrapper, List<FilterItemSuper> filterList) {
        if (filterList == null)
            return null;

        for (FilterItemSuper item : filterList) {
            if (item.getColumn() == null)
                return "没有选择列";

            String column = HumpLineConverter.humpToLine(item.getColumn());
            List<Object> values = item.getValues();
            if (values == null || values.size() == 0)
            {
                switch (item.getCompare().toLowerCase()) {
                    case "isnull":
                    case "isnotnull": break;
                    default: return "列" + column + "，没有比较值";
                }
            }

            String logic = item.getLogic();
            if (logic != null)
            {
                logic = logic.toLowerCase();
                if (logic.equals("or"))
                    wrapper.or();
            }

            switch (item.getCompare().toLowerCase())
            {
                case "eq": wrapper.eq(column, values.get(0)); break;
                case "ge": wrapper.ge(column, values.get(0)); break;
                case "le": wrapper.le(column, values.get(0)); break;
                case "gt": wrapper.gt(column, values.get(0)); break;
                case "lt": wrapper.lt(column, values.get(0)); break;
                case "like": wrapper.like(column, values.get(0)); break;
                case "ne": wrapper.ne(column, values.get(0)); break;
                case "in": wrapper.in(column, values); break;
                case "isnull": wrapper.isNull(column); break;
                case "isnotnull": wrapper.isNotNull(column); break;
                case "regex":
                case "regexp": wrapper.apply("{0} regexp {1}", column, values.get(0)); break;
                case "between": {
                    if (values.size() < 2)
                        return "列" + column + "，需要两个值";

                    wrapper.between(column, values.get(0), values.get(1));
                    break;
                }
                default: return "错误比较符：" + item.getCompare();
            }
        }

        return null;
    }
}

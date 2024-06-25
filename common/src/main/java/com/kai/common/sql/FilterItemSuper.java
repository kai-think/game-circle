package com.kai.common.sql;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class FilterItemSuper {
    @ApiModelProperty("and 或 or")
    String logic;
    @ApiModelProperty("列名，比如 id")
    String column;
    @ApiModelProperty("比如符，比如 eq, like, between, regexp")
    String compare;

    @ApiModelProperty("值")
    List<Object> values;
}

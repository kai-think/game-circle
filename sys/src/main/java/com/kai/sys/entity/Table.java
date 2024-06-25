package com.kai.sys.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.kai.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * <p>
 * 
 * </p>
 *
 * @author zqy
 * @since 2021-08-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_table")
@ApiModel(value="Table对象", description="")
public class Table extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "类型，0为数据表，1为视图")
    private Integer type;

    @ApiModelProperty(value = "表名")
    private String name;

    @ApiModelProperty(value = "后端对应相对路径，如 /sys/table")
    private String path;

    @ApiModelProperty(value = "描述")
    private String descr;

    @ApiModelProperty(value = "当类型为视图时，需要一张表作基础表，这是基础表的id，同时基础表必须是数据表")
    private Integer viewBaseTable;

    @ApiModelProperty(value = "视图的sql查询语句")
    private String viewSql;

    @TableField(exist = false)
    private List<TableCol> cols;
}

package com.kai.sys.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.kai.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
@TableName("sys_table_col")
@ApiModel(value="TableCol对象", description="")
public class TableCol extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private Integer tableId;

    @ApiModelProperty(value = "类型字典，对应字典里的id")
    private Integer type;

    @ApiModelProperty(value = "标记名字")
    private String name;

    @ApiModelProperty(value = "表名")
    private String value;

    @ApiModelProperty(value = "显示宽度")
    private Integer width;

    @ApiModelProperty(value = "描述")
    private String descr;

    @ApiModelProperty(value = "显示为 1， 不显示为 0")
    @TableField(value = "`show`")
    private Integer show;

    @ApiModelProperty(value = "不可用 为 1， 可用为 0")
    @TableField(value = "`disable`")
    private Integer disable;

    @ApiModelProperty(value = "难验证规则列表，对应的字典id，用 , 分开")
    private String ruleDicIds;

    @ApiModelProperty(value = "值格式化用的字典id")
    private Integer formatDicId;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "外键对应表的id")
    private String fkTable;

    @ApiModelProperty(value = "外键对应表的主键id")
    private String fkCol;

    @ApiModelProperty(value = "外键对应表的要显示的列")
    private String fkShowCol;
}

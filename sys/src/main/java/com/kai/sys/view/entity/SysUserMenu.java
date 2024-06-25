package com.kai.sys.view.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.kai.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * VIEW
 * </p>
 *
 * @author zqy
 * @since 2021-08-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="SysUserMenu对象", description="VIEW")
public class SysUserMenu extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "自动生成主键")
    private Integer userId;

    @ApiModelProperty(value = "图标")
    private String icon;

    @ApiModelProperty(value = "菜单名")
    @TableField(value = "`name`")
    private String name;

    @ApiModelProperty(value = "路径，可以是组件路径或链接")
    @TableField(value = "`path`")
    private String path;

    @ApiModelProperty(value = "描述")
    private String descr;

    @ApiModelProperty(value = "父菜单id，-1表示没有父菜单")
    private Integer parentId;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "组件")
    @TableField(value = "`component`")
    private String component;

    @ApiModelProperty(value = "对应的 sys_table 的 id")
    private Integer tableId;

    @ApiModelProperty(value = "菜单类型，可以是菜单或按钮，对应字典里的类型")
    private Integer type;
}

package com.kai.sys.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.kai.common.BaseEntity;
import com.kai.common.EnableListToTree;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * <p>
 * 系统菜单
 * </p>
 *
 * @author zqy
 * @since 2021-08-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_menu")
@ApiModel(value="Menu对象", description="系统菜单")
public class Menu extends BaseEntity implements EnableListToTree<Menu>{

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "图标")
    private String icon;

    @ApiModelProperty(value = "菜单名")
    private String name;

    @ApiModelProperty(value = "路径，可以是组件路径或链接")
    private String path;

    @ApiModelProperty(value = "组件")
    private String component;

    @ApiModelProperty(value = "描述")
    private String descr;
    
    @ApiModelProperty(value = "父菜单id，-1表示没有父菜单")
    private Integer parentId;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "菜单类型，可以是菜单或按钮，对应字典里的类型")
    private Integer type;

    @TableField(exist = false)
    private List<Menu> children;

    @ApiModelProperty(value = "对应的 sys_table 的 id")
    private Integer tableId;

    @Override
    public Object id() {
        return getId();
    }

    @Override
    public Object parentId() {
        return getParentId();
    }

    @Override
    public List<Menu> children() {
        return getChildren();
    }
}

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
 * 数据字典
 * </p>
 *
 * @author zqy
 * @since 2021-08-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dic")
@ApiModel(value="Dic对象", description="数据字典")
public class Dic extends BaseEntity implements EnableListToTree<Dic> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "名")
    private String name;

    @ApiModelProperty(value = "对应的 数字类型 值")
    private Integer value;

    @ApiModelProperty(value = "对应的字符类型值")
    private String stringValue;

    @ApiModelProperty(value = "父级字典id，顶级为空")
    private Integer parentId;

    @ApiModelProperty(value = "排序，数字越小越靠前")
    private Integer sort;

    @ApiModelProperty(value = "描述")
    private String descr;

    @ApiModelProperty(value = "标签颜色")
    private String color;

    @TableField(exist = false)
    private List<Dic> children;

    @Override
    public Object id() {
        return getId();
    }

    @Override
    public Object parentId() {
        return getParentId();
    }

    @Override
    public List<Dic> children() {
        return getChildren();
    }

    @Override
    public void setChildren(List<Dic> list) {
        children = list;
    }
}

package com.kai.sys.entity;

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
 * @since 2021-08-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role_menu")
@ApiModel(value="RoleMenu对象", description="")
public class RoleMenu extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "系统菜单id")
    private Integer menuId;

    @ApiModelProperty(value = "系统角色id")
    private Integer roleId;


}

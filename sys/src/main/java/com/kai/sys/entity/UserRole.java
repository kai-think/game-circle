package com.kai.sys.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.kai.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 用户、角色关联表
 * </p>
 *
 * @author zqy
 * @since 2021-08-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user_role")
@ApiModel(value="UserRole对象", description="用户、角色关联表")
public class UserRole extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private Integer userId;

    private Integer roleId;

    private String limitation;
}

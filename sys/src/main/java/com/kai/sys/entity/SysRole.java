package com.kai.sys.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.kai.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * <p>
 * 角色表
 * </p>
 *
 * @author zqy
 * @since 2021-08-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
@ApiModel(value="Role对象", description="角色表")
public class SysRole extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "角色名")
    private String name;

    @ApiModelProperty(value = "是否可用，1、0 表示 可用、不可用")
    private Integer useful;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "角色描述")
    private String descr;

    @ApiModelProperty(value = "排序")
    private Integer sort;
}

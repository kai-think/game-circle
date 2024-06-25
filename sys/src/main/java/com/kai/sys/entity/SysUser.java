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
 * 
 * </p>
 *
 * @author zqy
 * @since 2021-08-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
@ApiModel(value="User对象", description="")
public class SysUser extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "密码")
    private String password;

    private String salt;

    @ApiModelProperty(value = "昵称")
    private String nickname;

    @ApiModelProperty(value = "是否可用 0、1 表示 不可用、可用")
    private Integer useful;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "头像的相对路径")
    private String avatar;
}

package com.kai.sys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kai.sys.entity.UserRole;
import com.kai.sys.mapper.UserRoleMapper;
import com.kai.sys.service.IUserRoleService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户、角色关联表 服务实现类
 * </p>
 *
 * @author zqy
 * @since 2021-08-12
 */
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements IUserRoleService {

}

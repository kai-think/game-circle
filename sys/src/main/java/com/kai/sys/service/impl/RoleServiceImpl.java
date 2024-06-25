package com.kai.sys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kai.sys.entity.SysRole;
import com.kai.sys.mapper.RoleMapper;
import com.kai.sys.service.IRoleService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 角色表 服务实现类
 * </p>
 *
 * @author zqy
 * @since 2021-08-12
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, SysRole> implements IRoleService {

}

package com.kai.sys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kai.sys.entity.Menu;
import com.kai.sys.mapper.MenuMapper;
import com.kai.sys.service.IMenuService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 系统菜单 服务实现类
 * </p>
 *
 * @author zqy
 * @since 2021-08-12
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements IMenuService {

}

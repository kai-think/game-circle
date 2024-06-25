package com.kai.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kai.sys.entity.Menu;
import com.kai.sys.entity.PermissionPath;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 系统菜单 Mapper 接口
 * </p>
 *
 * @author zqy
 * @since 2021-08-07
 */
public interface MenuMapper extends BaseMapper<Menu> {
    @Select("SELECT count(*) FROM sys_user_menu\n" +
            "where user_id = #{param1} and id in (\n" +
            "\tselect id from\n" +
            "\t(\n" +
            "\t\tSELECT a.id, a.name, a.component, concat(b.path, a.path) as path FROM sys_menu a, sys_menu b\n" +
            "\t\twhere a.parent_id = b.id and a.type = 2 and b.type in (1, 2)\n" +
            "\t) as c\n" +
            "\twhere path = #{param2}\n" +
            ");")
    int userHasPermission(Integer userId, String path);

    @Select("select a.id as role_id, c.id2 as menu_id, c.path1, c.path2 from sys_role a, sys_role_menu b,\n" +
            "(\n" +
            "\tSELECT b.id as id1, a.id as id2, b.path as path1, a.path as path2 FROM sys_menu a, sys_menu b\n" +
            "\twhere a.parent_id = b.id and a.type = 2 and b.type in (1, 2)\n" +
            ") c\n" +
            "where a.id = b.role_id and b.menu_id = c.id2;")
    List<PermissionPath> getAllPermissionPath();
}

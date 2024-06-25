package com.kai.sys.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kai.common.BaseController;
import com.kai.common.ListToTree;
import com.kai.common.sql.FilterItem;
import com.kai.common.config.encrypt.EncryptedRequestBody;
import com.kai.common.config.resolver.JsonRequestBody;
import com.kai.common.config.resolver.JsonToList;
import com.kai.sys.entity.Menu;
import com.kai.sys.entity.RoleMenu;
import com.kai.sys.entity.SysUser;
import com.kai.sys.mapper.MenuMapper;
import com.kai.sys.service.impl.MenuServiceImpl;
import com.kai.sys.service.impl.RoleMenuServiceImpl;
import com.kai.sys.view.controller.SysUserMenuController;
import com.kai.sys.view.entity.SysUserMenu;
import com.kai.common.utils.CusFunctionInteface;
import com.kai.common.utils.converter.CusConverter;
import com.kai.common.utils.converter.HumpLineConverter;
import com.kai.common.utils.httpresult.FailResult;
import com.kai.common.utils.httpresult.HttpResult;
import com.kai.common.utils.httpresult.SuccessResult;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author zqy
* @since 2021-08-17
*/
@RestController
@RequestMapping("/sys/menu")
public class SysMenuController extends BaseController {

    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private MenuServiceImpl menuService;

    @Autowired
    private SysUserMenuController userMenuController;

    @Autowired
    private RoleMenuServiceImpl roleMenuService;

    @ApiImplicitParams({
    @ApiImplicitParam(name = "current",paramType = "query",value = "当前页码",required = false),
    @ApiImplicitParam(name = "size",paramType = "query",value = "每页显示记录数",required = false),
    @ApiImplicitParam(name = "icon",paramType = "query",value = "图标",required = false),
    @ApiImplicitParam(name = "name",paramType = "query",value = "菜单名",required = false),
    @ApiImplicitParam(name = "component",paramType = "query",value = "组件对应的路径",required = false),
    @ApiImplicitParam(name = "path",paramType = "query",value = "路径，可以是组件路径或链接",required = false),
    @ApiImplicitParam(name = "descr",paramType = "query",value = "描述",required = false),
    @ApiImplicitParam(name = "level",paramType = "query",value = "等级，依次为 0、1、2、3...",required = false),
    @ApiImplicitParam(name = "parentId",paramType = "query",value = "父菜单id，-1表示没有父菜单",required = false),
    @ApiImplicitParam(name = "tableId",paramType = "query",value = "对应的 sys_table 的 id",required = false),
    @ApiImplicitParam(name = "sort",paramType = "query",value = "排序",required = false)})
    @ApiOperation(value = "分页获取Menu信息")
    @PostMapping("/page")
    @EncryptedRequestBody
    public HttpResult<Page<Menu>> page(
        Integer current,
        Integer size,
        String icon,
        String name,
        String component,
        String path,
        String descr,
        Integer level,
        Integer parentId,
        Integer tableId,
        Integer sort) {

        QueryWrapper<Menu> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("sort");
        if (icon != null)
            wrapper.like("icon", icon);
        if (name != null)
            wrapper.like("name", name);
        if (component != null)
            wrapper.like("component", component);
        if (path != null)
            wrapper.like("path", path);
        if (descr != null)
            wrapper.like("descr", descr);
        if (level != null)
            wrapper.eq("level", level);
        if (parentId != null)
            wrapper.eq("parent_id", parentId);
        if (tableId != null)
            wrapper.eq("table_id", tableId);
        if (sort != null)
            wrapper.eq("sort", sort);

        Page<Menu> page = null;
        if (current == null || size == null)
        {
            List<Menu> menuList = menuService.list(wrapper);
            page = new Page<>(1, menuList.size());
            page.setTotal(menuList.size());
            page.setRecords(menuList);
        }
        else
        {
            page = new Page<>(current, size);
            page =  menuService.page(page, wrapper);
        }

        ListToTree.listToTree(page.getRecords());
        return new SuccessResult<>(page);
    }

    @PostMapping("/getByRoleId")
    @EncryptedRequestBody
    public HttpResult<List<Menu>> getByRoleId(Integer roleId) {
        List<RoleMenu> roleMenuList = roleMenuService.list(new QueryWrapper<RoleMenu>().eq("role_id", roleId));
        List<Menu> menuList = roleMenuList.stream()
                .map(roleMenu -> menuService.getById(roleMenu.getMenuId()))
                .sorted(Comparator.comparing(Menu::getSort))
                .collect(Collectors.toList());

        ListToTree.listToTree(menuList);
        return new SuccessResult<>(menuList);
    }

    @PostMapping("/listByCurrentUser")
    @EncryptedRequestBody
    public HttpResult<List<Menu>> listByCurrentUser() {
        SysUser sysUser = getUser();
        if (sysUser.getId() == 1)
        {
            List<Menu> menuList = menuService.list(new QueryWrapper<Menu>().orderByAsc("sort"));
            ListToTree.listToTree(menuList);
            return new SuccessResult<>(menuList);
        }

        List<Menu> menuList = userMenuController
                .page(null, null, sysUser.getId(), null, null,
                        null, null, null, null, null)
                .getData()
                .stream()
                .filter(CusFunctionInteface.distinctByKey(SysUserMenu::getId))
                .map(sysUserMenu -> {
                    Menu menu = (Menu) CusConverter.convert(sysUserMenu, Menu.class);
                    menu.setId(sysUserMenu.getId());
                    return menu;
                })
                .collect(Collectors.toList());


        ListToTree.listToTree(menuList);
        return new SuccessResult<>(menuList);
    }

    @ApiImplicitParams({
    @ApiImplicitParam(name = "page",paramType = "query",value = "分页信息，格式类似为{current: 1, size: 5}",required = false),
    @ApiImplicitParam(name = "filter",paramType = "query",value = "查询信息，格式类似为[{name:'name',compare:'like',value:'管理员'}, {name:'id',compare:'eq',value:1}]",required = false),
    @ApiImplicitParam(name = "orderBy",paramType = "query",value = "排序方式，格式类似为['create_time asc','id desc']",required = false)})
    @ApiOperation(value = "分页获取Menu信息")
    @PostMapping("/pagePlus")
    @EncryptedRequestBody
    public HttpResult<Page<Menu>> page(
        Integer current, Integer size,
        @JsonToList(FilterItem.class) List<FilterItem> filter,
        @JsonToList(String.class) List<String> orderBy) {
        QueryWrapper<Menu> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("sort");
        if (orderBy != null)
        {
            orderBy.forEach(o -> {
                String[] part = o.split(" ");
                if (part.length == 0)
                    return;
                if (part.length == 1)
                    wrapper.orderByAsc(part[0]);
                else
                    wrapper.orderBy(true, !part[1].toLowerCase().equals("desc"), part[0]);
            });
        }

        if (filter != null)
            for (FilterItem item : filter) {
                switch (item.getCompare())
                {
                    case "eq": wrapper.eq(item.getColumn(), item.getValue()); break;
                    case "ge": wrapper.ge(item.getColumn(), item.getValue()); break;
                    case "le": wrapper.le(item.getColumn(), item.getValue()); break;
                    case "gt": wrapper.gt(item.getColumn(), item.getValue()); break;
                    case "lt": wrapper.lt(item.getColumn(), item.getValue()); break;
                    case "like": wrapper.like(item.getColumn(), item.getValue()); break;
                    case "ne": wrapper.ne(item.getColumn(), item.getValue()); break;
                }
            }

        Page<Menu> page = null;
        if (current == null || size == null)
        {
            List<Menu> list = menuService.list(wrapper);
            page = new Page<>(1, list.size());
            page.setTotal(list.size());
            page.setRecords(list);
        }
        else
        {
            page = new Page<>(current, size);
            page =  menuService.page(page, wrapper);
        }

        ListToTree.listToTree(page.getRecords());
        return new SuccessResult<>(page);
    }

    @ApiImplicitParam(name = "menuId",paramType = "query",value = "主键",required = true)
    @PostMapping("/getById")
    @EncryptedRequestBody
    public HttpResult<Menu> getById(Integer id) {
        Menu menu = menuService.getById(id);
        if (menu == null)
            return new FailResult<>("不存在");

        return new SuccessResult<>(menu);
    }

    @ApiOperation(value = "保存或修改Menu信息")
    @PostMapping("/save")
    @EncryptedRequestBody
    public HttpResult<Menu> save(@JsonRequestBody Menu menu) {
        if (menu == null)
            return new FailResult<>("不能为空");

        if (menu.getId() == null)
            menuMapper.insert(menu);
        else
            menuMapper.updateById(menu);

        return new SuccessResult<>(menu);
    }

    @ApiOperation(value = "根据id批量修改Menu信息")
    @PostMapping("/update")
    @EncryptedRequestBody
    public HttpResult<String> update(@JsonToList(Menu.class) List<Menu> menuList) {
        menuService.updateBatchById(menuList);
        return new SuccessResult<>("修改成功");
    }

    @ApiOperation(value = "根据id批量修改Menu信息")
    @PostMapping("/updatePlus")
    @EncryptedRequestBody
    public HttpResult<String>   updatePlus(@JsonRequestBody String menuList) {
        JSONArray list = JSON.parseArray(menuList);
        for (int i = 0; i < list.size(); i++) {
            JSONObject obj = list.getJSONObject(i);
            UpdateWrapper<Menu> updateWrapper = new UpdateWrapper<>();
            boolean containId = false;
            for (String key : obj.keySet()) {
                Object value = obj.get(key);
                if (key.toLowerCase().equals("id"))
                {
                    updateWrapper.eq("id", value);
                    containId = true;
                }
                else
                    updateWrapper.set(HumpLineConverter.humpToLine(key), value);
            }
            if (containId)
                menuService.update(updateWrapper);
            else
                return new FailResult<>("必须包含id");
        }


        return new SuccessResult<>("修改成功");
    }

    @ApiOperation(value = "通过id列表删除Menu")
    @ApiImplicitParams({
    @ApiImplicitParam(name = "ids",paramType = "query",value = "传入的id列表",required = true)
    })
    @PostMapping("/delete")
    @EncryptedRequestBody
    public HttpResult<String> delete(@JsonToList(Integer.class) List<String> ids) {
        menuService.removeByIds(ids);
        return new SuccessResult<>("删除成功");
    }

    @ApiImplicitParam(name = "menuId",paramType = "query",value = "主键",required = true)
    @PostMapping("/addDefaultPermission")
    @EncryptedRequestBody
    public HttpResult<String> addDefaultPermission(Integer menuId) {
        Menu menu = menuService.getById(menuId);
        if (menu == null)
            return new FailResult<>("不存在主键");

        String[] name = {"增加", "删除", "编辑", "查询"};
        String[] permit = {"add", "delete", "edit", "search"};
        String[] path = {"/save", "/delete", "/update, /updatePlus", "/pageSuper, /pagePlus, /page, /getById"};

        for (int i = 0; i < name.length; i++) {
            Menu btn = new Menu();
            btn.setParentId(menuId);
            btn.setName(name[i]);
            btn.setComponent(permit[i]);
            btn.setPath(path[i]);
            btn.setType(2);

            menuService.save(btn);
        }

        return new SuccessResult<>("添加默认权限成功");
    }
}

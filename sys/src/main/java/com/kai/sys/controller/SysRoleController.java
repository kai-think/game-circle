package com.kai.sys.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kai.common.BaseController;
import com.kai.common.config.encrypt.EncryptedRequestBody;
import com.kai.common.config.resolver.JsonRequestBody;
import com.kai.common.config.resolver.JsonToList;
import com.kai.common.sql.FilterItem;
import com.kai.common.utils.CusFunctionInteface;
import com.kai.common.utils.converter.HumpLineConverter;
import com.kai.common.utils.httpresult.FailResult;
import com.kai.common.utils.httpresult.HttpResult;
import com.kai.common.utils.httpresult.SuccessResult;
import com.kai.sys.entity.RoleMenu;
import com.kai.sys.entity.SysRole;
import com.kai.sys.entity.SysUser;
import com.kai.sys.entity.UserRole;
import com.kai.sys.mapper.RoleMapper;
import com.kai.sys.service.impl.RoleMenuServiceImpl;
import com.kai.sys.service.impl.RoleServiceImpl;
import com.kai.sys.service.impl.SysUserServiceImpl;
import com.kai.sys.service.impl.UserRoleServiceImpl;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author zqy
* @since 2021-08-17
*/
@RestController
@RequestMapping("/sys/role")
    public class SysRoleController extends BaseController {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RoleServiceImpl roleService;

    @Autowired
    private RoleMenuServiceImpl roleMenuService;

    @Autowired
    private UserRoleServiceImpl userRoleService;

    @Autowired
    private SysUserServiceImpl userService;

    @ApiImplicitParams({
    @ApiImplicitParam(name = "current",paramType = "query",value = "当前页码",required = false),
    @ApiImplicitParam(name = "size",paramType = "query",value = "每页显示记录数",required = false),
    @ApiImplicitParam(name = "parentId",paramType = "query",value = "",required = false),
    @ApiImplicitParam(name = "name",paramType = "query",value = "角色名",required = false),
    @ApiImplicitParam(name = "useful",paramType = "query",value = "是否可用，1、0 表示 可用、不可用",required = false),
    @ApiImplicitParam(name = "createTime",paramType = "query",value = "创建时间",required = false),
    @ApiImplicitParam(name = "descr",paramType = "query",value = "角色描述",required = false)})
    @ApiOperation(value = "分页获取Role信息")
    @PostMapping("/page")
    @EncryptedRequestBody
    public HttpResult<Page<SysRole>> page(
        Integer current,
        Integer size,
        Integer parentId,
        String name,
        Integer useful,
        LocalDateTime createTime,
        String descr) {

        QueryWrapper<SysRole> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("sort");
        if (parentId != null)
            wrapper.eq("parent_id", parentId);
        if (name != null)
            wrapper.like("name", name);
        if (useful != null)
            wrapper.eq("useful", useful);
        if (createTime != null)
            wrapper.eq("create_time", createTime);
        if (descr != null)
            wrapper.like("descr", descr);

        Page<SysRole> page = null;
        if (current == null || size == null)
        {
            List<SysRole> sysRoleList = roleService.list(wrapper);
            page = new Page<>(1, sysRoleList.size());
            page.setTotal(sysRoleList.size());
            page.setRecords(sysRoleList);
        }
        else
        {
            page = new Page<>(current, size);
            page =  roleService.page(page, wrapper);
        }

        return new SuccessResult<>(page);
    }

    @ApiImplicitParams({
    @ApiImplicitParam(name = "page",paramType = "query",value = "分页信息，格式类似为{current: 1, size: 5}",required = false),
    @ApiImplicitParam(name = "filter",paramType = "query",value = "查询信息，格式类似为[{name:'name',compare:'like',value:'管理员'}, {name:'id',compare:'eq',value:1}]",required = false),
    @ApiImplicitParam(name = "orderBy",paramType = "query",value = "排序方式，格式类似为['create_time asc','id desc']",required = false)})
    @ApiOperation(value = "分页获取Role信息")
    @PostMapping("/pagePlus")
    @EncryptedRequestBody
    public HttpResult<Page<SysRole>> page(
        Integer current, Integer size,
        @JsonToList(FilterItem.class) List<FilterItem> filter,
        @JsonToList(String.class) List<String> orderBy) {
        QueryWrapper<SysRole> wrapper = new QueryWrapper<>();
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

        Page<SysRole> page = null;
        if (current == null || size == null)
        {
            List<SysRole> list = roleService.list(wrapper);
            page = new Page<>(1, list.size());
            page.setTotal(list.size());
            page.setRecords(list);
        }
        else
        {
            page = new Page<>(current, size);
            page =  roleService.page(page, wrapper);
        }

        return new SuccessResult<>(page);
    }

    @PostMapping("/listByCurrentUser")
    @EncryptedRequestBody
    public HttpResult<List<SysRole>> listByCurrentUser() {
        SysUser sysUser = getUser();
        if (sysUser.getId() == 1)
        {
            List<SysRole> sysRoleList = roleService.list(new QueryWrapper<SysRole>());
            return new SuccessResult<>(sysRoleList);
        }

        List<SysRole> sysRoleList = userRoleService.list(new QueryWrapper<UserRole>().eq("user_id", sysUser.getId()))
                .stream()
                .filter(CusFunctionInteface.distinctByKey(UserRole::getRoleId))
                .map(userRole -> roleService.getById(userRole.getRoleId()))
                .collect(Collectors.toList());

        return new SuccessResult<>(sysRoleList);
    }

    @ApiImplicitParam(name = "roleId",paramType = "query",value = "主键",required = true)
    @PostMapping("/getById")
    @EncryptedRequestBody
    public HttpResult<SysRole> getById(Integer id) {
        SysRole sysRole = roleService.getById(id);
        if (sysRole == null)
            return new FailResult<>("不存在");

        return new SuccessResult<>(sysRole);
    }

    @ApiOperation(value = "保存或修改Role信息")
    @PostMapping("/save")
    @EncryptedRequestBody
    public HttpResult<SysRole> save(@JsonRequestBody SysRole sysRole) {
        if (sysRole == null)
            return new FailResult<>("不存在对象");

        if (sysRole.getId() == null)
            roleMapper.insert(sysRole);
        else
            roleMapper.updateById(sysRole);

        return new SuccessResult<>(sysRole);
    }

    @ApiOperation(value = "根据id批量修改Role信息")
    @PostMapping("/update")
    @EncryptedRequestBody
    public HttpResult<String> update(@JsonToList(SysRole.class) List<SysRole> sysRoleList) {
        roleService.updateBatchById(sysRoleList);
        return new SuccessResult<>("修改成功");
    }

    @ApiOperation(value = "根据id批量修改Role信息")
    @PostMapping("/updatePlus")
    @EncryptedRequestBody
    public HttpResult<String> updatePlus(@JsonRequestBody String roleList) {
        JSONArray list = JSON.parseArray(roleList);
        for (int i = 0; i < list.size(); i++) {
            JSONObject obj = list.getJSONObject(i);
            UpdateWrapper<SysRole> updateWrapper = new UpdateWrapper<>();
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
                roleService.update(updateWrapper);
            else
                return new FailResult<>("必须包含id");
        }


        return new SuccessResult<>("修改成功");
    }

    @ApiOperation(value = "通过id列表删除Role")
    @ApiImplicitParams({
    @ApiImplicitParam(name = "ids",paramType = "query",value = "传入的id列表",required = true)
    })
    @PostMapping("/delete")
    @EncryptedRequestBody
    public HttpResult<String> delete(@JsonToList(Integer.class) List<String> ids) {
        roleService.removeByIds(ids);
        return new SuccessResult<>("删除成功");
    }

    @PostMapping("/assignMenu")
    @EncryptedRequestBody
    public HttpResult<String> assignMenu(@NonNull Integer roleId, @JsonToList(Integer.class) List<Integer> menuIds) {
        SysRole sysRole = roleService.getById(roleId);
        if (sysRole == null)
            return new FailResult<>("角色不存在");
        roleMenuService.remove(new QueryWrapper<RoleMenu>().eq("role_id", roleId));
        List<RoleMenu> roleMenuList = menuIds.stream().map(id -> {
            RoleMenu roleMenu = new RoleMenu();
            roleMenu.setRoleId(roleId);
            roleMenu.setMenuId(id);
            return roleMenu;
        }).collect(Collectors.toList());

        roleMenuService.saveBatch(roleMenuList);
        return new SuccessResult<>("分配成功");
    }

    @ApiImplicitParam(name = "roleId",paramType = "query",value = "主键",required = true)
    @PostMapping("/listByUser")
    @EncryptedRequestBody
    public HttpResult<List<SysRole>> listByUser(Integer userId) {
        SysUser sysUser = null;
        if (userId == null)
            sysUser = getUser();
        else
            sysUser = userService.getById(userId);

        if (sysUser == null)
            return new FailResult<>("无效用户");

        if (sysUser.getId() == 1)
        {
            List<SysRole> sysRoleList = roleService.list(new QueryWrapper<SysRole>().orderByAsc("sort"));
            return new SuccessResult<>(sysRoleList);
        }

        List<UserRole> userRoles = userRoleService.list(new QueryWrapper<UserRole>().eq("user_id", sysUser.getId()));
        List<SysRole> sysRoleList = userRoles.stream()
                .map(userRole -> {
                    SysRole role = roleService.getById(userRole.getRoleId());
                    role.putEtc("limitation", userRole.getLimitation());
                    return role;
                })
                .sorted(Comparator.comparing(SysRole::getSort))
                .collect(Collectors.toList());


        return new SuccessResult<>(sysRoleList);
    }

    @PostMapping("/getFirstByCurrentUser")
    @EncryptedRequestBody
    public HttpResult<SysRole> getByCurrentUser() {
        SysUser sysUser = getUser();

        List<SysRole> sysRoleList = userRoleService
                .page(new Page<>(1, 1),
                        new QueryWrapper<UserRole>().eq("user_id", sysUser.getId()))
                .getRecords()
                .stream()
                .map(userRole -> roleService.getById(userRole.getRoleId()))
                .collect(Collectors.toList());

        if (sysRoleList.size() > 0)
            return new SuccessResult<>(sysRoleList.get(0));

        return new SuccessResult<>(null);
    }

    @PostMapping("/listUserRoleByCurrentUser")
    @EncryptedRequestBody
    public HttpResult<List<UserRole>> listUserRoleByCurrentUser() {
        SysUser sysUser = getUser();

        List<UserRole> userRoleList = userRoleService
                .list(Wrappers.<UserRole>lambdaQuery()
                        .eq(UserRole::getUserId, sysUser.getId()));

        return new SuccessResult<>(userRoleList);
    }
}

package com.kai.sys.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kai.common.BaseController;
import com.kai.common.config.encrypt.EncryptedRequestBody;
import com.kai.common.config.resolver.JsonRequestBody;
import com.kai.common.config.resolver.JsonToList;
import com.kai.common.sql.FilterItem;
import com.kai.common.sql.FilterItemSuper;
import com.kai.common.utils.converter.HumpLineConverter;
import com.kai.common.utils.httpresult.FailResult;
import com.kai.common.utils.httpresult.HttpResult;
import com.kai.common.utils.httpresult.SuccessResult;
import com.kai.sys.entity.RoleMenu;
import com.kai.sys.mapper.RoleMenuMapper;
import com.kai.sys.service.impl.RoleMenuServiceImpl;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
* @author zqy
* @since 2021-10-16
*/
@RestController
@RequestMapping("/sys/role-menu")
public class SysRoleMenuController extends BaseController {

    @Autowired
    private RoleMenuMapper roleMenuMapper;

    @Autowired
    private RoleMenuServiceImpl roleMenuService;

    @ApiImplicitParams({
    @ApiImplicitParam(name = "current",paramType = "query",value = "当前页码",required = false),
    @ApiImplicitParam(name = "size",paramType = "query",value = "每页显示记录数",required = false),
    @ApiImplicitParam(name = "menuId",paramType = "query",value = "系统菜单id",required = false),
    @ApiImplicitParam(name = "roleId",paramType = "query",value = "系统角色id",required = false)})
    @ApiOperation(value = "分页获取RoleMenu信息")
    @PostMapping("/page")
    @EncryptedRequestBody
    public HttpResult<Page<RoleMenu>> page(
        Integer current,
        Integer size,
        Integer menuId,
        Integer roleId) {

        QueryWrapper<RoleMenu> wrapper = new QueryWrapper<>();
        if (menuId != null)
            wrapper.eq("menu_id", menuId);
        if (roleId != null)
            wrapper.eq("role_id", roleId);

        Page<RoleMenu> page = null;
        if (current == null || size == null)
        {
            List<RoleMenu> roleMenuList = roleMenuService.list(wrapper);
            page = new Page<>(1, roleMenuList.size());
            page.setTotal(roleMenuList.size());
            page.setRecords(roleMenuList);
        }
        else
        {
            page = new Page<>(current, size);
            page =  roleMenuService.page(page, wrapper);
        }

        return new SuccessResult<>(page);
    }

    @ApiImplicitParams({
    @ApiImplicitParam(name = "page",paramType = "query",value = "分页信息，格式类似为{current: 1, size: 5}",required = false),
    @ApiImplicitParam(name = "filter",paramType = "query",value = "查询信息，格式类似为[{name:'name',compare:'like',value:'管理员'}, {name:'id',compare:'eq',value:1}]",required = false),
    @ApiImplicitParam(name = "orderBy",paramType = "query",value = "排序方式，格式类似为['create_time asc','id desc']",required = false)})
    @ApiOperation(value = "分页获取RoleMenu信息")
    @PostMapping("/pagePlus")
    @EncryptedRequestBody
    public HttpResult<Page<RoleMenu>> page(
        Integer current, Integer size,
        @JsonToList(FilterItem.class) List<FilterItem> filter,
        @JsonToList(String.class) List<String> orderBy) {
        QueryWrapper<RoleMenu> wrapper = new QueryWrapper<>();

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

        Page<RoleMenu> page = null;
        if (current == null || size == null)
        {
            List<RoleMenu> list = roleMenuService.list(wrapper);
            page = new Page<>(1, list.size());
            page.setTotal(list.size());
            page.setRecords(list);
        }
        else
        {
            page = new Page<>(current, size);
            page =  roleMenuService.page(page, wrapper);
        }

        return new SuccessResult<>(page);
    }

    @ApiImplicitParams({
    @ApiImplicitParam(name = "current",paramType = "query",value = "分页信息，当前页",required = false),
    @ApiImplicitParam(name = "size",paramType = "query",value = "分页信息，每页大小",required = false),
    @ApiImplicitParam(name = "filterList",paramType = "query",value = "查询信息，格式类似为[\n" +
    "    {\"column\": \"title\", \"compare\": \"like\", \"values\": [\"一个文章\"]},\n" +
    "    {\"logic\": \"or\", \"column\": \"id\", \"compare\": \"in\", \"values\": [1,2,4,5]},\n" +
    "  ]",required = false),
    @ApiImplicitParam(name = "orderByList",paramType = "query",value = "排序方式，格式类似为['create_time asc','id desc']",required = false)})
    @ApiOperation(value = "分页获取RoleMenu信息")
    @PostMapping("/pageSuper")
    @EncryptedRequestBody
    public HttpResult<Page<RoleMenu>> pageSuper(
        Integer current, Integer size,
        @JsonToList(FilterItemSuper.class) List<FilterItemSuper> filterList,
        @JsonToList(String.class) List<String> orderByList) {
        QueryWrapper<RoleMenu> wrapper = new QueryWrapper<>();

        setQueryWrapperOrderByList(wrapper, orderByList);

        String failMessage = setQueryWrapperFilterList(wrapper, filterList);
        if (failMessage != null)
            return new FailResult<>(failMessage);

        Page<RoleMenu> page = null;
        if (current == null || size == null)
        {
            List<RoleMenu> list = roleMenuService.list(wrapper);
            page = new Page<>(1, list.size());
            page.setTotal(list.size());
            page.setRecords(list);
        }
        else
        {
            page = new Page<>(current, size);
            page =  roleMenuService.page(page, wrapper);
        }

        return new SuccessResult<>(page);
    }

    @ApiImplicitParam(name = "roleMenuId",paramType = "query",value = "主键",required = true)
    @PostMapping("/getById")
    @EncryptedRequestBody
    public HttpResult<RoleMenu> getById(Integer id) {
        RoleMenu roleMenu = roleMenuService.getById(id);
        if (roleMenu == null)
            return new FailResult<>("不存在");

        return new SuccessResult<>(roleMenu);
    }

    @ApiOperation(value = "保存或修改RoleMenu信息")
    @PostMapping("/save")
    @EncryptedRequestBody
    public HttpResult<RoleMenu> save(@JsonRequestBody RoleMenu roleMenu) {
        if (roleMenu == null)
            return new FailResult<>("实体不能为空");

        if (roleMenu.getId() == null)
            roleMenuMapper.insert(roleMenu);
        else
            roleMenuMapper.updateById(roleMenu);

        return new SuccessResult<>(roleMenu);
    }

    @ApiOperation(value = "根据id批量修改RoleMenu信息")
    @PostMapping("/update")
    @EncryptedRequestBody
    public HttpResult<String> update(@JsonToList(RoleMenu.class) List<RoleMenu> roleMenuList) {
        roleMenuService.updateBatchById(roleMenuList);
        return new SuccessResult<>("修改成功");
    }

    @ApiOperation(value = "根据id批量修改RoleMenu信息")
    @PostMapping("/updatePlus")
    @EncryptedRequestBody
    public HttpResult<String> updatePlus(@JsonRequestBody String roleMenuList) {
        JSONArray list = JSON.parseArray(roleMenuList);
        for (int i = 0; i < list.size(); i++) {
            JSONObject obj = list.getJSONObject(i);
            UpdateWrapper<RoleMenu> updateWrapper = new UpdateWrapper<>();
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
                roleMenuService.update(updateWrapper);
            else
                return new FailResult<>("必须包含id");
        }


        return new SuccessResult<>("修改成功");
    }

    @ApiOperation(value = "通过id列表删除RoleMenu")
    @ApiImplicitParams({
    @ApiImplicitParam(name = "ids",paramType = "query",value = "传入的id列表",required = true)
    })
    @PostMapping("/delete")
    @EncryptedRequestBody
    public HttpResult<String> delete(@JsonToList(Integer.class) List<String> ids) {
        roleMenuService.removeByIds(ids);
        return new SuccessResult<>("删除成功");
    }
}

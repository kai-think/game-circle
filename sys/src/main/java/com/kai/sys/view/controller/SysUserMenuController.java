package com.kai.sys.view.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kai.common.BaseController;
import com.kai.common.utils.httpresult.HttpResult;
import com.kai.common.utils.httpresult.SuccessResult;
import com.kai.sys.view.entity.SysUserMenu;
import com.kai.sys.view.mapper.SysUserMenuMapper;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @author zqy
* @since 2021-08-09
*/
@RestController
@RequestMapping("/sys.view/sys-user-menu")
    public class SysUserMenuController extends BaseController {

    @Autowired
    private SysUserMenuMapper sysUserMenuMapper;

    @ApiImplicitParams({
    @ApiImplicitParam(name = "current",paramType = "query",value = "当前页码",required = true),
    @ApiImplicitParam(name = "size",paramType = "query",value = "每页显示记录数",required = true),
    @ApiImplicitParam(name = "userId",paramType = "query",value = "自动生成主键",required = false),
    @ApiImplicitParam(name = "icon",paramType = "query",value = "图标",required = false),
    @ApiImplicitParam(name = "name",paramType = "query",value = "菜单名",required = false),
    @ApiImplicitParam(name = "path",paramType = "query",value = "路径，可以是组件路径或链接",required = false),
    @ApiImplicitParam(name = "descr",paramType = "query",value = "描述",required = false),
    @ApiImplicitParam(name = "level",paramType = "query",value = "等级，依次为 0、1、2、3...",required = false),
    @ApiImplicitParam(name = "parentId",paramType = "query",value = "父菜单id，-1表示没有父菜单",required = false),
    @ApiImplicitParam(name = "sort",paramType = "query",value = "排序",required = false)})
    @ApiOperation(value = "分页获取SysUserMenu信息")
    @GetMapping("/page")
    public HttpResult<List<SysUserMenu>> page(
        Integer current,
        Integer size,
        Integer userId,
        String icon,
        String name,
        String path,
        String descr,
        Integer level,
        Integer parentId,
        Integer sort) {
        QueryWrapper<SysUserMenu> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("sort");
        if (userId != null)
            wrapper.eq("user_id", userId);
        if (icon != null)
            wrapper.like("icon", icon);
        if (name != null)
            wrapper.like("name", name);
        if (path != null)
            wrapper.like("path", path);
        if (descr != null)
            wrapper.like("descr", descr);
        if (level != null)
            wrapper.eq("level", level);
        if (parentId != null)
            wrapper.eq("parent_id", parentId);
        if (sort != null)
            wrapper.eq("sort", sort);

        if (current == null || size == null)
            return new SuccessResult<>(sysUserMenuMapper.selectList(wrapper));

        Page<SysUserMenu> page = new Page<>(current, size);
        return new SuccessResult<>(sysUserMenuMapper.selectPage(page, wrapper).getRecords());
    }

    @ApiOperation(value = "保存或修改SysUserMenu信息")
    @PostMapping("/save")
    public HttpResult<SysUserMenu> save(@RequestBody @NonNull SysUserMenu sysUserMenu) {
        if (sysUserMenu.getId() == null)
            sysUserMenuMapper.insert(sysUserMenu);
        else
            sysUserMenuMapper.updateById(sysUserMenu);

        return new SuccessResult<>(sysUserMenu);
    }

    @ApiOperation(value = "通过id列表删除SysUserMenu")
    @ApiImplicitParams({
    @ApiImplicitParam(name = "ids",paramType = "query",value = "传入的id列表",required = true)
    })
    @DeleteMapping("/delete")
    public HttpResult<String> delete(@NonNull List<String> ids) {
        sysUserMenuMapper.deleteBatchIds(ids);
        return new SuccessResult<>("删除成功");
    }
}

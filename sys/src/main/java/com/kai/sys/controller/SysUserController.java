package com.kai.sys.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.BaseController;
import com.example.demo.common.sql.FilterItem;
import com.example.demo.common.sql.FilterItemSuper;
import com.example.demo.common.throwable.CheckFaildedException;
import com.example.demo.config.encrypt.EncryptedRequestBody;
import com.example.demo.config.resolver.JsonRequestBody;
import com.example.demo.config.resolver.JsonToList;
import com.example.demo.sys.entity.SysUser;
import com.example.demo.sys.entity.UserRole;
import com.example.demo.sys.mapper.SysUserMapper;
import com.example.demo.sys.service.impl.SysUserServiceImpl;
import com.example.demo.sys.service.impl.UserRoleServiceImpl;
import com.example.demo.utils.cipher.MD5WithSalt;
import com.example.demo.utils.converter.HumpLineConverter;
import com.example.demo.utils.httpresult.FailResult;
import com.example.demo.utils.httpresult.HttpResult;
import com.example.demo.utils.httpresult.SuccessResult;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
* @author zqy
* @since 2021-08-17
*/
@RestController
@RequestMapping("/sys/user")
public class SysUserController extends BaseController {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysUserServiceImpl userService;

    @Autowired
    private UserRoleServiceImpl userRoleService;

    @ApiImplicitParams({
    @ApiImplicitParam(name = "current",paramType = "query",value = "当前页码",required = false),
    @ApiImplicitParam(name = "size",paramType = "query",value = "每页显示记录数",required = false),
    @ApiImplicitParam(name = "username",paramType = "query",value = "用户名",required = false),
    @ApiImplicitParam(name = "password",paramType = "query",value = "密码",required = false),
    @ApiImplicitParam(name = "salt",paramType = "query",value = "",required = false),
    @ApiImplicitParam(name = "nickname",paramType = "query",value = "昵称",required = false),
    @ApiImplicitParam(name = "useful",paramType = "query",value = "是否可用 0、1 表示 不可用、可用",required = false),
    @ApiImplicitParam(name = "createTime",paramType = "query",value = "创建时间",required = false)})
    @ApiOperation(value = "分页获取User信息")
    @PostMapping("/page")
    @EncryptedRequestBody
    public HttpResult<Page<SysUser>> page(
        Integer current,
        Integer size,
        String username,
        String password,
        String salt,
        String nickname,
        Integer useful,
        LocalDateTime createTime) {

        QueryWrapper<SysUser> wrapper = new QueryWrapper<>();
        if (username != null)
            wrapper.like("username", username);
        if (password != null)
            wrapper.like("password", password);
        if (salt != null)
            wrapper.like("salt", salt);
        if (nickname != null)
            wrapper.like("nickname", nickname);
        if (useful != null)
            wrapper.eq("useful", useful);
        if (createTime != null)
            wrapper.eq("create_time", createTime);

        Page<SysUser> page = null;
        if (current == null || size == null)
        {
            List<SysUser> sysUserList = userService.list(wrapper);
            page = new Page<>(1, sysUserList.size());
            page.setTotal(sysUserList.size());
            page.setRecords(sysUserList);
        }
        else
        {
            page = new Page<>(current, size);
            page =  userService.page(page, wrapper);
        }

        return new SuccessResult<>(page);
    }

    @ApiImplicitParams({
    @ApiImplicitParam(name = "page",paramType = "query",value = "分页信息，格式类似为{current: 1, size: 5}",required = false),
    @ApiImplicitParam(name = "filter",paramType = "query",value = "查询信息，格式类似为[{name:'name',compare:'like',value:'管理员'}, {name:'id',compare:'eq',value:1}]",required = false),
    @ApiImplicitParam(name = "orderBy",paramType = "query",value = "排序方式，格式类似为['create_time asc','id desc']",required = false)})
    @ApiOperation(value = "分页获取User信息")
    @PostMapping("/pagePlus")
    @EncryptedRequestBody
    public HttpResult<Page<SysUser>> page(
        Integer current, Integer size,
        @JsonToList(FilterItem.class) List<FilterItem> filter,
        @JsonToList(String.class) List<String> orderBy) {
        QueryWrapper<SysUser> wrapper = new QueryWrapper<>();

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

        Page<SysUser> page = null;
        if (current == null || size == null)
        {
            List<SysUser> list = userService.list(wrapper);
            page = new Page<>(1, list.size());
            page.setTotal(list.size());
            page.setRecords(list);
        }
        else
        {
            page = new Page<>(current, size);
            page =  userService.page(page, wrapper);
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
    @ApiOperation(value = "分页获取SysUser信息")
    @PostMapping("/pageSuper")
    @EncryptedRequestBody
    public HttpResult<Page<SysUser>> pageSuper(
            Integer current, Integer size,
            @JsonToList(FilterItemSuper.class) List<FilterItemSuper> filterList,
            @JsonToList(String.class) List<String> orderByList) {
        QueryWrapper<SysUser> wrapper = new QueryWrapper<>();

        setQueryWrapperOrderByList(wrapper, orderByList);

        String failMessage = setQueryWrapperFilterList(wrapper, filterList);
        if (failMessage != null)
            return new FailResult<>(failMessage);

        Page<SysUser> page = null;
        if (current == null || size == null)
        {
            List<SysUser> list = userService.list(wrapper);
            page = new Page<>(1, list.size());
            page.setTotal(list.size());
            page.setRecords(list);
        }
        else
        {
            page = new Page<>(current, size);
            page =  userService.page(page, wrapper);
        }

        return new SuccessResult<>(page);
    }

    @ApiImplicitParam(name = "userId",paramType = "query",value = "主键",required = true)
    @PostMapping("/getById")
    @EncryptedRequestBody
    public HttpResult<SysUser> getById(Integer id) {
        SysUser sysUser = userService.getById(id);
        if (sysUser == null)
            return new FailResult<>("不存在");

        return new SuccessResult<>(sysUser);
    }

    @ApiOperation(value = "保存或修改User信息")
    @PostMapping("/save")
    @EncryptedRequestBody
    public HttpResult<SysUser> save(@JsonRequestBody SysUser sysUser) {
        if (sysUser == null)
            return new FailResult<>("实体不能为空");

        if (sysUser.getId() == null)
        {
            String salt = UUID.randomUUID().toString();
            String password = MD5WithSalt.digest(sysUser.getPassword(), salt);
            sysUser.setSalt(salt);
            sysUser.setPassword(password);
            sysUserMapper.insert(sysUser);
        }
        else
        {
            SysUser sysUser2 = userService.getById(sysUser.getId());
            if (sysUser2 == null)
                return new FailResult<>("用户不存在");
            if (!sysUser2.getPassword().equals(sysUser.getPassword()))
            {
                String salt = UUID.randomUUID().toString();
                String password = MD5WithSalt.digest(sysUser.getPassword(), salt);
                sysUser.setSalt(salt);
                sysUser.setPassword(password);
            }
            sysUserMapper.updateById(sysUser);
        }

        return new SuccessResult<>(sysUser);
    }

    @ApiOperation(value = "根据id批量修改User信息")
    @PostMapping("/update")
    @EncryptedRequestBody
    public HttpResult<String> update(@JsonToList(SysUser.class) List<SysUser> sysUserList) {
        userService.updateBatchById(sysUserList);
        return new SuccessResult<>("修改成功");
    }

    @ApiOperation(value = "根据id批量修改User信息")
    @PostMapping("/updatePlus")
    @EncryptedRequestBody
    public HttpResult<String> updatePlus(@JsonRequestBody String userList) {
        JSONArray list = JSON.parseArray(userList);
        for (int i = 0; i < list.size(); i++) {
            JSONObject obj = list.getJSONObject(i);
            UpdateWrapper<SysUser> updateWrapper = new UpdateWrapper<>();
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
                userService.update(updateWrapper);
            else
                return new FailResult<>("必须包含id");
        }


        return new SuccessResult<>("修改成功");
    }

    @ApiOperation(value = "通过id列表删除User")
    @ApiImplicitParams({
    @ApiImplicitParam(name = "ids",paramType = "query",value = "传入的id列表",required = true)
    })
    @PostMapping("/delete")
    @EncryptedRequestBody
    public HttpResult<String> delete(@JsonToList(Integer.class) List<String> ids) {
        userService.removeByIds(ids);
        return new SuccessResult<>("删除成功");
    }

    @PostMapping("/assignRole")
    @EncryptedRequestBody
    public HttpResult<String> assignRole(@NonNull Integer userId, @JsonToList(UserRole.class) List<UserRole> roleIdAndLimitationList) {
        SysUser sysUser = userService.getById(userId);
        if (sysUser == null)
            return new FailResult<>("角色不存在");
        userRoleService.remove(new QueryWrapper<UserRole>().eq("user_id", userId));

        for (UserRole ur : roleIdAndLimitationList) {
            if (ur.getRoleId() == null)
                throw new CheckFaildedException();

            ur.setUserId(userId);
        }

        userRoleService.saveBatch(roleIdAndLimitationList);
        return new SuccessResult<>("分配成功");
    }
}

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
import com.kai.sys.entity.Dic;
import com.kai.sys.mapper.DicMapper;
import com.kai.sys.service.impl.DicServiceImpl;
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

import java.util.List;

/**
* @author zqy
* @since 2021-08-17
*/
@RestController
@RequestMapping("/sys/dic")
    public class SysDicController extends BaseController {

    @Autowired
    private DicMapper dicMapper;

    @Autowired
    private DicServiceImpl dicService;

    @ApiImplicitParams({
    @ApiImplicitParam(name = "current",paramType = "query",value = "当前页码",required = false),
    @ApiImplicitParam(name = "size",paramType = "query",value = "每页显示记录数",required = false),
    @ApiImplicitParam(name = "name",paramType = "query",value = "名",required = false),
    @ApiImplicitParam(name = "value",paramType = "query",value = "对应的 数字类型 值",required = false),
    @ApiImplicitParam(name = "stringValue",paramType = "query",value = "对应的字符类型值",required = false),
    @ApiImplicitParam(name = "parentId",paramType = "query",value = "父级字典id，顶级为空",required = false),
    @ApiImplicitParam(name = "sort",paramType = "query",value = "排序，数字越小越靠前",required = false),
    @ApiImplicitParam(name = "descr",paramType = "query",value = "描述",required = false)})
    @ApiOperation(value = "分页获取Dic信息")
    @PostMapping("/page")
    @EncryptedRequestBody
    public HttpResult<Page<Dic>> page(
        Integer current,
        Integer size,
        String name,
        Integer value,
        String stringValue,
        Integer parentId,
        Integer sort,
        String descr) {

        QueryWrapper<Dic> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("sort");
        if (name != null)
            wrapper.like("name", name);
        if (value != null)
            wrapper.eq("value", value);
        if (stringValue != null)
            wrapper.like("string_value", stringValue);
        if (parentId != null)
            wrapper.eq("parent_id", parentId);
        if (sort != null)
            wrapper.eq("sort", sort);
        if (descr != null)
            wrapper.like("descr", descr);

        Page<Dic> page = null;
        if (current == null || size == null)
        {
            List<Dic> dicList = dicService.list(wrapper);
            page = new Page<>(1, dicList.size());
            page.setTotal(dicList.size());
            page.setRecords(dicList);
        }
        else
        {
            page = new Page<>(current, size);
            page =  dicService.page(page, wrapper);
        }

        ListToTree.listToTree(page.getRecords());
        return new SuccessResult<>(page);
    }

    @ApiImplicitParams({
    @ApiImplicitParam(name = "page",paramType = "query",value = "分页信息，格式类似为{current: 1, size: 5}",required = false),
    @ApiImplicitParam(name = "filter",paramType = "query",value = "查询信息，格式类似为[{name:'name',compare:'like',value:'管理员'}, {name:'id',compare:'eq',value:1}]",required = false),
    @ApiImplicitParam(name = "orderBy",paramType = "query",value = "排序方式，格式类似为['create_time asc','id desc']",required = false)})
    @ApiOperation(value = "分页获取Dic信息")
    @PostMapping("/pagePlus")
    @EncryptedRequestBody
    public HttpResult<Page<Dic>> page(
        Integer current, Integer size,
        @JsonToList(FilterItem.class) List<FilterItem> filter,
        @JsonToList(String.class) List<String> orderBy) {
        QueryWrapper<Dic> wrapper = new QueryWrapper<>();
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

        Page<Dic> page = null;
        if (current == null || size == null)
        {
            List<Dic> list = dicService.list(wrapper);
            page = new Page<>(1, list.size());
            page.setTotal(list.size());
            page.setRecords(list);
        }
        else
        {
            page = new Page<>(current, size);
            page =  dicService.page(page, wrapper);
        }

        ListToTree.listToTree(page.getRecords());
        return new SuccessResult<>(page);
    }

    @ApiImplicitParam(name = "dicId",paramType = "query",value = "主键",required = true)
    @PostMapping("/getById")
    @EncryptedRequestBody
    public HttpResult<Dic> getById(Integer id) {
        Dic dic = dicService.getById(id);
        if (dic == null)
            return new FailResult<>("不存在");

        List<Dic> children = dicService.list(new QueryWrapper<Dic>().lambda().eq(Dic::getParentId, dic.getId()));
        dic.setChildren(children);

        return new SuccessResult<>(dic);
    }

    @ApiImplicitParam(name = "name",paramType = "query",value = "字典英文名",required = true)
    @PostMapping("/getByName")
    @EncryptedRequestBody
    public HttpResult<Dic> getByName(String name) {
        Dic dic = dicService.getOne(new QueryWrapper<Dic>().lambda().eq(Dic::getName, name));
        if (dic == null)
            return new FailResult<>("不存在");

        List<Dic> children = dicService.list(new QueryWrapper<Dic>().lambda().eq(Dic::getParentId, dic.getId()));
        dic.setChildren(children);
        return new SuccessResult<>(dic);
    }

    @ApiOperation(value = "保存或修改Dic信息")
    @PostMapping("/save")
    @EncryptedRequestBody
    public HttpResult<Dic> save(@JsonRequestBody Dic dic) {
        if (dic == null)
            return new FailResult<>("对象不存在");

        if (dic.getId() == null)
            dicMapper.insert(dic);
        else
            dicMapper.updateById(dic);

        return new SuccessResult<>(dic);
    }

    @ApiOperation(value = "根据id批量修改Dic信息")
    @PostMapping("/update")
    @EncryptedRequestBody
    public HttpResult<String> update(@JsonToList(Dic.class) List<Dic> dicList) {
        dicService.updateBatchById(dicList);
        return new SuccessResult<>("修改成功");
    }

    @ApiOperation(value = "根据id批量修改Dic信息")
    @PostMapping("/updatePlus")
    @EncryptedRequestBody
    public HttpResult<String> updatePlus(@JsonRequestBody String dicList) {
        JSONArray list = JSON.parseArray(dicList);
        
        for (int i = 0; i < list.size(); i++) {
            JSONObject obj = list.getJSONObject(i);
            UpdateWrapper<Dic> updateWrapper = new UpdateWrapper<>();
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
                dicService.update(updateWrapper);
            else
                return new FailResult<>("必须包含id");
        }


        return new SuccessResult<>("修改成功");
    }

    @ApiOperation(value = "通过id列表删除Dic")
    @ApiImplicitParams({
    @ApiImplicitParam(name = "ids",paramType = "query",value = "传入的id列表",required = true)
    })
    @PostMapping("/delete")
    @EncryptedRequestBody
    public HttpResult<String> delete(@JsonToList(Integer.class) List<String> ids) {
        dicService.removeByIds(ids);
        return new SuccessResult<>("删除成功");
    }
}

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
import com.kai.sys.entity.TableCol;
import com.kai.sys.mapper.TableColMapper;
import com.kai.sys.service.impl.TableColServiceImpl;
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
@RequestMapping("/sys/table-col")
public class SysTableColController extends BaseController {

    @Autowired
    private TableColMapper tableColMapper;

    @Autowired
    private TableColServiceImpl tableColService;

    @ApiImplicitParams({
    @ApiImplicitParam(name = "current",paramType = "query",value = "当前页码",required = false),
    @ApiImplicitParam(name = "size",paramType = "query",value = "每页显示记录数",required = false),
    @ApiImplicitParam(name = "tableId",paramType = "query",value = "",required = false),
    @ApiImplicitParam(name = "type",paramType = "query",value = "在字典里对应的类型值",required = false),
    @ApiImplicitParam(name = "name",paramType = "query",value = "标记名字",required = false),
    @ApiImplicitParam(name = "value",paramType = "query",value = "表名",required = false),
    @ApiImplicitParam(name = "width",paramType = "query",value = "显示宽度",required = false),
    @ApiImplicitParam(name = "descr",paramType = "query",value = "描述",required = false),
    @ApiImplicitParam(name = "show",paramType = "query",value = "显示为 1， 不显示为 0",required = false),
    @ApiImplicitParam(name = "disable",paramType = "query",value = "不可用 为 1， 可用为 0",required = false),
    @ApiImplicitParam(name = "ruleDicIds",paramType = "query",value = "难验证规则列表，对应的字典id，用 , 分开",required = false),
    @ApiImplicitParam(name = "formatDicId",paramType = "query",value = "值格式化用的字典id",required = false),
    @ApiImplicitParam(name = "sort",paramType = "query",value = "",required = false)})
    @ApiOperation(value = "分页获取TableCol信息")
    @PostMapping("/page")
    @EncryptedRequestBody
    public HttpResult<Page<TableCol>> page(
        Integer current,
        Integer size,
        Integer tableId,
        Integer type,
        String name,
        String value,
        Integer width,
        String descr,
        Integer show,
        Integer disable,
        String ruleDicIds,
        Integer formatDicId,
        Integer sort) {

        QueryWrapper<TableCol> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("sort");
        if (tableId != null)
            wrapper.eq("table_id", tableId);
        if (type != null)
            wrapper.eq("type", type);
        if (name != null)
            wrapper.like("name", name);
        if (value != null)
            wrapper.like("value", value);
        if (width != null)
            wrapper.eq("width", width);
        if (descr != null)
            wrapper.like("descr", descr);
        if (show != null)
            wrapper.eq("show", show);
        if (disable != null)
            wrapper.eq("disable", disable);
        if (ruleDicIds != null)
            wrapper.like("rule_dic_ids", ruleDicIds);
        if (formatDicId != null)
            wrapper.eq("format_dic_id", formatDicId);
        if (sort != null)
            wrapper.eq("sort", sort);

        wrapper.orderByAsc("sort");

        Page<TableCol> page = null;
        if (current == null || size == null)
        {
            List<TableCol> tableColList = tableColService.list(wrapper);
            page = new Page<>(1, tableColList.size());
            page.setTotal(tableColList.size());
            page.setRecords(tableColList);
        }
        else
        {
            page = new Page<>(current, size);
            page =  tableColService.page(page, wrapper);
        }

        return new SuccessResult<>(page);
    }

    @ApiImplicitParams({
    @ApiImplicitParam(name = "page",paramType = "query",value = "分页信息，格式类似为{current: 1, size: 5}",required = false),
    @ApiImplicitParam(name = "filter",paramType = "query",value = "查询信息，格式类似为[{name:'name',compare:'like',value:'管理员'}, {name:'id',compare:'eq',value:1}]",required = false),
    @ApiImplicitParam(name = "orderBy",paramType = "query",value = "排序方式，格式类似为['create_time asc','id desc']",required = false)})
    @ApiOperation(value = "分页获取TableCol信息")
    @PostMapping("/pagePlus")
    @EncryptedRequestBody
    public HttpResult<Page<TableCol>> page(
        Integer current, Integer size,
        @JsonToList(FilterItem.class) List<FilterItem> filter,
        @JsonToList(String.class) List<String> orderBy) {
        QueryWrapper<TableCol> wrapper = new QueryWrapper<>();

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

        Page<TableCol> page = null;
        if (current == null || size == null)
        {
            List<TableCol> list = tableColService.list(wrapper);
            page = new Page<>(1, list.size());
            page.setTotal(list.size());
            page.setRecords(list);
        }
        else
        {
            page = new Page<>(current, size);
            page =  tableColService.page(page, wrapper);
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
    @ApiOperation(value = "分页获取TableCol信息")
    @PostMapping("/pageSuper")
    @EncryptedRequestBody
    public HttpResult<Page<TableCol>> pageSuper(
        Integer current, Integer size,
        @JsonToList(FilterItemSuper.class) List<FilterItemSuper> filterList,
        @JsonToList(String.class) List<String> orderByList) {
        QueryWrapper<TableCol> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("sort");
        setQueryWrapperOrderByList(wrapper, orderByList);

        String failMessage = setQueryWrapperFilterList(wrapper, filterList);
        if (failMessage != null)
            return new FailResult<>(failMessage);

        Page<TableCol> page = null;
        if (current == null || size == null)
        {
            List<TableCol> list = tableColService.list(wrapper);
            page = new Page<>(1, list.size());
            page.setTotal(list.size());
            page.setRecords(list);
        }
        else
        {
            page = new Page<>(current, size);
            page =  tableColService.page(page, wrapper);
        }

        return new SuccessResult<>(page);
    }

    @ApiImplicitParam(name = "tableColId",paramType = "query",value = "主键",required = true)
    @PostMapping("/getById")
    @EncryptedRequestBody
    public HttpResult<TableCol> getById(Integer id) {
        TableCol tableCol = tableColService.getById(id);
        if (tableCol == null)
            return new FailResult<>("不存在");

        return new SuccessResult<>(tableCol);
    }

    @ApiOperation(value = "保存或修改TableCol信息")
    @PostMapping("/save")
    @EncryptedRequestBody
    public HttpResult<TableCol> save(@JsonRequestBody TableCol tableCol) {
        if (tableCol == null)
            return new FailResult<>("实体不能为空");

        if (tableCol.getId() == null)
            tableColMapper.insert(tableCol);
        else
            tableColMapper.updateById(tableCol);

        return new SuccessResult<>(tableCol);
    }

    @ApiOperation(value = "根据id批量修改TableCol信息")
    @PostMapping("/update")
    @EncryptedRequestBody
    public HttpResult<String> update(@JsonToList(TableCol.class) List<TableCol> tableColList) {
        tableColService.updateBatchById(tableColList);
        return new SuccessResult<>("修改成功");
    }

    @ApiOperation(value = "根据id批量修改TableCol信息")
    @PostMapping("/updatePlus")
    @EncryptedRequestBody
    public HttpResult<String> updatePlus(@JsonRequestBody String tableColList) {
        JSONArray list = JSON.parseArray(tableColList);
        for (int i = 0; i < list.size(); i++) {
            JSONObject obj = list.getJSONObject(i);
            UpdateWrapper<TableCol> updateWrapper = new UpdateWrapper<>();
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
                tableColService.update(updateWrapper);
            else
                return new FailResult<>("必须包含id");
        }


        return new SuccessResult<>("修改成功");
    }

    @ApiOperation(value = "通过id列表删除TableCol")
    @ApiImplicitParams({
    @ApiImplicitParam(name = "ids",paramType = "query",value = "传入的id列表",required = true)
    })
    @PostMapping("/delete")
    @EncryptedRequestBody
    public HttpResult<String> delete(@JsonToList(Integer.class) List<String> ids) {
        tableColService.removeByIds(ids);
        return new SuccessResult<>("删除成功");
    }
}

package com.kai.sys.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kai.common.BaseController;
import com.kai.sys.mapper.TableMapper;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @author zqy
* @since 2021-08-17
*/
@RestController
@RequestMapping("/sys/table")
    public class SysTableController extends BaseController {

    @Autowired
    private TableMapper tableMapper;

    @Autowired
    private TableServiceImpl tableService;

    @Autowired
    private TableColServiceImpl tableColService;

    @ApiImplicitParams({
    @ApiImplicitParam(name = "current",paramType = "query",value = "当前页码",required = false),
    @ApiImplicitParam(name = "size",paramType = "query",value = "每页显示记录数",required = false),
    @ApiImplicitParam(name = "name",paramType = "query",value = "表名",required = false),
    @ApiImplicitParam(name = "descr",paramType = "query",value = "描述",required = false)})
    @ApiOperation(value = "分页获取Table信息")
    @PostMapping("/page")
    @EncryptedRequestBody
    public HttpResult<Page<Table>> page(
        Integer current,
        Integer size,
        String name,
        String descr) {

        QueryWrapper<Table> wrapper = new QueryWrapper<>();
        if (name != null)
            wrapper.like("name", name);
        if (descr != null)
            wrapper.like("descr", descr);

        Page<Table> page = null;
        if (current == null || size == null)
        {
            List<Table> tableList = tableService.list(wrapper);
            page = new Page<>(1, tableList.size());
            page.setTotal(tableList.size());
            page.setRecords(tableList);
        }
        else
        {
            page = new Page<>(current, size);
            page =  tableService.page(page, wrapper);
        }

        return new SuccessResult<>(page);
    }

    @ApiImplicitParams({
    @ApiImplicitParam(name = "page",paramType = "query",value = "分页信息，格式类似为{current: 1, size: 5}",required = false),
    @ApiImplicitParam(name = "filter",paramType = "query",value = "查询信息，格式类似为[{name:'name',compare:'like',value:'管理员'}, {name:'id',compare:'eq',value:1}]",required = false),
    @ApiImplicitParam(name = "orderBy",paramType = "query",value = "排序方式，格式类似为['create_time asc','id desc']",required = false)})
    @ApiOperation(value = "分页获取Table信息")
    @PostMapping("/pagePlus")
    @EncryptedRequestBody
    public HttpResult<Page<Table>> page(
        Integer current, Integer size,
        @JsonToList(FilterItem.class) List<FilterItem> filter,
        @JsonToList(String.class) List<String> orderBy) {
        QueryWrapper<Table> wrapper = new QueryWrapper<>();

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

        Page<Table> page = null;
        if (current == null || size == null)
        {
            List<Table> list = tableService.list(wrapper);
            page = new Page<>(1, list.size());
            page.setTotal(list.size());
            page.setRecords(list);
        }
        else
        {
            page = new Page<>(current, size);
            page =  tableService.page(page, wrapper);
        }

        List<Table> tableList = page.getRecords();
        tableList.forEach(table -> {
            List<TableCol> cols = tableColService.list(new QueryWrapper<TableCol>().eq("table_id", table.getId()));
            table.setCols(cols);
        });

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
    @ApiOperation(value = "分页获取Table信息")
    @PostMapping("/pageSuper")
    @EncryptedRequestBody
    public HttpResult<Page<Table>> pageSuper(
            Integer current, Integer size,
            @JsonToList(FilterItemSuper.class) List<FilterItemSuper> filterList,
            @JsonToList(String.class) List<String> orderByList) {
        QueryWrapper<Table> wrapper = new QueryWrapper<>();

        setQueryWrapperOrderByList(wrapper, orderByList);

        String failMessage = setQueryWrapperFilterList(wrapper, filterList);
        if (failMessage != null)
            return new FailResult<>(failMessage);

        Page<Table> page = null;
        if (current == null || size == null)
        {
            List<Table> list = tableService.list(wrapper);
            page = new Page<>(1, list.size());
            page.setTotal(list.size());
            page.setRecords(list);
        }
        else
        {
            page = new Page<>(current, size);
            page =  tableService.page(page, wrapper);
        }

        return new SuccessResult<>(page);
    }

    @ApiImplicitParam(name = "tableId",paramType = "query",value = "主键",required = true)
    @PostMapping("/getById")
    @EncryptedRequestBody
    public HttpResult<Table> getById(Integer id) {
        Table table = tableService.getById(id);
        if (table == null)
            return new FailResult<>("不存在");

        List<TableCol> cols = tableColService.list(new QueryWrapper<TableCol>()
                .eq("table_id", table.getId())
                .orderByAsc("sort"));
        table.setCols(cols);
        return new SuccessResult<>(table);
    }

    @ApiImplicitParam(name = "tableId",paramType = "query",value = "主键",required = true)
    @PostMapping("/getByName")
    @EncryptedRequestBody
    public HttpResult<Table> getByName(String name) {
        Table table = tableService.getOne(new QueryWrapper<Table>().eq("name", name));
        if (table == null)
            return new FailResult<>("不存在");

        List<TableCol> cols = tableColService.list(new QueryWrapper<TableCol>()
                .eq("table_id", table.getId())
                .orderByAsc("sort"));
        table.setCols(cols);
        return new SuccessResult<>(table);
    }

    @ApiOperation(value = "保存或修改Table信息")
    @PostMapping("/save")
    @EncryptedRequestBody
    public HttpResult<Table> save(@JsonRequestBody Table table) {
        if (table.getId() == null)
            tableMapper.insert(table);
        else
            tableMapper.updateById(table);

        return new SuccessResult<>(table);
    }

    @ApiOperation(value = "根据id批量修改Table信息")
    @PostMapping("/update")
    @EncryptedRequestBody
    public HttpResult<String> update(@JsonToList(Table.class) List<Table> tableList) {
        tableService.updateBatchById(tableList);
        return new SuccessResult<>("修改成功");
    }

    @ApiOperation(value = "根据id批量修改Table信息")
    @PostMapping("/updatePlus")
    @EncryptedRequestBody
    public HttpResult<String> updatePlus(@JsonRequestBody String tableList) {
        JSONArray list = JSON.parseArray(tableList);
        for (int i = 0; i < list.size(); i++) {
            JSONObject obj = list.getJSONObject(i);
            UpdateWrapper<Table> updateWrapper = new UpdateWrapper<>();
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
                tableService.update(updateWrapper);
            else
                return new FailResult<>("必须包含id");
        }


        return new SuccessResult<>("修改成功");
    }

    @ApiOperation(value = "通过id列表删除Table")
    @ApiImplicitParams({
    @ApiImplicitParam(name = "ids",paramType = "query",value = "传入的id列表",required = true)
    })
    @PostMapping("/delete")
    @EncryptedRequestBody
    public HttpResult<String> delete(@JsonToList(Integer.class) List<String> ids) {
        tableService.removeByIds(ids);
        return new SuccessResult<>("删除成功");
    }


    Map<String, String> filedNameMap = new HashMap<>();
    {
        filedNameMap.put("id", "主键");
        filedNameMap.put("title", "标题");
        filedNameMap.put("cover", "封面");
        filedNameMap.put("content", "内容");
        filedNameMap.put("addr", "地址");
        filedNameMap.put("address", "地址");
        filedNameMap.put("name", "名字");
        filedNameMap.put("value", "值");
        filedNameMap.put("desc", "描述");
        filedNameMap.put("descr", "描述");
        filedNameMap.put("sex", "性别");
        filedNameMap.put("sort", "排序");
        filedNameMap.put("path", "路径");
        filedNameMap.put("component", "组件");
        filedNameMap.put("avatar", "头像");
        filedNameMap.put("image", "图片");
        filedNameMap.put("img", "图片");
        filedNameMap.put("video", "视频");
        filedNameMap.put("username", "用户名");
        filedNameMap.put("password", "密码");
        filedNameMap.put("nickname", "昵称");
        filedNameMap.put("time", "时间");
        filedNameMap.put("date", "日期");
        filedNameMap.put("create_time", "创建时间");
        filedNameMap.put("type", "类别");
        filedNameMap.put("state", "状态");
        filedNameMap.put("status", "状态");
    }

    public static class ColTypeVal {
        public static Integer Dic = -2;
        public static Integer FK = -1;
        public static Integer PK = 0;
        public static Integer Number = 1;
        public static Integer String = 2;
        public static Integer TextArea = 20;
        public static Integer Boolean = 3;
        public static Integer DateTime = 4;
        public static Integer WangEditor = 5;
        public static Integer Image = 6;
        public static Integer Video = 7;
    }

    @ApiOperation(value = "通过id列表删除TableCol")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tableId",paramType = "body",value = "表id",required = true)
    })
    @PostMapping("/initTableCols")
    @EncryptedRequestBody
    public HttpResult<String> initTableCols(Integer tableId) {
        Table table = tableService.getById(tableId);
        if (table == null)
            return new FailResult<>("表不存在");

        List<SysTableDesc> descs = tableMapper.getTableDesc(table.getName());
        descs.forEach(desc -> {
            TableCol col = new TableCol();
            col.setTableId(tableId);
            col.setWidth(120);
            col.setDisable(0);
            col.setShow(1);

            String field = desc.getField();
            field = HumpLineConverter.lineToHump(field);
            String name = filedNameMap.get(field);

            if (name != null)
                col.setName(name);
            else
                col.setName(desc.getField());

            col.setValue(field);

            if (desc.getKey().equals("PRI"))
            {
                col.setType(ColTypeVal.PK);
                col.setDisable(1);
            }
            else if (desc.getKey().equals("MUL"))
                col.setType(ColTypeVal.FK);
            else if (desc.getType().contains("char"))
                col.setType(ColTypeVal.String);
            else if (desc.getType().contains("text"))
                col.setType(ColTypeVal.TextArea);
            else
            {
                switch (desc.getType())
                {
                    case "int":
                    case "tinyint":
                    case "float":
                    case "double": col.setType(ColTypeVal.Number); break;
                    case "date":
                    case "time":
                    case "datetime":
                    case "timestamp": col.setType(ColTypeVal.DateTime); break;
                    default: col.setType(ColTypeVal.String); break;
                }
            }

            tableColService.save(col);
        });

        return new SuccessResult<>("初始化成功");
    }
}

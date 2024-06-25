package com.kai.sys.controller;

import com.kai.common.BaseController;
import com.kai.common.config.encrypt.EncryptedRequestBody;
import com.kai.common.config.resolver.JsonToList;
import com.kai.common.throwable.CheckFaildedException;
import com.kai.common.utils.IValidator;
import com.kai.common.utils.converter.HumpLineConverter;
import com.kai.common.utils.httpresult.HttpResult;
import com.kai.common.utils.httpresult.SuccessResult;
import com.kai.sys.mapper.CommonMapper;
import com.kai.sys.service.impl.TableColServiceImpl;
import com.kai.sys.service.impl.TableServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sys/public")
public class SysCommonController extends BaseController {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    TableServiceImpl tableService;

    @Autowired
    TableColServiceImpl colService;

    @Autowired
    CommonMapper commonMapper;

    @PostMapping("/getByIdBatch")
    @EncryptedRequestBody
    public HttpResult<List<Map<String, Object>>> getByIdBatch(@JsonToList(Integer.class) List<Integer> ids, String table, @JsonToList(String.class) List<String> showCols) {
        if (IValidator.empty(ids, table, showCols))
            throw new CheckFaildedException();

        showCols = showCols.stream().distinct().map(HumpLineConverter::humpToLine).collect(Collectors.toList());

        List<String> idStrings = ids.stream().distinct().map(String::valueOf).collect(Collectors.toList());

        String sql = "select id," + String.join(",", showCols) +
                " from " + table +
                " where id in (" + String.join(",", idStrings) + ")";

        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);

        return new SuccessResult<>(result);
    }
}

package com.kai.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kai.sys.entity.SysTableDesc;
import com.kai.sys.entity.Table;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zqy
 * @since 2021-08-12
 */
public interface TableMapper extends BaseMapper<Table> {

    @Select("desc ${param1}")
    List<SysTableDesc> getTableDesc(String tableName);
}

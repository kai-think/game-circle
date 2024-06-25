package com.kai.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kai.sys.entity.Table;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 数据字典 Mapper 接口
 * </p>
 *
 * @author zqy
 * @since 2021-08-07
 */
public interface CommonMapper extends BaseMapper<Table> {

    List<Map<String, Object>> getByIdBatch(@Param("ids") List<Integer> ids,
                                           @Param("table") String table,
                                           @Param("cols")  List<String> cols);

}

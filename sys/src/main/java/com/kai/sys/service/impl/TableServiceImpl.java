package com.kai.sys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kai.sys.entity.Table;
import com.kai.sys.mapper.TableMapper;
import com.kai.sys.service.ITableService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zqy
 * @since 2021-08-12
 */
@Service
public class TableServiceImpl extends ServiceImpl<TableMapper, Table> implements ITableService {

}

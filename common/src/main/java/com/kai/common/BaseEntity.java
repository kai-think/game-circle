package com.kai.common;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;

/**
 * 基础实体类
 * @author bill
 *
 */
@Data
public class BaseEntity implements Serializable {

    @ApiModelProperty(value ="自动生成主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value ="etc，额外数据")
    @TableField(exist = false)
    private HashMap<String, Object> etc;

    @SuppressWarnings("unchecked")
    public void putEtc(String key, Object value) {
        if(etc == null) {
            etc = new HashMap<>();
        }
        etc.put(key, value);
    }
}

package com.kai.common.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MybatisPlus配置
 * @author bill
 *
 * MapperScan("com.demo.mapper")：扫描指定包中的接口
 *
 * MapperScan("com.demo.*.mapper")：一个*代表任意字符串，但只代表一级包,比如可以扫到com.demo.aaa.mapper,不能扫到com.demo.aaa.bbb.mapper
 *
 * MapperScan("com.demo.**.mapper")：两个*代表任意个包,比如可以扫到com.demo.aaa.mapper,也可以扫到com.demo.aaa.bbb.mapper
 *
 * 多个包
 * @MapperScan({"com.mysiteforme.admin.dao","com.zipon.tpf.dao"})
 */


@Configuration
@MapperScan("com.example.demo.**.mapper")
public class MybatisPlusConfig {

    /**
     * 新的分页插件,一缓和二缓遵循mybatis的规则,需要设置 MybatisConfiguration#useDeprecatedExecutor = false 避免缓存出现问题(该属性会在旧插件移除后一同移除)
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.H2));
        return interceptor;
    }

    @Bean
    public ConfigurationCustomizer configurationCustomizer() {
        return configuration -> configuration.setUseDeprecatedExecutor(false);
    }
}
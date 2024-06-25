package com.kai.common.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.intercepter.AuthorityIntercepter;
import com.example.demo.common.intercepter.BbsUserLoginRequiredIntercepter;
import com.example.demo.common.intercepter.DecryptionIntercepter;
import com.example.demo.common.intercepter.FlowControlIntercepter;
import com.example.demo.common.service.FlushActivationService;
import com.example.demo.common.sql.FilterItem;
import com.example.demo.common.sql.SqlFilter;
import com.example.demo.common.sql.SqlOrder;
import com.example.demo.config.encrypt.EncryptedPostMethodArgumentResolver;
import com.example.demo.config.resolver.PostMethodArgumentResolver;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.CacheControl;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Configuration
public class CusWebMvcConfigurer implements WebMvcConfigurer {
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.CHINA);
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss", Locale.CHINA);

    @Value("${spring.mvc.static-path-pattern}")
    String StaticPathPattern;

    @Value("${spring.resources.static-locations}")
    String StaticLocation;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String StaticPath = StaticLocation;
        registry.addResourceHandler(StaticPathPattern)
                .addResourceLocations(StaticPath)
                .setCacheControl(CacheControl.maxAge(1, TimeUnit.HOURS).cachePublic())
                .setCachePeriod(3153600);
    }

    @Autowired
    AuthorityIntercepter authorityIntercepter;
    @Autowired
    DecryptionIntercepter decryptionIntercepter;
    @Autowired
    BbsUserLoginRequiredIntercepter bbsUserLoginRequiredIntercepter;

    @Autowired
    FlowControlIntercepter flowControlIntercepter;

    @Autowired
    FlushActivationService flushActivationService;

    @PostConstruct
    public void init() {
        //更新activation表
        flushActivationService.flush();
        //刷新缓存
        authorityIntercepter.reflushPermissionMapper();
    }

    //RabbitMQ使用的消息转换器
    @Bean
    MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * @MethodName: addInterceptors
     * @Description: 添加一个拦截器
     * @Param:
     * @Return:
     * @Author: linjiangyi
     * @Date: 2019-12-10 19:48
     **/
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加拦截器

        //流量控制拦截器
        registry.addInterceptor(flowControlIntercepter);
        //权限拦截器
        registry.addInterceptor(authorityIntercepter);
        //加密拦截器
        registry.addInterceptor(decryptionIntercepter);
        registry.addInterceptor(bbsUserLoginRequiredIntercepter);
    }

    /**
     * 添加自定义的Converters和Formatters.
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new Converter<String, Page<?>>() {
            @Override
            public Page<?> convert(String s) {
                return JSONObject.parseObject(s).toJavaObject(Page.class);
            }
        });
        registry.addConverter(new Converter<String, SqlFilter>() {
            @Override
            public SqlFilter convert(String s) {
                List<FilterItem> items = JSON.parseArray(s, FilterItem.class);
                if (items == null)
                    return null;
                return new SqlFilter(items);
            }
        });
        registry.addConverter(new Converter<String, SqlOrder>() {
            @Override
            public SqlOrder convert(String s) {
                List<String> order = JSON.parseArray(s, String.class);
                if (order == null || order.size() == 0)
                    return null;

                SqlOrder sqlOrder = new SqlOrder();
                order.forEach(o -> {
                    String[] part = o.split(" ");
                    if (part.length == 0)
                        return;
                    if (part.length == 1)
                        sqlOrder.put(part[0], null);
                    else
                        sqlOrder.put(part[0], !part[1].toLowerCase().equals("desc"));
                });

                return sqlOrder;
            }
        });
    }

    /**
     * 	结果转换器
     * @param converters
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
//        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
//        ObjectMapper objectMapper = new ObjectMapper();
//        JavaTimeModule timeModule = new JavaTimeModule();
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        //序列化日期格式
//        timeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
//        timeModule.addSerializer(LocalDate.class, new LocalDateSerializer());
//        timeModule.addSerializer(LocalTime.class, new LocalTimeSerializer());
//        timeModule.addSerializer(Date.class, new DateSerializer(false, simpleDateFormat));
//        //反序列化日期格式
//        timeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
//        timeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer());
//        timeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer());
//        timeModule.addDeserializer(Date.class, new JsonDeserializer<Date>() {
//            @Override
//            public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
//                String date = jsonParser.getText();
//                try {
//                    return simpleDateFormat.parse(date);
//                } catch (ParseException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        });
//        objectMapper.registerModule(timeModule);
////        objectMapper.setTimeZone();
//        converter.setObjectMapper(objectMapper);
    }

    /**
     * 参数解析器
     * @param resolvers
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new PostMethodArgumentResolver());
        resolvers.add(new EncryptedPostMethodArgumentResolver());
    }

    /**
     * 支持跨域请求处理
     * @param registry
     */
    @Override
    public void addCorsMappings (CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*").allowedMethods("*").allowedHeaders("*").allowCredentials(true);
    }

    /**
     * LocalDateTime序列化
     */
    private static class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {

        @Override
        public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeString(value.format(DATETIME_FORMATTER));
        }
    }

    /**
     * LocalDateTime反序列化
     */
    private static class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

        @Override
        public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {

            try {
                return LocalDateTime.parse(p.getValueAsString(), DATETIME_FORMATTER);
            } catch (DateTimeParseException e) {
                return LocalDateTime.of(LocalDate.parse(p.getValueAsString()), LocalTime.of(0, 0, 0));
            }

        }
    }

    /**
     * LocalDate序列化
     */
    private static class LocalDateSerializer extends JsonSerializer<LocalDate> {

        @Override
        public void serialize(LocalDate value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeString(value.format(DATE_FORMATTER));
        }
    }

    /**
     * LocalDate反序列化
     */
    private static class LocalDateDeserializer extends JsonDeserializer<LocalDate> {

        @Override
        public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            return LocalDate.parse(p.getValueAsString(), DATE_FORMATTER);
        }
    }

    /**
     * LocalTime序列化
     */
    private static class LocalTimeSerializer extends JsonSerializer<LocalTime> {

        @Override
        public void serialize(LocalTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeString(value.format(TIME_FORMATTER));
        }
    }

    /**
     * LocalTime反序列化
     */
    private static class LocalTimeDeserializer extends JsonDeserializer<LocalTime> {

        @Override
        public LocalTime deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
            return LocalTime.parse(p.getValueAsString(), TIME_FORMATTER);
        }
    }
}

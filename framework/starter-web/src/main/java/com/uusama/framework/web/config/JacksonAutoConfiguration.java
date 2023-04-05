package com.uusama.framework.web.config;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.uusama.framework.web.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * jackson序列化配置
 *
 * @author uusama
 */
@Slf4j
@AutoConfiguration
public class JacksonAutoConfiguration {

    @Bean
    public BeanPostProcessor objectMapperBeanPostProcessor() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                if (!(bean instanceof ObjectMapper)) {
                    return bean;
                }
                ObjectMapper objectMapper = (ObjectMapper) bean;
                SimpleModule simpleModule = new SimpleModule();
                /*
                 * 1. 新增Long类型序列化规则，数值超过2^53-1，在JS会出现精度丢失问题，因此Long自动序列化为字符串类型
                 * 2. 新增LocalDateTime序列化、反序列化规则
                 */
                simpleModule
                    .addSerializer(Long.class, NumberSerializer.INSTANCE)
                    .addSerializer(Long.TYPE, NumberSerializer.INSTANCE)
                    .addSerializer(LocalDateTime.class, LocalDateTimeSerializer.INSTANCE)
                    .addDeserializer(LocalDateTime.class, LocalDateTimeDeserializer.INSTANCE);

                objectMapper.registerModules(simpleModule);
                JsonUtils.init(objectMapper);
                log.info("初始化 jackson 自动配置");
                return bean;
            }
        };
    }

    /**
     * LocalDateTime反序列化规则
     * 会将毫秒级时间戳反序列化为LocalDateTime
     *
     * @author uusama
     */
    public static class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

        public static final LocalDateTimeDeserializer INSTANCE = new LocalDateTimeDeserializer();

        @Override
        public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(p.getValueAsLong()), ZoneId.systemDefault());
        }
    }

    /**
     * LocalDateTime序列化规则
     * 会将LocalDateTime序列化为毫秒级时间戳
     *
     * @author uusama
     */
    public static class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {

        public static final LocalDateTimeSerializer INSTANCE = new LocalDateTimeSerializer();

        @Override
        public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeNumber(value.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        }
    }

    /**
     * Long 序列化规则
     * 会将超长 long 值转换为 string，解决前端 JavaScript 最大安全整数是 2^53-1 的问题
     *
     * @author uusama
     */
    public static class NumberSerializer extends com.fasterxml.jackson.databind.ser.std.NumberSerializer {

        private static final long MAX_SAFE_INTEGER = 9007199254740991L;
        private static final long MIN_SAFE_INTEGER = -9007199254740991L;

        public static final NumberSerializer INSTANCE = new NumberSerializer(Number.class);

        public NumberSerializer(Class<? extends Number> rawType) {
            super(rawType);
        }

        @Override
        public void serialize(Number value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            // 超出范围 序列化位字符串
            if (value.longValue() > MIN_SAFE_INTEGER && value.longValue() < MAX_SAFE_INTEGER) {
                super.serialize(value, gen, serializers);
            } else {
                gen.writeString(value.toString());
            }
        }
    }
}

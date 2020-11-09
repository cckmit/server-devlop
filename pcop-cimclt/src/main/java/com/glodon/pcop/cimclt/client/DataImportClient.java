package com.glodon.pcop.cimclt.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.glodon.pcop.cimapi.DataImportApi;
import com.glodon.pcop.cimclt.client.fallback.DataImportClientFallback;
import com.glodon.pcop.service.feignconfig.SnakeCaseConfig;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

/**
 * @Auther: shiyw-a
 * @Date: 2018/10/10 11:05
 * @Description:
 */
@FeignClient(value = "pcop-cimsvc",path = "/v1",fallback = DataImportClientFallback.class,configuration = DataImportClient.SnakeCaseConfig.class)
public interface DataImportClient extends DataImportApi {

    @Configuration
    class SnakeCaseConfig {
        @Bean
        public SpringDecoder feignDecoder() {
            HttpMessageConverter jacksonConverter = new MappingJackson2HttpMessageConverter(customObjectMapper());
            ObjectFactory<HttpMessageConverters> objectFactory = () -> new HttpMessageConverters(jacksonConverter);
            return new SpringDecoder(objectFactory);
        }
        @Bean
        public SpringEncoder feignEncoder(){
            HttpMessageConverter jacksonConverter = new MappingJackson2HttpMessageConverter(customObjectMapper());
            ObjectFactory<HttpMessageConverters> objectFactory = () -> new HttpMessageConverters(jacksonConverter);
            return new SpringEncoder(objectFactory);
        }

        public ObjectMapper customObjectMapper(){
            ObjectMapper objectMapper = new ObjectMapper();
            //Customize as much as you want
            objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
            objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
            return objectMapper;
        }


    }
}

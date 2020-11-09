package com.glodon.pcop.cimclt.client;

import com.glodon.pcop.cimapi.MinioClientApi;
import com.glodon.pcop.cimclt.client.fallback.MinioClientFallback;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Auther: shiyw-a
 * @Date: 2018/10/10 11:22
 * @Description:
 */
@FeignClient(value = "pcop-cimsvc",path = "/",fallback = MinioClientFallback.class,configuration = MinioClient.MultipartSupportConfig.class)
public interface MinioClient extends MinioClientApi {

    @Configuration
    class MultipartSupportConfig {

        @Autowired
        private ObjectFactory<HttpMessageConverters> messageConverters;

        @Bean
        public Encoder feignFormEncoder() {
            return new SpringFormEncoder(new SpringEncoder(messageConverters));
        }
    }
}

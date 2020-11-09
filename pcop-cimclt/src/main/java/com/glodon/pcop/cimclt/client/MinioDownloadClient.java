package com.glodon.pcop.cimclt.client;

import com.glodon.pcop.cimapi.MinioDownloadClientApi;
import com.glodon.pcop.cimclt.client.fallback.MinioDownloadClientFallback;
import com.glodon.pcop.service.feignconfig.SnakeCaseConfig;
import feign.codec.Decoder;
import feign.form.spring.converter.SpringManyMultipartFilesReader;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;

import java.util.ArrayList;
import java.util.List;


/**
 * @Auther: shiyw-a
 * @Date: 2018/10/15 16:42
 * @Description:
 */
@FeignClient(value = "pcop-cimsvc",path = "/",fallback = MinioDownloadClientFallback.class,configuration = SnakeCaseConfig.class)
public interface MinioDownloadClient extends MinioDownloadClientApi {
//    @Configuration
//    class ClientConfiguration {
//
//        @Autowired
//        private ObjectFactory<HttpMessageConverters> messageConverters;
//
//        @Bean
//        public Decoder feignDecoder () {
//            final List<HttpMessageConverter<?>> springConverters = messageConverters.getObject().getConverters();
//            final List<HttpMessageConverter<?>> decoderConverters
//                    = new ArrayList<HttpMessageConverter<?>>(springConverters.size() + 1);
//
//            decoderConverters.addAll(springConverters);
//            decoderConverters.add(new SpringManyMultipartFilesReader(4096));
//            final HttpMessageConverters httpMessageConverters = new HttpMessageConverters(decoderConverters);
//
//            return new SpringDecoder(new ObjectFactory<HttpMessageConverters>() {
//                @Override
//                public HttpMessageConverters getObject() {
//                    return httpMessageConverters;
//                }
//            });
//        }
//    }
}

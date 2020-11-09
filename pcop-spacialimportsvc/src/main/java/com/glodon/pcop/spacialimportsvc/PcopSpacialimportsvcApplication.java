package com.glodon.pcop.spacialimportsvc;

import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.engine.dataServiceCache.CacheUtil;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.config.PropertyHandler;
import com.glodon.pcop.jobclt.EnableJobClients;
import com.glodon.pcop.spacialimportsvc.service.KafkaSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


/**
 * @author Jimmy.Liu(liuzm @ glodon.com), Jul/07/2018.
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, SecurityAutoConfiguration.class, OAuth2ClientAutoConfiguration.class, MongoAutoConfiguration.class})
@EnableDiscoveryClient
@EnableSwagger2
@EnableScheduling
@EnableJobClients
public class PcopSpacialimportsvcApplication {
    private static final Logger mLog = LoggerFactory.getLogger(PcopSpacialimportsvcApplication.class);

    @Autowired
    private KafkaSender kafkaSender;

    public static void main(String[] args) {
        SpringApplication.run(PcopSpacialimportsvcApplication.class, args);
        // cacheInit();
        mLog.info("Pcop spacial import service started.");
    }

    /**
     * 初始化缓存
     */
    private static void cacheInit() {
        if (PropertyHandler.map == null || PropertyHandler.map.size() == 0) {
            mLog.error("No orientDb configuration parameters is provided.");
            System.exit(-1);//NOSONAR
        }
        try {
            mLog.info("开始初始化缓存...");
            CacheUtil.cacheInit(CimConstants.defauleSpaceName);
            mLog.info("缓存初始化完成！！！");
        } catch (Exception e) {
            mLog.error("Cache initilization failed!");
            e.printStackTrace();
        }
    }

    //然后每隔1分钟执行一次
    // @Scheduled(fixedRate = 1000 * 10)
    public void testKafka() throws Exception {
        kafkaSender.sendTest();
    }

}

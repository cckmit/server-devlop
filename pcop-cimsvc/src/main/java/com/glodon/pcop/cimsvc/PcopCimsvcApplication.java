package com.glodon.pcop.cimsvc;

import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.engine.dataServiceCache.CacheUtil;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.config.PropertyHandler;
import com.glodon.pcop.cimsvc.config.listener.ApplicationStartingEventListener;
import com.glodon.pcop.jobclt.EnableJobClients;
import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ImportResource;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


/**
 * @author Jimmy.Liu(liuzm @ glodon.com), Jul/07/2018.
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, SecurityAutoConfiguration.class, OAuth2ClientAutoConfiguration.class, MongoAutoConfiguration.class})
@EnableDiscoveryClient
@EnableSwagger2
@EnableFeignClients
@EnableJobClients
public class PcopCimsvcApplication {
    private static final Logger mLog = LoggerFactory.getLogger(PcopCimsvcApplication.class);

    public static void main(String[] args) {
        // SpringApplication.run(PcopCimsvcApplication.class, args);

        SpringApplication app = new SpringApplication(PcopCimsvcApplication.class);
        app.addListeners(new ApplicationStartingEventListener());//加入自定义的监听类
        ConfigurableApplicationContext ctx = app.run(args);
        ctx.registerShutdownHook();
        // cacheInit();
        mLog.info("Pcop CIM Data service started.");
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
}

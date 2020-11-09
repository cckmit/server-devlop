package com.glodon.pcop.weasvc;

import com.glodon.pcop.cim.engine.dataServiceCache.CacheUtil;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.RelationDirection;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineDataMartException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceFeature.BusinessLogicConstant;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.CommonTagVO;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.*;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.transferVO.UniversalDimensionAttachInfo;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceModelRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.factory.ModelAPIComponentFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;


/**
 * @author Jimmy.Liu(liuzm@glodon.com), Oct/24/2018.
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class,  MongoAutoConfiguration.class})
@EnableDiscoveryClient
@EnableSwagger2
@EnableScheduling
public class PcopWeasvcApplication {

	private static final Logger mLog = LoggerFactory.getLogger(PcopWeasvcApplication.class);

	public static void main(String[] args) {


		SpringApplication.run(PcopWeasvcApplication.class, args);
//        CacheUtil.cacheManagerInit();
		System.out.println(Runtime.getRuntime().freeMemory());
		// 的JVM内存总量（单位是字节）
		System.out.println(Runtime.getRuntime().totalMemory());
		// JVM试图使用额最大内存量（单位是字节）
		System.out.println(Runtime.getRuntime().maxMemory());
		// 可用处理器的数目
		System.out.println(Runtime.getRuntime().availableProcessors());
		mLog.info("Pcop Weather Data service started.");
	}
}

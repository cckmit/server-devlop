package com.glodon.pcop.cimstatsvc;

import com.glodon.pcop.cimstatsvc.service.DispatchService;
import com.orientechnologies.orient.core.config.OGlobalConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 * @author Jimmy.Liu(liuzm@glodon.com), Sep/20/2018.
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, MongoAutoConfiguration.class})
@EnableDiscoveryClient
@EnableSwagger2
public class PcopCimstatsvcApplication {

	private static final Logger mLog = LoggerFactory.getLogger(PcopCimstatsvcApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(PcopCimstatsvcApplication.class, args);
//		CacheUtil.cacheManagerInit();
		DispatchService dis = new DispatchService();
		dis.dispath();
		firstStat();
		mLog.info("Pcop CIM Data Statistics service started.");
	}

	//启动后初始统计
	public static void  firstStat(){
//		QualityStatistic.countProject();
//		countProject();

//		LaborWorkTeamStatistic.countProject();
//		LaborWorkTypeStatistic.countProject();
	}
}
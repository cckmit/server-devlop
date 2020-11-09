package com.glodon.cim;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CassandraAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author tangd-a
 * @date 2020/7/22 17:06
 */
@EnableSwagger2
@SpringBootApplication(exclude = { CassandraAutoConfiguration.class,DataSourceAutoConfiguration.class,MongoAutoConfiguration.class})
public class InitApplication {

	public static void main(String[] args) {
		SpringApplication.run(InitApplication.class, args);
	}

}



package com.glodon.pcop.spacialimportsvc.config;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.IOException;

@Configuration
public class JacksonConfig {

//	@Bean
	public Jackson2ObjectMapperBuilderCustomizer customJackson() {
		return new Jackson2ObjectMapperBuilderCustomizer() {

			@Override
			public void customize(Jackson2ObjectMapperBuilder jacksonObjectMapperBuilder) {
				// 只包含非null属性
				jacksonObjectMapperBuilder.serializationInclusion(Include.NON_NULL);
				jacksonObjectMapperBuilder.failOnUnknownProperties(false);
				jacksonObjectMapperBuilder.propertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
			}

		};
	}

	@Bean
	@Primary
	@ConditionalOnMissingBean(ObjectMapper.class)
	public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
		ObjectMapper objectMapper = builder.createXmlMapper(false).build();

		// Use property
		objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
		objectMapper.getSerializerProvider().setNullValueSerializer(new JsonSerializer<Object>() {
			@Override
			public void serialize(Object o, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
					throws IOException {
				jsonGenerator.writeString("");
			}
		});

		return objectMapper;
	}

}


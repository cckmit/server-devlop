package com.glodon.pcop.spacialimportsvc.config;

import java.io.IOException;

import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;

/**
 * 自定义的过滤规则，在自动扫描的时候用，自定义过滤的时候不在使用默认的规则了
 *
 * @ClassName FilterCustom
 * @author xxx
 * @date xxx
 * @version xxx
 */
public class FilterCustom implements TypeFilter {

	/**
	 * 
	 * @Title: match
	 * @Description: 覆盖方法注释标签说明
	 * @param metadataReader        读取的当前正在扫描类的信息
	 * @param metadataReaderFactory 类工厂中其它类的信息
	 * @return
	 * @throws IOException
	 * @see org.springframework.core.type.filter.TypeFilter#match(org.springframework.core.type.classreading.MetadataReader,
	 *      org.springframework.core.type.classreading.MetadataReaderFactory)
	 */
	@Override
	public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory)
			throws IOException {
		// 获取当前类注解的信息
		// AnnotationMetadata metadata = metadataReader.getAnnotationMetadata();
		// 获取当前正在扫描类的信息
		ClassMetadata classMetadata = metadataReader.getClassMetadata();
		// 获取当前类路径的信息
		// Resource resource = metadataReader.getResource();
		if (classMetadata.getClassName().startsWith("DefaultConfiguration")) {
			return true;
		}
		return false;
	}

}
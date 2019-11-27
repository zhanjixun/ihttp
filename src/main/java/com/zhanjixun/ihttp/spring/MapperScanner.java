package com.zhanjixun.ihttp.spring;

import com.zhanjixun.ihttp.IHTTP;
import com.zhanjixun.ihttp.annotations.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.SystemPropertyUtils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.util.Assert.notNull;

/**
 * spring扫描ihttp mapper接口
 *
 * @author zhanjixun
 */
@Slf4j
public class MapperScanner implements BeanDefinitionRegistryPostProcessor, ApplicationContextAware, InitializingBean, ResourceLoaderAware {

	@Getter
	@Setter
	private String basePackage;
	@Getter
	private ApplicationContext applicationContext;
	private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
	private MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resourcePatternResolver);

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		notNull(basePackage, "Property 'basePackage' is required");
	}

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
		metadataReaderFactory = new CachingMetadataReaderFactory(resourceLoader);
	}

	private Set<Class> scan(String[] basePackages) {
		return Arrays.stream(basePackages)
				.flatMap(p -> doScan(p).stream())
				.collect(Collectors.toSet());
	}

	private Set<Class> doScan(String basePackage) {
		Set<Class> classes = new HashSet<>();
		try {
			String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + ClassUtils.convertClassNameToResourcePath(SystemPropertyUtils.resolvePlaceholders(basePackage)) + "/**/*.class";
			Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);

			for (Resource resource : resources) {
				if (resource.isReadable()) {
					MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
					try {
						classes.add(Class.forName(metadataReader.getClassMetadata().getClassName()));
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (IOException ex) {
			throw new BeanDefinitionStoreException("I/O failure during classpath scanning", ex);
		}
		return classes;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		Set<Class> classes = scan(StringUtils.tokenizeToStringArray(basePackage, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS));
		for (Class clazz : classes.stream().filter(Class::isInterface).collect(Collectors.toSet())) {
			if (isMapper(clazz)) {
				Object obj = IHTTP.getMapper(clazz);
				applicationContext.getAutowireCapableBeanFactory().applyBeanPostProcessorsAfterInitialization(obj, obj.getClass().getName());
				DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
				beanFactory.registerSingleton(obj.getClass().getName(), obj);
			}
		}
	}

	private boolean isMapper(Class<?> mapperClass) {
		Class[] httpMethod = new Class[]{GET.class, POST.class, PUT.class, DELETE.class, HEAD.class, OPTIONS.class, PATCH.class, TRACE.class};
		for (Method method : mapperClass.getDeclaredMethods()) {
			for (Class httpMethodClazz : httpMethod) {
				if (method.isAnnotationPresent(httpMethodClazz)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

	}

}

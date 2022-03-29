package com.ksn.config.consumer;

import com.ksn.annotation.CustomReference;
import com.ksn.annotation.EnableCustomDubbo;
import com.ksn.common.CommonConfig;
import com.ksn.common.GlobalConstants;
import com.ksn.common.URL;
import com.ksn.common.URLBuilder;
import com.ksn.register.IRegister;
import com.ksn.register.NotifyListener;
import com.ksn.spi.ExtensionLoader;
import com.ksn.utils.ApplicationContextUtil;
import lombok.SneakyThrows;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ksn
 * @version 1.0
 * @date 2022/3/23 12:00
 * @description:
 */
@Component
public class CustomDubboRegistrar implements ImportBeanDefinitionRegistrar, ApplicationContextAware, InitializingBean {

    private static final ThreadLocal<Set<Field>> threadLocal = new ThreadLocal<>();

    private static ConcurrentHashMap<String, Field> fieldMap = new ConcurrentHashMap<>();

    private ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    @SneakyThrows
    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        String[] packages = getPackageName(metadata);
        ConfigurationBuilder scanners = new ConfigurationBuilder()
                .forPackages(packages)
                .addScanners(new FieldAnnotationsScanner());
        Reflections reflections = new Reflections(scanners);
        Set<Field> fields = reflections.getFieldsAnnotatedWith(CustomReference.class);
        threadLocal.set(fields);
        SingletonBeanRegistry beanRegistry = registry instanceof SingletonBeanRegistry ? (SingletonBeanRegistry) registry : null;
        for (Field field : fields) {
            String className = field.getType().getName();
            if (fieldMap.get(className) == null) {
                String registryName = className.substring(className.lastIndexOf(".") + 1);
                CustomDubboFactoryBean bean = new CustomDubboFactoryBean()
                        .setType(field.getType())
                        .setClassName(className);
                // 注册到spring中
                beanRegistry.registerSingleton(registryName, bean.getObject());
                fieldMap.putIfAbsent(className, field);
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        CommonConfig config = context.getBean(CommonConfig.class);
        ApplicationContextUtil applicationContextUtil = context.getBean(ApplicationContextUtil.class);
        IRegister register = ExtensionLoader.getExtensionLoader(IRegister.class).getExtension("nacos");
        Set<Field> fields = threadLocal.get();
        Set<Class<?>> set = new HashSet<>();
        if (fields != null && fields.size() > 0) {
            for (Field field : fields) {
                field.setAccessible(true);
                Class<?> type = field.getType();
                set.add(type);
                // 被注入属性的类
                Object o = ApplicationContextUtil.getBean(field.getDeclaringClass());
                CustomDubboFactoryBean bean = new CustomDubboFactoryBean()
                        .setType(field.getType())
                        .setClassName(field.getType().getName());
                // 注入属性，相当于实现了Autowired（方式二）
                field.set(o, bean.getObject());
            }
        }
        Set<URL> urls = URLBuilder.build(config, set, true);
        urls.stream().forEach(item -> {
            // 注册到注册中心
            register.register(item);
            // 订阅提供者初始化列表
            register.subscribe(convUrl(item), new NotifyListener());
        });
        threadLocal.remove();
    }

    public static String[] getPackageName(AnnotationMetadata metadata) {
        Map<String, Object> map = metadata.getAnnotationAttributes(EnableCustomDubbo.class.getName());
        String[] strArr = (String[]) map.get("scanPackages");
        if (strArr.length == 0) {
            String className = metadata.getClassName();
            int lastDotIndex = className.lastIndexOf(46);
            String path = lastDotIndex != -1 ? className.substring(0, lastDotIndex) : "";
            strArr = new String[]{path};
        }
        return strArr;
    }

    /**
     * 把url中的注册名改成provider，通过这个去订阅
     * @param url
     * @return com.ksn.common.URL
     */
    private URL convUrl(URL url) {
        if (url == null) {
            throw new RuntimeException("url对象不能为空");
        }
        String registerName = url.getRegisterName();
        if (registerName.startsWith(GlobalConstants.CONSUMERS_CATEGORY)) {
            String first = registerName.replaceFirst("^" + GlobalConstants.CONSUMERS_CATEGORY, GlobalConstants.PROVIDERS_CATEGORY);
            url.setRegisterName(first);
        };
        return url;
    }

}

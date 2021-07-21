package com.lizy.myspring.spring.context;

import com.lizy.myspring.spring.BeanDefinition;
import com.lizy.myspring.spring.InititalizingBean;
import com.lizy.myspring.spring.annotation.*;
import com.lizy.myspring.spring.aware.BeanNameAware;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lizy
 * @date 2021/7/20 15:44
 */
public class ApplicationContext {

    private Class configClazz;

    private ConcurrentHashMap<String, Object> singletonObjects = new ConcurrentHashMap<>();

    private ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    private List<BeanPostProcessor> beanPostProcessorList = new ArrayList<>();

    public ApplicationContext(Class clazz) {
        this.configClazz = clazz;

        scan(configClazz);

        for (String beanName : beanDefinitionMap.keySet()) {
            final BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if (beanDefinition.getScope().equals("singleton")) {
                final Object bean = createBean(beanName, beanDefinition);
                singletonObjects.put(beanName, bean);
            }
        }


    }

    public void scan(Class configClazz) {
        // 获取包扫描路径
        final ComponentScan componentScan = (ComponentScan) configClazz.getDeclaredAnnotation(ComponentScan.class);
        String configPath = componentScan.value();
        System.out.println(configPath);

        // 扫描
        configPath = StringUtils.replace(configPath, ".", "/");
        final ClassLoader classLoader = configClazz.getClassLoader();
        final URL resource = configClazz.getClassLoader().getResource(configPath);
        File file = new File(resource.getFile());
        if (file.isDirectory()) {
            final File[] files = file.listFiles();
            for (File f : files) {
                String absolutePath = f.getAbsolutePath();
                if (absolutePath.contains(".class")) {
                    absolutePath = StringUtils.substringAfter(absolutePath, "classes/");
                    absolutePath = StringUtils.remove(absolutePath, ".class");
                    String className = StringUtils.replace(absolutePath, "/", ".");
                    try {
                        final Class<?> loadClass = classLoader.loadClass(className);

                        // beanpostprocessor
                        if (BeanPostProcessor.class.isAssignableFrom(loadClass)) {
                            try {
                                final BeanPostProcessor beanPostProcessor = (BeanPostProcessor) loadClass.newInstance();
                                beanPostProcessorList.add(beanPostProcessor);
                            } catch (InstantiationException | IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        if (loadClass.isAnnotationPresent(Component.class)) {
                            final Component component = loadClass.getDeclaredAnnotation(Component.class);
                            final String beanName = component.value();
                            BeanDefinition beanDefinition = new BeanDefinition();
                            beanDefinition.setClazz(loadClass);
                            if (loadClass.isAnnotationPresent(Scope.class)) {
                                final Scope scope = loadClass.getDeclaredAnnotation(Scope.class);
                                beanDefinition.setScope(scope.value());
                            } else {
                                beanDefinition.setScope("singleton");
                            }

                            beanDefinitionMap.put(beanName, beanDefinition);
                        }

                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }

    public Object createBean(String beanName, BeanDefinition beanDefinition) {
        final Class clazz = beanDefinition.getClazz();
        try {
            final Object instance = clazz.newInstance();

            //依赖注入
            final Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(Autowried.class)) {
                    final Object o = singletonObjects.get(field.getName());
                    field.setAccessible(true);
                    field.set(instance, o);
                }
            }

            // aware
            if (instance instanceof BeanNameAware) {
                ((BeanNameAware) instance).setBeanName(beanName);
            }

            // 执行beanPostProcessor
            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                beanPostProcessor.postProcessBeforeInitialization(instance, beanName);
            }

            // 初始化
            if(instance instanceof InititalizingBean){
                ((InititalizingBean) instance).afterPropertiesSet();
            }

            // 执行beanPostProcessor
            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                beanPostProcessor.postProcessAfterInitialization(instance, beanName);
            }

            return instance;
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object getBean(String beanName) {
        if (beanDefinitionMap.containsKey(beanName)) {
            final BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if (beanDefinition.getScope().equals("singleton")) {
                return singletonObjects.get(beanName);
            } else {
                // 多例
                // 创建bean
                createBean(beanName, beanDefinition);
            }
        } else {
            throw new NullPointerException();
        }
        return null;
    }

}

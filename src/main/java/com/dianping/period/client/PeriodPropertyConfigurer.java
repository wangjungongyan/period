package com.dianping.period.client;

import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.TypedStringValue;

/**
 * Created by vali on 15-5-14.
 */
public class PeriodPropertyConfigurer implements BeanFactoryPostProcessor {

    private static final String DEFAULT_PLACEHOLDER_PREFIX = "${";

    private static final String DEFAULT_PLACEHOLDER_SUFFIX = "}";

    @Override public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
            throws BeansException {

        String[] beanDefinitionNames = beanFactory.getBeanDefinitionNames();

        for (int i = 0; i < beanDefinitionNames.length; i++) {
            String beanDefinitionName = beanDefinitionNames[i];
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanDefinitionName);
            MutablePropertyValues mutablePropertyValues = beanDefinition.getPropertyValues();
            PropertyValue[] propertyValues = mutablePropertyValues.getPropertyValues();

            replaceDynamicPropertyValue(propertyValues, beanFactory, beanDefinitionName);

        }

    }

    private void replaceDynamicPropertyValue(PropertyValue[] propertyValues,
                                             ConfigurableListableBeanFactory beanFactory, String beanName) {

        if (propertyValues == null || propertyValues.length == 0) {
            return;
        }

        for (PropertyValue propertyValue : propertyValues) {

            if (noNeedReplacePropertyValue(propertyValue)) {
                continue;
            }

            TypedStringValue dynamicProperty = (TypedStringValue) propertyValue.getValue();



            Object zkData = PeriodClientUtil.getProperty(
                    dynamicProperty.getValue().substring(2, dynamicProperty.getValue().length() - 1));

            dynamicProperty.setValue((String)zkData);
            /*
            String propertyName = propertyValue.getName();

//            if (zkData == null) {
            //                throw new RuntimeException(
            //                        "Init bean '" + beanName + "' fail. Because property '" + propertyName
            //                        + "' must be set a not null value."
            //                );
            //            }

            Object beanObject = beanFactory.getBean(beanName);

            BeanUtil.setProperty(beanObject, propertyName, zkData);
            */
        }
    }

    private boolean noNeedReplacePropertyValue(PropertyValue propertyValue) {

        Object property = propertyValue.getValue();

        if (!(property instanceof TypedStringValue)) {
            return true;
        }

        String propertyVariable = ((TypedStringValue) property).getValue();

        return !(propertyVariable.startsWith(DEFAULT_PLACEHOLDER_PREFIX)
                 && propertyVariable.endsWith(DEFAULT_PLACEHOLDER_SUFFIX));
    }

}

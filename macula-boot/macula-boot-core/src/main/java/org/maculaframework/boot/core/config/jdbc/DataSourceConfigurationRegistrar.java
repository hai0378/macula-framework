/*
 * Copyright 2004-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.maculaframework.boot.core.config.jdbc;

import lombok.extern.slf4j.Slf4j;
import org.maculaframework.boot.MaculaConstants;
import org.maculaframework.boot.core.utils.StringUtils;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.jdbc.JdbcProperties;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertyNameAliases;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * <p>
 * <b>DataSourceConfigurationRegistrar</b> 根据配置数据源动态注册
 * </p>
 *
 * @author Rain
 * @since 2019-02-02
 */

@Slf4j
public class DataSourceConfigurationRegistrar implements EnvironmentAware, ImportBeanDefinitionRegistrar {

    private final static ConfigurationPropertyNameAliases aliases = new ConfigurationPropertyNameAliases();
    private Environment env;
    private Binder binder;

    @Override
    @SuppressWarnings("rawtypes")
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
        log.info("开始注册数据源");
        try {
            List<Map> list = binder.bind(MaculaConstants.CONFIG_DATASOURCE_PREFIX, Bindable.listOf(Map.class)).get();

            JdbcProperties jdbcProperties = binder.bind("spring.jdbc", JdbcProperties.class).orElse(new JdbcProperties());

            for (Map dsPropMap : list) {
                Object name = dsPropMap.get("name");
                if (name != null) {
                    String dataSourceName = "dataSource-" + name;
                    String jdbcTempalteName = "jdbcTemplate-" + name;
                    String namedParameterJdbcTempalteName = "namedParameterJdbcTemplate-" + name;
                    if (!beanDefinitionRegistry.containsBeanDefinition(dataSourceName)) {
                        // 注册 DataSourceBean
                        BeanDefinition dsBeanDef = BeanDefinitionBuilder.genericBeanDefinition(DruidDataSourceWrapper.class)
                                .setInitMethodName("init")
                                .setDestroyMethodName("close")
                                .getBeanDefinition();
                        MutablePropertyValues mpv = dsBeanDef.getPropertyValues();

                        for (Object key : dsPropMap.keySet()) {
                            mpv.addPropertyValue(StringUtils.camelCaseName(key.toString()), dsPropMap.get(key));
                        }
                        beanDefinitionRegistry.registerBeanDefinition(dataSourceName, dsBeanDef);

                        // 注册 JdbcTemplate
                        BeanDefinition jdbcTemplateBeanDef = BeanDefinitionBuilder.genericBeanDefinition(JdbcTemplate.class)
                                .getBeanDefinition();

                        mpv = jdbcTemplateBeanDef.getPropertyValues();
                        mpv.addPropertyValue("dataSource", new RuntimeBeanReference(dataSourceName));

                        JdbcProperties.Template template = jdbcProperties.getTemplate();
                        mpv.addPropertyValue("fetchSize", template.getFetchSize());
                        mpv.addPropertyValue("maxRows", template.getMaxRows());
                        if (template.getQueryTimeout() != null) {
                            mpv.addPropertyValue("queryTimeout", template.getQueryTimeout());
                        }
                        beanDefinitionRegistry.registerBeanDefinition(jdbcTempalteName, jdbcTemplateBeanDef);

                        // 注册 NamedParameterJdbcTemplate
                        BeanDefinition namedParameterJdbcTemplateBeanDef = BeanDefinitionBuilder.genericBeanDefinition(NamedParameterJdbcTemplate.class)
                                .getBeanDefinition();
                        namedParameterJdbcTemplateBeanDef.getConstructorArgumentValues().addGenericArgumentValue(new RuntimeBeanReference(jdbcTempalteName));

                        beanDefinitionRegistry.registerBeanDefinition(namedParameterJdbcTempalteName, namedParameterJdbcTemplateBeanDef);
                    }
                } else {
                    log.error("No DataSource Name config!!");
                }
            }
        } catch (NoSuchElementException ex) {
            log.warn("No Macula DataSource Configuration!!");
        }
    }

    /**
     * EnvironmentAware接口的实现方法，通过aware的方式注入，此处是environment对象
     *
     * @param environment
     */
    @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;
        // 绑定配置器
        binder = Binder.get(env);
    }
}

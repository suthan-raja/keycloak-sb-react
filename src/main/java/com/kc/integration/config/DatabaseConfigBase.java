package com.kc.integration.config;

import com.kc.integration.config.constants.InitConstants;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.Properties;

public abstract class DatabaseConfigBase {

    protected final InitConstants initConstants;

    protected DatabaseConfigBase(InitConstants initConstants) {
        this.initConstants = initConstants;
    }

    protected abstract String getDataSourceUrl();
    protected abstract String getDataSourceUsername();
    protected abstract String getDataSourcePassword();
    protected abstract String getPackagesToScan();
    protected abstract String getPersistenceUnitName();
    protected abstract String getDriverClassName();

    protected DataSource buildDataSource() {
        return DataSourceBuilder.create()
                .driverClassName(getDriverClassName())
                .url(getDataSourceUrl())
                .username(getDataSourceUsername())
                .password(getDataSourcePassword())
                .build();
    }

    protected LocalContainerEntityManagerFactoryBean buildEntityManagerFactory(
            DataSource dataSource, String packagesToScan, String persistenceUnitName) {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource);
        emf.setPackagesToScan(packagesToScan);
        emf.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        emf.setJpaProperties(jpaProperties());
        emf.setPersistenceUnitName(persistenceUnitName);
        return emf;
    }


    private Properties jpaProperties() {
        Properties props = new Properties();
        props.setProperty("hibernate.hbm2ddl.auto", "none");
        props.setProperty("hibernate.show_sql", "false");
        props.setProperty("hibernate.format_sql", "true");
        return props;
    }
}

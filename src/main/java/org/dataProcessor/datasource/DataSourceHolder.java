package org.dataProcessor.datasource;

import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;

import javax.sql.DataSource;

// this holder class should be introduced due to the usage of virtual threads
// no explicit declaration of beans of DataSource type allowed
@Getter
public class DataSourceHolder {
    DataSource dataSource;

    public DataSourceHolder (String driverClassName, String username, String password, String jdbcUrl) {
        final HikariDataSource dataSource = new HikariDataSource();

        dataSource.setJdbcUrl(jdbcUrl);
        dataSource.setDriverClassName(driverClassName);
        dataSource.addDataSourceProperty("user", username);
        dataSource.addDataSourceProperty("password", password);

        this.dataSource = dataSource;
    }
}

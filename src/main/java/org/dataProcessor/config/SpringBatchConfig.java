package org.dataProcessor.config;

import lombok.extern.slf4j.Slf4j;
import org.dataProcessor.datasource.DataSourceHolder;
import org.dataProcessor.importData.CustomItemProcessor;
import org.dataProcessor.importData.CustomSkipListener;
import org.dataProcessor.importData.JobCompletionNotificationListener;
import org.dataProcessor.model.Starship;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.VirtualThreadTaskExecutor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableScheduling
@Slf4j
public class SpringBatchConfig {

    @Value("${spring.datasource.mysql.driverClassName}")
    private String mysqlDriverClassName;
    @Value("${spring.datasource.mysql.username}")
    private String mysqlUsername;
    @Value("${spring.datasource.mysql.password}")
    private String mysqlPassword;
    @Value("${spring.datasource.mysql.endpoint}")
    private String mysqlEndpoint;
    @Value("${spring.datasource.mysql.databaseName}")
    private String mysqlDatabaseName;

    @Value("${spring.datasource.postgres.driverClassName}")
    private String postgresDriverClassName;
    @Value("${spring.datasource.postgres.username}")
    private String postgresUsername;
    @Value("${spring.datasource.postgres.password}")
    private String postgresPassword;
    @Value("${spring.datasource.postgres.endpoint}")
    private String postgresEndpoint;
    @Value("${spring.datasource.postgres.databaseName}")
    private String postgresDatabaseName;

    @Bean
    public VirtualThreadTaskExecutor taskExecutor() {
        return new VirtualThreadTaskExecutor("virtual-thread-executor");
    }

    @Bean
    public JdbcPagingItemReader<Starship> itemReader() {
        return new JdbcPagingItemReaderBuilder<Starship>()
                .name("itemReader")
                .dataSource(mysqlDataSource().getDataSource())
                .queryProvider(createQuery())
                .pageSize(100)
                .fetchSize(100)
                .rowMapper(starshipsRawMapper())
                .build();
    }

    private MySqlPagingQueryProvider createQuery() {
        final Map<String, Order> sortKeys = new HashMap<>();
        sortKeys.put("id", Order.ASCENDING);
        MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
        queryProvider.setSelectClause("*");
        queryProvider.setFromClause("starships");
        queryProvider.setSortKeys(sortKeys);
        return queryProvider;
    }

    private RowMapper<Starship> starshipsRawMapper() {
        return (rs, rownumber) -> {
            Starship entity = new Starship();
            entity.setId(rs.getInt(1));
            entity.setName(rs.getString(2));
            entity.setShipClass(rs.getString(3));
            entity.setCaptain(rs.getString(4));
            entity.setLauncheYear(rs.getInt(5));
            return entity;
        };
    }


    @Bean
    public JdbcBatchItemWriter<Starship> itemWriter() {
        String sql = "INSERT INTO vessels (name, class, captain, launched_year) VALUES (:name, :shipClass, :captain, :launcheYear);";
        return new JdbcBatchItemWriterBuilder<Starship>().dataSource(postgresDatasource().getDataSource())
                .sql(sql)
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .build();
    }

    @Bean
    public ItemProcessor<Starship, Starship> itemProcessor() {
        return new CustomItemProcessor();
    }

    @Bean
    public Job importUserJob(JobRepository jobRepository,
                         JobCompletionNotificationListener listener,
                         Step step1) {
        return new JobBuilder("importUserJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1)
                .end()
                .build();
    }

    @Bean
    protected Step step1(JobRepository jobRepository,
                         PlatformTransactionManager transactionManager,
                         CustomSkipListener skipListener
    ) {

        return new StepBuilder("step1", jobRepository)
                .<Starship, Starship> chunk(10, transactionManager)
                .reader(itemReader())
                .processor(itemProcessor())
                .writer(itemWriter())
                .taskExecutor(taskExecutor())
                .faultTolerant()
                .listener(skipListener)
                .skipLimit(1)
                .skip(Exception.class)
                .build();

    }

    @Bean (name = "mysqlDataSource")
    public DataSourceHolder mysqlDataSource() {
        String jdbcUrl = "jdbc:mysql://" + mysqlEndpoint + '/' + mysqlDatabaseName;
        return new DataSourceHolder(mysqlDriverClassName, mysqlUsername, mysqlPassword, jdbcUrl);
    }

    @Bean (name = "postgresDatasource")
    public DataSourceHolder postgresDatasource() {
        String jdbcUrl = "jdbc:postgresql://" + postgresEndpoint + '/' + postgresDatabaseName;
        return new DataSourceHolder(postgresDriverClassName, postgresUsername, postgresPassword, jdbcUrl);
    }

}

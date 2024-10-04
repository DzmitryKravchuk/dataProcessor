package org.dataProcessor.importData;

import lombok.extern.slf4j.Slf4j;
import org.dataProcessor.datasource.DataSourceHolder;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Slf4j
@Component
public class JobCompletionNotificationListener implements JobExecutionListener {

    private final DataSource datasource;

    @Autowired
    public JobCompletionNotificationListener(DataSourceHolder postgresDatasource) {
        this.datasource = postgresDatasource.getDataSource();
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        int itemsFound = 0;
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("!!! JOB FINISHED! Time to verify the results");

            String sqlCount = "SELECT COUNT(id) FROM vessels";

            try (Connection con = datasource.getConnection()){
                PreparedStatement stmt = con.prepareStatement(sqlCount);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    itemsFound = rs.getInt(1);
                }
            } catch (Exception ignored) {
                log.error(ignored.getMessage());
            }
            log.info("Found {} items in the vessels database", itemsFound);
        }
    }
}
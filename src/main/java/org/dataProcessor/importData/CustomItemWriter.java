package org.dataProcessor.importData;

import lombok.extern.slf4j.Slf4j;
import org.dataProcessor.model.Starship;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.util.List;

@Slf4j
public class CustomItemWriter implements ItemWriter<Starship> {
    private static final String SQL = "INSERT INTO vessels (name, class, captain, launched_year) VALUES (?,?,?,?);";

    private JdbcTemplate jdbcTemplate;

    public CustomItemWriter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void write(Chunk<? extends Starship> chunk) {
        List<? extends Starship> starships = chunk.getItems();
        try {
            jdbcTemplate.batchUpdate(SQL, (List<Starship>) starships,
            5,
            (PreparedStatement ps, Starship entity) -> {
                ps.setString(1, entity.getName());
                ps.setString(2, entity.getShipClass());
                ps.setString(3, entity.getCaptain());
                ps.setInt(4, entity.getLauncheYear());
            });
        } catch (Exception e){
            log.warn("got error {}", e.getMessage());
        }
    }


}

package com.heatguideIOT.demo.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;

@Repository
public class DatabaseUtils {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseUtils(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> getTableColumns(String tableName) {
        return jdbcTemplate.queryForList("EXEC sp_columns ?", tableName);
    }
}

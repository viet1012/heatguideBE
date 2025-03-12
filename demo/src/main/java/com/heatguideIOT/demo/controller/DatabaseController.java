package com.heatguideIOT.demo.controller;
import com.heatguideIOT.demo.repository.DatabaseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;

@RestController
public class DatabaseController {

    @Autowired
    private DatabaseUtils databaseUtils;

    @GetMapping("/check-columns")
    public List<Map<String, Object>> checkColumns(@RequestParam String tableName) {
        return databaseUtils.getTableColumns(tableName);
    }
}

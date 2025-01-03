package com.finzly.config_management.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

@RestController
@RequestMapping("/api")
public class DynamicDatabaseController {

    @PostMapping("/fetch-table-data")
    public ResponseEntity<?> fetchTableData(@RequestParam String hostname,
                                            @RequestParam String username,
                                            @RequestParam String password,
                                            @RequestParam String databaseName) {
        String jdbcUrl = "jdbc:mysql://" + hostname + ":3309/" + databaseName+"?zeroDateTimeBehavior=convertToNull";
        System.out.println("Connecting to: " + jdbcUrl);
        List<Map<String, Object>> tableData = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {

            // Fetch the first table in the database
            try (PreparedStatement showTablesStmt = connection.prepareStatement("SHOW TABLES");
                 ResultSet tablesResultSet = showTablesStmt.executeQuery()) {

                if (tablesResultSet.next()) {
                    String tableName = tablesResultSet.getString(1);

                    // Fetch column names for the table
                    String selectQuery = "SELECT * FROM " + tableName;
                    try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery);
                         ResultSet dataResultSet = selectStmt.executeQuery()) {

                        ResultSetMetaData metaData = dataResultSet.getMetaData();
                        int columnCount = metaData.getColumnCount();
                        List<String> columnNames = new ArrayList<>();
                        for (int i = 1; i <= columnCount; i++) {
                            columnNames.add(metaData.getColumnName(i));
                        }

                        // Fetch data from the table
                        while (dataResultSet.next()) {
                            Map<String, Object> row = new LinkedHashMap<>();
                            for (int i = 1; i <= columnCount; i++) {
                                row.put(columnNames.get(i - 1), dataResultSet.getObject(i)); // Add column names as keys
                            }
                            tableData.add(row);
                        }
                    }
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No tables found in the specified database.");
                }
            }
            return ResponseEntity.ok(tableData);

        } catch (SQLException e) {
            String errorMessage = "Error: Unable to connect or fetch table data. Please check your inputs.";
            System.err.println("Exception: " + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }

    }
}

package com.finzly.config_management.service;

import com.finzly.config_management.Exception.DataNotFoundException;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class TestingService{
    public Map<String,Object> fetchConfigProperties(String env){
        Map<String, Object> properties = new HashMap<>();

        try (Connection connection = DriverManager.getConnection("ef","ef","erf")) {

            // Fetch the first table in the database
            try (PreparedStatement showTablesStmt = connection.prepareStatement("SHOW TABLES");
                 ResultSet tablesResultSet = showTablesStmt.executeQuery()) {

                if (tablesResultSet.next()) {
                    String tableName = tablesResultSet.getString(1);

                    // Fetch data from the table
                    String selectQuery = "SELECT * FROM " + tableName;
                    try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery);
                         ResultSet dataResultSet = selectStmt.executeQuery()) {

                        ResultSetMetaData metaData = dataResultSet.getMetaData();
                        int columnCount = metaData.getColumnCount();
                        List<String> columnNames = new ArrayList<>();

                        for (int i = 1; i <= columnCount; i++) {
                            columnNames.add(metaData.getColumnName(i));
                        }

                        while (dataResultSet.next()) {
                            Object key = null;
                            Object value = null;

                            for (int i = 1; i <= columnCount; i++) {
                                if ("prop_key".equalsIgnoreCase(columnNames.get(i - 1))) {
                                    key = dataResultSet.getObject(i);
                                }
                                if ("value".equalsIgnoreCase(columnNames.get(i - 1))) {
                                    value = dataResultSet.getObject(i);
                                }
                            }

                            if (key != null && value != null) {
                                properties.put(key.toString(), value);
                            }
                        }
                    }
                } else {
                    throw new DataNotFoundException("No tables found in the specified database.");
                }
            }
        } catch (SQLException | DataNotFoundException e) {
            e.printStackTrace(); // Log the exception for debugging
            throw new RuntimeException("Database error: " + e.getMessage(), e);
        }

        return properties;
    }
}
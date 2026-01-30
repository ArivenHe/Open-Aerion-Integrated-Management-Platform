package cn.ariven.openaimpbackend.common;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

public class SchemaInspector {
    public static void main(String[] args) {
        try (Connection conn = DatabaseManager.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            
            System.out.println("Tables:");
            try (ResultSet rs = meta.getTables(null, null, "%", null)) {
                while (rs.next()) {
                    String tableName = rs.getString("TABLE_NAME");
                    System.out.println("  " + tableName);
                    
                    if (tableName.equalsIgnoreCase("approach") || 
                        tableName.equalsIgnoreCase("runway") || 
                        tableName.equalsIgnoreCase("airport")) {
                        System.out.println("    Columns for " + tableName + ":");
                        try (ResultSet cols = meta.getColumns(null, null, tableName, null)) {
                            while (cols.next()) {
                                System.out.println("      " + cols.getString("COLUMN_NAME") + " (" + cols.getString("TYPE_NAME") + ")");
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

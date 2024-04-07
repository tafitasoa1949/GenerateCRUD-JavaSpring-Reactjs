package database_details;

import connecting.Connexion;
import database_details.ForeignKeyDetails;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;

public class DatabaseDetails {
    private String databaseName;
    private String tableName;
    private String columnName;
    private String columnType;
    private String columnSize;
    private String columnNullable;
    private String columnRemarks;
    private ArrayList<ForeignKeyDetails> foreignKeys;
    private ArrayList<ForeignKeyDetails> prims;


    public DatabaseDetails() {
        this.foreignKeys = new ArrayList<>();
        this.prims = new ArrayList<>();
    }

    public DatabaseDetails(String databaseName, String tableName, String columnName, String columnType,
                           String columnSize, String columnNullable, String columnRemarks,
                           ArrayList<ForeignKeyDetails> foreignKeys, ArrayList<ForeignKeyDetails> prims) {
        this.databaseName = databaseName;
        this.tableName = tableName;
        this.columnName = columnName;
        this.columnType = columnType;
        this.columnSize = columnSize;
        this.columnNullable = columnNullable;
        this.columnRemarks = columnRemarks;
        this.foreignKeys = foreignKeys;
        this.prims = prims;
    }

    public ArrayList<ForeignKeyDetails> getForeignKeys() {
        return foreignKeys;
    }

    public void setForeignKeys(ArrayList<ForeignKeyDetails> foreignKeys) {
        this.foreignKeys = foreignKeys;
    }

    public ArrayList<ForeignKeyDetails> getPrims() {
        return prims;
    }

    public void setPrims(ArrayList<ForeignKeyDetails> prims) {
        this.prims = prims;
    }


    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public String getColumnSize() {
        return columnSize;
    }

    public void setColumnSize(String columnSize) {
        this.columnSize = columnSize;
    }

    public String getColumnNullable() {
        return columnNullable;
    }

    public void setColumnNullable(String columnNullable) {
        this.columnNullable = columnNullable;
    }

    public String getColumnRemarks() {
        return columnRemarks;
    }

    public void setColumnRemarks(String columnRemarks) {
        this.columnRemarks = columnRemarks;
    }

    public static ArrayList<DatabaseDetails> getDatabaseDetailsFromDatabase() throws Exception {
        ArrayList<DatabaseDetails> databaseDetails = new ArrayList<>();
        try {
            Connection connection = Connexion.getConnection("postgres");
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet resultSet = metaData.getTables(null, null, "%", new String[] { "TABLE" });
            while (resultSet.next()) {
                String tableName = resultSet.getString(3);
                ResultSet columnsResultSet = metaData.getColumns(null, null, tableName, null);
                while (columnsResultSet.next()) {
                    DatabaseDetails databaseDetail = new DatabaseDetails();
                    databaseDetail.setTableName(columnsResultSet.getString(3));
                    databaseDetail.setColumnName(columnsResultSet.getString(4));
                    databaseDetail.setColumnType(columnsResultSet.getString(6));
                    databaseDetail.setColumnSize(columnsResultSet.getString(7));
                    databaseDetail.setColumnNullable(columnsResultSet.getString(11));
                    databaseDetail.setColumnRemarks(columnsResultSet.getString(12));
                    databaseDetails.add(databaseDetail);
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return databaseDetails;
    }




    public static ArrayList<DatabaseDetails> getDatabaseDetailsFK() throws Exception {
        ArrayList<DatabaseDetails> databaseDetails = new ArrayList<>();
        try {
            Connection connection = Connexion.getConnection("postgres");
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet resultSet = metaData.getTables(null, null, "%", new String[]{"TABLE"});

            while (resultSet.next()) {
                DatabaseDetails databaseDetail = new DatabaseDetails();
                String tableName = resultSet.getString(3);
                databaseDetail.setTableName(tableName);

                ResultSet columnsResultSet = metaData.getColumns(null, null, tableName, null);
                while (columnsResultSet.next()) {
                    databaseDetail.setColumnName(columnsResultSet.getString(4));
                    databaseDetail.setColumnType(columnsResultSet.getString(6));
                    databaseDetail.setColumnSize(columnsResultSet.getString(7));
                    databaseDetail.setColumnNullable(columnsResultSet.getString(11));
                    databaseDetail.setColumnRemarks(columnsResultSet.getString(12));
                }
                ResultSet exportedKeysResultSet = metaData.getImportedKeys(null, null, tableName);
                ArrayList<ForeignKeyDetails> foreignKeysList = new ArrayList<>();
                while (exportedKeysResultSet.next()) {
                    String fkTableName = exportedKeysResultSet.getString("FKTABLE_NAME");
                    String fkColumnName = exportedKeysResultSet.getString("FKCOLUMN_NAME");
                    ForeignKeyDetails foreignKeyDetail = new ForeignKeyDetails(fkTableName, fkColumnName);
                    foreignKeysList.add(foreignKeyDetail);
                }
                databaseDetail.setForeignKeys(foreignKeysList);

                databaseDetails.add(databaseDetail);
            }
        } catch (Exception e) {
            throw e;
        }
        return databaseDetails;
    }







}

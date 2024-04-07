package database_details;

public class ForeignKeyDetails {
    private String fkTableName;
    private String fkColumnName;

    public ForeignKeyDetails(){

    }

    public ForeignKeyDetails(String fkTableName, String fkColumnName) {
        this.fkTableName = fkTableName;
        this.fkColumnName = fkColumnName;
    }

    public String getFkTableName() {
        return fkTableName;
    }

    public void setFkTableName(String fkTableName) {
        this.fkTableName = fkTableName;
    }

    public String getFkColumnName() {
        return fkColumnName;
    }

    public void setFkColumnName(String fkColumnName) {
        this.fkColumnName = fkColumnName;
    }

}

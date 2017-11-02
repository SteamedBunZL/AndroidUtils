package com.clean.spaceplus.cleansdk.junk.engine;

/**
 * @author dongdong.huang
 * @Description:
 * @date 2016/5/5 15:55
 * @copyright TCL-MIG
 */
public class DBColumn {
    private String tableName;
    private String columnIndex;

    public DBColumn() {
    }

    public DBColumn(String tableName, String columnIndex) {
        this.tableName = tableName;
        this.columnIndex = columnIndex;
    }

    public boolean equals(String tableName, String columnIndex) {
        return this.tableName.equals(tableName) && this.columnIndex.equals(columnIndex);
    }

    @Override
    public boolean equals(Object o) {
        DBColumn other = (DBColumn) o;
        return this.tableName.equals(other.tableName) && this.columnIndex.equals(other.columnIndex);
    }
}

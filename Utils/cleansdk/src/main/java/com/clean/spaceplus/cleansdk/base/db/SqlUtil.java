package com.clean.spaceplus.cleansdk.base.db;

import com.hawkclean.framework.log.NLog;

import java.util.Collection;

/**
 * @author dongdong.huang
 * @Description: sql语句帮助类
 * @date 2016/4/26 14:49
 * @copyright TCL-MIG
 */
public class SqlUtil {
    private static String arrayToSQLInString(Object[] col, int start, int end) {
        if (col == null || col.length == 0 || start >= end || start < 0 || end > col.length) {
            return null;
        }
        Object[] array = col;

        String strFirst = "'" + array[start] + "'";

        //先预先估算下需要的buffer大小
        int bufSize =
                2 //前后两个括号
                        + (end - start) * (strFirst.length() + 1);//逗号+字串

        StringBuilder sb = new StringBuilder(bufSize);
        sb.append('(');
        sb.append(strFirst);
        for (int i = start + 1; i < end; i++) {
            sb.append(",");
            sb.append("'" + array[i] + "'");
        }
        sb.append(')');
        return sb.toString();
    }

    public static String arrayLongToSQLInStringIncreasing(
            long[] col, int pageSize, int pageNum) {
        NLog.i("test--error--", "test--error--"+col+", size:"+pageSize+",num:"+pageNum);
        int start = pageNum * pageSize;
        if (col != null && col.length > 0 && pageSize >= 0 && pageNum >= 0
                && start < col.length) {
            return arrayLongToSQLInString(col, start, start + pageSize > col.length
                    ? col.length
                    : start + pageSize);
        }
        return null;
    }

    private static String arrayLongToSQLInString(long[] col, int start, int end) {
        if (col == null) {
            return null;
        }
        if (col.length == 0 || start >= end || start < 0 || end > col.length) {
            return null;
        }
        long[] array = col;
        StringBuilder sb = new StringBuilder(array.length * 7);
        sb.append('(');
        sb.append(array[start]);
        if (start + 1 < end) {
            for (int i = start + 1; i < end; i++) {
                sb.append(", ");
                sb.append(array[i]);
            }
        }
        sb.append(')');
        return sb.toString();
    }

    public static String collectionSToSQLInString(Collection<? extends CharSequence> col) {
        if (col == null) {
            return null;
        }
        if (col.size() == 0) {
            return null;
        }
        return arrayToSQLInString(col.toArray(), 0, col.size());
    }

    public static String collectionToSQLInStringIncreasing(Collection<String> col, int pageSize, int pageNum) {
        return collectionToSQLInStringIncreasing(col, pageSize, pageNum, false);
    }

    public static String collectionToSQLInStringIncreasing(
            Collection<String> col, int pageSize, int pageNum, boolean blob) {
        int start = pageNum * pageSize;
        if (!isCollectionEmpty(col) && pageSize >= 0 && pageNum >= 0
                && start < col.size()) {
            int end = (start + pageSize > col.size()) ? col.size():start + pageSize;
            return collectionSToSQLInString(col, start, end, blob);
        }

        return null;
    }

    private static boolean isCollectionEmpty(Collection<? extends Object> col){
        return col == null || col.size() == 0;
    }

    private static String collectionSToSQLInString(Collection<? extends CharSequence> col, int start, int end, boolean hex) {
        if (col == null) {
            return null;
        }
        if (col.size() == 0 || start >= end || start < 0 || end > col.size()) {
            return null;
        }
        return hex ? arrayToSQLInHEXString(col.toArray(), start, end) : arrayToSQLInString(col.toArray(), start, end);
    }

    private static String arrayToSQLInHEXString(Object[] col, int start, int end) {
        if (col == null) {
            return null;
        }
        if (col.length == 0 || start >= end || start < 0 || end > col.length) {
            return null;
        }

        Object[] array = col;

        String strFirst = "x'" + array[start] + "'";

        //先预先估算下需要的buffer大小,要进行这样处理的多是md5值,这个估算还是比较准确的
        int bufSize =
                2 //前后两个括号
                        + (end - start) * (strFirst.length() + 1);//逗号+字串

        StringBuilder sb = new StringBuilder(bufSize);
        sb.append('(');
        sb.append(strFirst);
        if (start + 1 < end) {
            for (int i = start + 1; i < end; i++) {
                sb.append(",");
                sb.append("x'" + array[i] + "'");
            }
        }
        sb.append(')');
        return sb.toString();
    }


    public static void appendSqlInExpString(StringBuilder builder, Collection<String> strs) {
        if (strs == null || strs.isEmpty())
            return ;

        int i = 0;
        builder.append("(");
        for (String str : strs) {
            if (i != 0) {
                builder.append(",");
            }
            builder.append(str);
            ++i;
        }
        builder.append(")");
    }


    /**
     * 组装查询指定表明的指定字段
     * @param tableName
     * @param selection
     * @return
     */
    public static String appendSqlString(String tableName, String[] selection){
        StringBuffer buffer = new StringBuffer();
        buffer.append("select ");
        int length = selection.length;
        for (int i = 0; i < length; i++){
            buffer.append(selection[i]);
            if (i != length - 1){
                buffer.append(",");
            }
        }
        buffer.append(" from ").append(tableName);
        return buffer.toString();
    }

}

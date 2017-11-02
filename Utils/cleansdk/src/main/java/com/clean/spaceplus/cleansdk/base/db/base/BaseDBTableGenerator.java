package com.clean.spaceplus.cleansdk.base.db.base;

import com.clean.spaceplus.cleansdk.base.db.TableCodec;

import java.util.List;

/**
 * @author Jerry
 * @Description:
 * @date 2016/6/29 10:01
 * @copyright TCL-MIG
 */
public interface BaseDBTableGenerator {
     List<TableCodec<?>> generateTableBeans();
}

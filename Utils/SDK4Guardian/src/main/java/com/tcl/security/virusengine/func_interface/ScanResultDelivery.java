package com.tcl.security.virusengine.func_interface;

import android.support.annotation.IntDef;

import com.tcl.security.virusengine.entry.ScanInfo;

/**
 * Created by Steve on 2016/4/29.
 */
public interface ScanResultDelivery {

    /**Delivery the Event that the number of Scan*/
    int DELIVERY_EVENT_NUMERIC = 0;

    /**Delivery the Event when scan each entry*/
    int DELIVERY_EVENT_ENTRY =1;

    /**Delivery the Event when you prepare to scan*/
    int DELIVERY_EVENT_PRE = 2;

    /**Delivery the Event when you cancel the scan*/
    int DELIVERY_EVENT_CANCEL = 3;

    /**Delivery the Event that fake progress*/
    //int DELIVERY_EVENT_FAKE  =4;

    int DELIVERY_EVENT_UPLOAD = 10;

    @IntDef({DELIVERY_EVENT_NUMERIC, DELIVERY_EVENT_ENTRY, DELIVERY_EVENT_PRE, DELIVERY_EVENT_CANCEL,
   DELIVERY_EVENT_UPLOAD})
    @interface DeliveryEvent{}


    void postScanInfo(@DeliveryEvent int type, ScanInfo info, Object... args);

}

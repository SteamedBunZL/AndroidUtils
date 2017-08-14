package com.tcl.security.cloudengine;


public class RttProfile {
    public static final int TYPE_QUERY = 1;
    public static final int TYPE_UPDATE = 2;
    public static final int TYPE_COMMON = 3;

    public int itemType;
    public int itemCount;
    public int reqSize;
    public int rspSize;
    public long rtt;
    public int netType;

    static RttProfile dup(RttProfile rtt) {
        return new RttProfile(rtt.itemType, rtt.itemCount, rtt.reqSize, rtt.rspSize, rtt.rtt, rtt.netType);
    }

    public RttProfile() {
    }

    public RttProfile(int itemType, int itemCount, int reqSize, int rspSize, long rtt, int netType) {
        this.itemType = itemType;
        this.itemCount = itemCount;
        this.reqSize = reqSize;
        this.rspSize = rspSize;
        this.rtt = rtt;
        this.netType = netType;
    }

    @Override
    public String toString() {
        if (ProjectEnv.bDebug) {
            StringBuilder builder = new StringBuilder(256);
            builder.append("item type:");
            builder.append(itemType);
            builder.append("|");
            builder.append("item count:");
            builder.append(itemCount);
            builder.append("|");
            builder.append("req bytes count:");
            builder.append(reqSize);
            builder.append("|");
            builder.append("rsp bytes count:");
            builder.append(rspSize);
            builder.append("|");
            builder.append("RTT:");
            builder.append(rtt);
            builder.append("|");
            builder.append("network type:");
            builder.append(netType);
            return builder.toString();
        } else {
            return super.toString();
        }
    }
}

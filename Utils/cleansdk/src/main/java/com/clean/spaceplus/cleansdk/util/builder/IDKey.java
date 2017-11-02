package com.clean.spaceplus.cleansdk.util.builder;

/**
 * @author wangtianbao
 * @Description:
 * @date 2016/5/11 17:44
 * @copyright TCL-MIG
 */
final class IDKey
{
    private final Object value;
    private final int id;

    public IDKey(Object _value)
    {
        this.id = System.identityHashCode(_value);

        this.value = _value;
    }

    public int hashCode()
    {
        return this.id;
    }

    public boolean equals(Object other)
    {
        if (!(other instanceof IDKey)) {
            return false;
        }
        IDKey idKey = (IDKey)other;
        if (this.id != idKey.id) {
            return false;
        }
        return this.value == idKey.value;
    }
}


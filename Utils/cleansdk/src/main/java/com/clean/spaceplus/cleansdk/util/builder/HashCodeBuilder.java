package com.clean.spaceplus.cleansdk.util.builder;

/**
 * @author wangtianbao
 * @Description:
 * @date 2016/5/11 17:43
 * @copyright TCL-MIG
 */
public class HashCodeBuilder implements Builder<Integer>
{
//    private static final ThreadLocal<Set<IDKey>> REGISTRY = new ThreadLocal();
    private final int iConstant;

//    static Set<IDKey> getRegistry()
//    {
//        return (Set)REGISTRY.get();
//    }

//    static boolean isRegistered(Object value)
//    {
//        Set<IDKey> registry = getRegistry();
//        return (registry != null) && (registry.contains(new IDKey(value)));
//    }





//    static void register(Object value)
//    {
//        synchronized (HashCodeBuilder.class)
//        {
//            if (getRegistry() == null) {
//                REGISTRY.set(new HashSet());
//            }
//        }
//        getRegistry().add(new IDKey(value));
//    }
//
//    static void unregister(Object value)
//    {
//        Set<IDKey> registry = getRegistry();
//        if (registry != null)
//        {
//            registry.remove(new IDKey(value));
//            synchronized (HashCodeBuilder.class)
//            {
//                registry = getRegistry();
//                if ((registry != null) && (registry.isEmpty())) {
//                    REGISTRY.remove();
//                }
//            }
//        }
//    }

    private int iTotal = 0;

    public HashCodeBuilder()
    {
        this.iConstant = 37;
        this.iTotal = 17;
    }

//    public HashCodeBuilder(int initialOddNumber, int multiplierOddNumber)
//    {
//        if (initialOddNumber % 2 == 0) {
//            if (PublishVersionManager.isTest()) {
//                throw new IllegalArgumentException("HashCodeBuilder requires an odd initial value");
//            }
//        }
//        if (multiplierOddNumber % 2 == 0) {
//            if (PublishVersionManager.isTest()) {
//                throw new IllegalArgumentException("HashCodeBuilder requires an odd multiplier");
//            }
//        }
//        this.iConstant = multiplierOddNumber;
//        this.iTotal = initialOddNumber;
//    }

    public HashCodeBuilder append(boolean value)
    {
        this.iTotal = (this.iTotal * this.iConstant + (value ? 0 : 1));
        return this;
    }

    public HashCodeBuilder append(boolean[] array)
    {
        if (array == null) {
            this.iTotal *= this.iConstant;
        } else {
            for (boolean element : array) {
                append(element);
            }
        }
        return this;
    }

    public HashCodeBuilder append(byte value)
    {
        this.iTotal = (this.iTotal * this.iConstant + value);
        return this;
    }

    public HashCodeBuilder append(byte[] array)
    {
        if (array == null) {
            this.iTotal *= this.iConstant;
        } else {
            for (byte element : array) {
                append(element);
            }
        }
        return this;
    }

    public HashCodeBuilder append(char value)
    {
        this.iTotal = (this.iTotal * this.iConstant + value);
        return this;
    }

    public HashCodeBuilder append(char[] array)
    {
        if (array == null) {
            this.iTotal *= this.iConstant;
        } else {
            for (char element : array) {
                append(element);
            }
        }
        return this;
    }

    public HashCodeBuilder append(double value)
    {
        return append(Double.doubleToLongBits(value));
    }

    public HashCodeBuilder append(double[] array)
    {
        if (array == null) {
            this.iTotal *= this.iConstant;
        } else {
            for (double element : array) {
                append(element);
            }
        }
        return this;
    }

    public HashCodeBuilder append(float value)
    {
        this.iTotal = (this.iTotal * this.iConstant + Float.floatToIntBits(value));
        return this;
    }

    public HashCodeBuilder append(float[] array)
    {
        if (array == null) {
            this.iTotal *= this.iConstant;
        } else {
            for (float element : array) {
                append(element);
            }
        }
        return this;
    }

    public HashCodeBuilder append(int value)
    {
        this.iTotal = (this.iTotal * this.iConstant + value);
        return this;
    }

    public HashCodeBuilder append(int[] array)
    {
        if (array == null) {
            this.iTotal *= this.iConstant;
        } else {
            for (int element : array) {
                append(element);
            }
        }
        return this;
    }

    public HashCodeBuilder append(long value)
    {
        this.iTotal = (this.iTotal * this.iConstant + (int)(value ^ value >> 32));
        return this;
    }

    public HashCodeBuilder append(long[] array)
    {
        if (array == null) {
            this.iTotal *= this.iConstant;
        } else {
            for (long element : array) {
                append(element);
            }
        }
        return this;
    }

    public HashCodeBuilder append(Object object)
    {
        if (object == null) {
            this.iTotal *= this.iConstant;
        } else if (object.getClass().isArray())
        {
            if ((object instanceof long[])) {
                append((long[])object);
            } else if ((object instanceof int[])) {
                append((int[])object);
            } else if ((object instanceof short[])) {
                append((short[])object);
            } else if ((object instanceof char[])) {
                append((char[])object);
            } else if ((object instanceof byte[])) {
                append((byte[])object);
            } else if ((object instanceof double[])) {
                append((double[])object);
            } else if ((object instanceof float[])) {
                append((float[])object);
            } else if ((object instanceof boolean[])) {
                append((boolean[])object);
            } else {
                append((Object[])object);
            }
        }
        else {
            this.iTotal = (this.iTotal * this.iConstant + object.hashCode());
        }
        return this;
    }

    public HashCodeBuilder append(Object[] array)
    {
        if (array == null) {
            this.iTotal *= this.iConstant;
        } else {
            for (Object element : array) {
                append(element);
            }
        }
        return this;
    }

    public HashCodeBuilder append(short value)
    {
        this.iTotal = (this.iTotal * this.iConstant + value);
        return this;
    }

    public HashCodeBuilder append(short[] array)
    {
        if (array == null) {
            this.iTotal *= this.iConstant;
        } else {
            for (short element : array) {
                append(element);
            }
        }
        return this;
    }

//    public HashCodeBuilder appendSuper(int superHashCode)
//    {
//        this.iTotal = (this.iTotal * this.iConstant + superHashCode);
//        return this;
//    }

    public int toHashCode()
    {
        return this.iTotal;
    }

    public Integer build()
    {
        return Integer.valueOf(toHashCode());
    }

    public int hashCode()
    {
        return toHashCode();
    }
}

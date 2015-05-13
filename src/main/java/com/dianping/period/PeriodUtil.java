package com.dianping.period;

public class PeriodUtil {

    public static int getProperty2Int(String key) {
        Object originalValue = PeriodDataPool.get(key);
        return originalValue == null ? 0 : new Integer(originalValue.toString()).intValue();
    }

    public static String getProperty2String(String key) {
        return (String) PeriodDataPool.get(key);
    }

    public static boolean getProperty2Boolean(String key) {
        Object originalValue = PeriodDataPool.get(key);
        return originalValue == null ? false : new Boolean(originalValue.toString()).booleanValue();
    }

    public static float getProperty2Float(String key) {
        Object originalValue = PeriodDataPool.get(key);
        return originalValue == null ? 0 : new Float(originalValue.toString()).floatValue();
    }

    public static double getProperty2Double(String key) {
        Object originalValue = PeriodDataPool.get(key);
        return originalValue == null ? 0 : new Double(originalValue.toString()).doubleValue();
    }

    public static short getProperty2Short(String key) {
        Object originalValue = PeriodDataPool.get(key);
        return originalValue == null ? 0 : new Short(originalValue.toString()).shortValue();
    }

    public static long getProperty2Long(String key) {
        Object originalValue = PeriodDataPool.get(key);
        return originalValue == null ? 0 : new Long(originalValue.toString()).longValue();
    }

}

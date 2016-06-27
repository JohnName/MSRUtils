package msr.msrlibrary.utils;

import java.util.ArrayList;

/**
 * GArrayList的实现类
 */
public class MSRArrayList extends ArrayList<Object> {

    public String getString(int index) {
        return (String) get(index);
    }

    public boolean getBoolean(int index) {
        return (Boolean) get(index);
    }

    public int getInt(int index) {
        return (Integer) get(index);
    }

    public float getFloat(int index) {
        return (Float) get(index);
    }

    public double getDouble(int index) {
        return (Double) get(index);
    }

    public MSRHashMap getSAFHashMap(int index) {
        return (MSRHashMap) get(index);
    }

    public MSRArrayList getSAFArrayList(int index) {
        return (MSRArrayList) get(index);
    }

}

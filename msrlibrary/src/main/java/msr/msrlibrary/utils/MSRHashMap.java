package msr.msrlibrary.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;

/**
 * MSRHashMap
 */
public class MSRHashMap extends HashMap<String, Object> implements Serializable {

    private static final long serialVersionUID = 1000L;

    public MSRHashMap() {
    }

    public static MSRHashMap fromJson2SAFHashMap(String jsonString) {
        try {
            return MSRUtils.convertJSONToMap(new JSONObject(jsonString));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getString(String key) {
        return (String) get(key);
    }

    public boolean getBoolean(String key) {
        return (Boolean) get(key);
    }

    public int getInt(String key) {
        return (Integer) get(key);
    }

    public float getFloat(String key) {
        return (Float) get(key);
    }

    public double getDouble(String key) {
        return (Double) get(key);
    }

    public long getLong(String key) {
        return (Long) (get(key));
    }

    public MSRHashMap getMSRHashMap(String key) {
        return (MSRHashMap) get(key);
    }

    public MSRArrayList getMSRArrayList(String key) {
        return (MSRArrayList) get(key);
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return super.toString();
    }

}

package com.fns.grivet.model;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.json.JSONArray;
import org.json.JSONObject;


public class ValueHelper {

    public static Object getValue(AttributeType attributeType, Object source) {
        Object value = null;
        String stringValue = String.valueOf(source);
        // conversion takes place here...
        switch (attributeType) {
            case BIG_INTEGER:
                value = Long.valueOf(stringValue);
                break;
            case DATETIME:
                if (!stringValue.endsWith("Z")) {
                    stringValue = String.format("%s%s", stringValue, "Z");
                }
                value = stringValue;
                break;
            case DECIMAL:
                value = Double.valueOf(stringValue);
                break;
            case INTEGER:
                value = Integer.valueOf(stringValue);
                break;
            case VARCHAR:
                value = stringValue;
                break;
            case TEXT:
                value = stringValue;
                break;
            case JSON_BLOB:
                if (stringValue.startsWith("{")) {
                    value = new JSONObject(stringValue);
                }
                if (stringValue.startsWith("[")) {
                    value = new JSONArray(stringValue);
                }
                break;
        }
        return value;
    }
    
    public static Object toValue(AttributeType attributeType, Object source) {
        Object value = null;
        String stringValue = String.valueOf(source);
        // conversion takes place here...
        switch (attributeType) {
            case BIG_INTEGER:
                value = Long.valueOf(stringValue);
                break;
            case DATETIME:
                if (stringValue.endsWith("Z")) {
                    stringValue = stringValue.replace("Z", "");
                }
                value = Timestamp.valueOf(LocalDateTime.parse(stringValue));
                break;
            case DECIMAL:
                value = Double.valueOf(stringValue);
                break;
            case INTEGER:
                value = Integer.valueOf(stringValue);
                break;
            case VARCHAR:
                value = stringValue;
                break;
            case TEXT:
                value = stringValue;
                break;
            case JSON_BLOB:
                value = stringValue;
                break;
        }
        return value;
    }
    
}

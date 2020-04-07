package com.mbajdowski.utils;

import java.util.Map;

public class MapToStringHelper {
    public static String toString(Map<String, String> map){
        StringBuffer sb = new StringBuffer();
        map.forEach((k, v) -> sb.append(k).append(" -> ").append(v).append(System.lineSeparator()));
        return sb.toString();
    }
}

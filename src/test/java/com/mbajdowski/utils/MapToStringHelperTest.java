package com.mbajdowski.utils;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MapToStringHelperTest {

    @Test
    void toStringShouldConvertMapToFormattedString() {
        Map inputMap = new HashMap<String, String>(){{
            put("PROPERTY1", "Description1");
            put("PROPERTY2", "Description2");
        }};
        String expected1 = "PROPERTY1 -> Description1" + System.lineSeparator();
        String expected2 = "PROPERTY2 -> Description2" + System.lineSeparator();

        String result = MapToStringHelper.toString(inputMap);

        assertTrue(result.contains(expected1));
        assertTrue(result.contains(expected2));
    }
}
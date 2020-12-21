package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ruby
 * @date 2020/12/20 23:25
 */
public class CountTools {
    public static Map<String, Integer> lineToMap(BufferedReader reader) throws IOException {
        String line;
        List<Map<String, Integer>> mapList = new ArrayList<>();
        while ((line = reader.readLine()) != null) {
            mapList.add(lineToMap(line));
        }
        return MapListReduce(mapList);
    }

    public static Map<String, Integer> lineToMap(String line) {
        Map<String, Integer> map = new HashMap<>();
        if (line == null) {
            return map;
        }
        String[] strings = line.split(" ");
        for (String s : strings) {
            map.merge(s, 1, Integer::sum);
        }
        return map;
    }

    public static Map<String, Integer> MapListReduce(List<Map<String, Integer>> mapList) {
        Map<String, Integer> result = new HashMap<>();
        for (Map<String, Integer> map : mapList) {
            map.forEach((k, v) -> result.merge(k, v, Integer::sum));
        }
        return result;
    }
}

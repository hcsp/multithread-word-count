package com.github.hcsp.multithread;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description:
 * @author: HuFan
 * @time: 2020/2/34:09 下午
 **/
public class Utils {
    static Map<String, Integer> countFile(File file) throws IOException {
        Map<String, Integer> result = new ConcurrentHashMap<>();
        List<String> lines = Files.readAllLines(file.toPath());
        for (String line : lines) {
            String[] split = line.split("\\s+");
            for (String word : split) {
                int count = result.getOrDefault(word, 0) + 1;
                result.put(word, count);
            }
        }
        return result;
    }

    static Map<String, Integer> mergeMaps(Map<String, Integer> map1, Map<String, Integer> map2) {
        Set<String> set = map1.keySet();
        set.addAll(map2.keySet());

        Map<String, Integer> result = new HashMap<>();
        for (String key : set) {
            result.put(key, map1.getOrDefault(key, 0) + map2.getOrDefault(key, 0));
        }
        return result;
    }

    static void mergeIntoFirstMap(Map<String, Integer> map1, Map<String, Integer> map2) {
        for (String key : map2.keySet()) {
            map1.put(key, map1.getOrDefault(key, 0) + map2.get(key));
        }
    }

}

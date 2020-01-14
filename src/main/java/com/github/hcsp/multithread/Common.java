package com.github.hcsp.multithread;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Common {
    public static Map<String, Integer> countOneFile(File file) throws IOException {
        return Files.readAllLines(file.toPath())
                .stream()
                .map(line -> line.split("\\s+"))
                .flatMap(Arrays::stream)
                .collect(Collectors.groupingBy(word -> word, Collectors.summingInt(word -> 1)));
    }

    public static Map<String, Integer> mergeMaps(Map<String, Integer> map1, Map<String, Integer> map2) {
        Set<String> keys = new HashSet<>();
        keys.addAll(map1.keySet());
        keys.addAll(map2.keySet());

        Map<String, Integer> result = new HashMap<>();

        for (String key : keys) {
            result.put(key, map1.getOrDefault(key, 0) + map2.getOrDefault(key, 0));
        }
        return result;
    }

    public static void mergeToMap(Map<String, Integer> originalMap, Map<String, Integer> newMap) {
        for (String newKey : newMap.keySet()) {
            originalMap.put(newKey, originalMap.getOrDefault(newKey, 0) + newMap.get(newKey));
        }
    }
}

package com.github.hcsp.multithread;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class Util {

    public static <K> Map<K, Integer> mergeTwoMap(Map<K, Integer> map1, Map<K, Integer> map2) {
        Map<K, Integer> result = new HashMap();
        Set<K> set = new HashSet(map1.keySet());
        set.addAll(map2.keySet());
        for (K k : set) {
            result.put(k, map1.getOrDefault(k, 0) + map2.getOrDefault(k, 0));
        }
        return result;
    }

    public static Map<String, Integer> countWordFromOneFile(File file) throws IOException {
        List<String> strings = Files.readAllLines(file.toPath());
        Map<String, Integer> map = new HashMap<>();
        for (String s : strings) {
            String[] words = s.split("\\s+");
            for (String word : words) {
                map.put(word, map.getOrDefault(word, 0) + 1);
            }
        }
        return map;
    }

    public static <K> Map<K, Integer> mergeMapsFromList(List<Map<K, Integer>> list) {
        Map<K, Integer> result = new HashMap<>();
        for (Map<K, Integer> map : list) {
            for (K k : map.keySet()) {
                result.put(k, result.getOrDefault(k, 0) + map.get(k));
            }
        }
        return result;
    }

}

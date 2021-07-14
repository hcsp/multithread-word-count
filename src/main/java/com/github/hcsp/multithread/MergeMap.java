package com.github.hcsp.multithread;

import java.util.Map;

public class MergeMap {
    public static Map<String, Integer> merge(Map<String, Integer> targetMap, Map<String, Integer> sourceMap) {
        sourceMap.keySet().forEach(key -> {
            if (targetMap.containsKey(key)) {
                targetMap.put(key, targetMap.get(key) + sourceMap.get(key));
            } else {
                targetMap.put(key, sourceMap.get(key));
            }
        });
        return targetMap;
    }
}

package com.github.hcsp.multithread;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommonUtils {
    public static Map<String, Integer> mergeWorkerResults(List<Map<String, Integer>> workerResults) {
        Map<String, Integer> result = new HashMap<>();
        for (Map<String, Integer> workResult : workerResults) {
            for (Map.Entry<String, Integer> entry : workResult.entrySet()) {
                result.put(entry.getKey(), result.getOrDefault(entry.getKey(), 0) + entry.getValue());
            }
        }
        return result;
    }
}

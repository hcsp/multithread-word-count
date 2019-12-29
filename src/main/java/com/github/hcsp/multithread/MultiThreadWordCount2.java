package com.github.hcsp.multithread;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class MultiThreadWordCount2 {
    private static ExecutorService executorService;

    public static Map<String, Integer> count(int threadNum, List<File> files) throws FileNotFoundException {
        Map<String, Integer> result = new HashMap<>();
        List<Future<Map<String, Integer>>> futures = new LinkedList<>();
        executorService = Executors.newFixedThreadPool(threadNum);
        for (File file : files) {
            Future<Map<String, Integer>> future = executorService.submit(new Callable<Map<String, Integer>>() {
                @Override
                public Map<String, Integer> call() throws Exception {
                    return StatisticsPerFile(file);
                }
            });
            futures.add(future);
        }
        for (Future<Map<String, Integer>> future : futures) {
            try {
                mergeMap(result, future.get());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    private static void mergeMap(Map<String, Integer> result, Map<String, Integer> stringIntegerMap) {
        for (String key : stringIntegerMap.keySet()) {
            result.put(key, result.getOrDefault(key, 0) + stringIntegerMap.get(key));
        }
    }

    public static Map<String, Integer> StatisticsPerFile(File file) throws IOException {
        Map<String, Integer> result = new HashMap<>();
        BufferedReader eachLine = new BufferedReader(new FileReader(file));
        String line = null;
        while ((line = eachLine.readLine()) != null) {
            String[] words = line.split(" ");
            for (String word : words) {
//                    result.put(word, result.getOrDefault(word, 0) + 1);
                if (result.containsKey(word)) {
                    result.put(word, result.get(word) + 1);
                } else {
                    result.put(word, 1);
                }
            }
        }
        return result;
    }
}

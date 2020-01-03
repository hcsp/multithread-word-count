package com.github.hcsp.multithread;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class MultiThreadWordCount1 {
    public static Map<String, Integer> count(int threadNum, List<File> files) throws FileNotFoundException {
        Map<String, Integer> result = new HashMap<>();
        List<Future<Map<String, Integer>>> futures = new LinkedList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(threadNum);
        for (File file : files) {
            Future<Map<String, Integer>> future = executorService.submit(() -> countSingleFile(file));
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

    static void mergeMap(Map<String, Integer> result, Map<String, Integer> stringIntegerMap) {
        for (String key : stringIntegerMap.keySet()) {
            result.put(key, result.getOrDefault(key, 0) + stringIntegerMap.get(key));
        }
    }

    /* 统计单个文件单词个数 */
    static Map<String, Integer> countSingleFile(File file) throws IOException {
        Map<String, Integer> result = new HashMap<>();
        BufferedReader eachLine = new BufferedReader(new FileReader(file));
        String line = null;
        while ((line = eachLine.readLine()) != null) {
            String[] words = line.split(" ");
            for (String word : words) {
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

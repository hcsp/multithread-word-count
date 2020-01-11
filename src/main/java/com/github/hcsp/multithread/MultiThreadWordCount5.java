package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MultiThreadWordCount5 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    //    public static Map<String, Integer> count(int threadNum, List<File> files) {
    //        return null;
    //    }
    private static List<Map<String, Integer>> results = new ArrayList<>();

    public static Map<String, Integer> count(int threadNum, List<File> files) throws ExecutionException, InterruptedException {
        Map<String, Integer> countResult = new HashMap<>();
        files.parallelStream().forEach(file -> {
            try {
                countResult(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        for (Map<String, Integer> result : results) {
            mergeResulttoFinal(result, countResult);
        }
        return countResult;
    }

    private static void countResult(File file) throws IOException {
        Map<String, Integer> result = new HashMap<>();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String line = "";
        while ((line = bufferedReader.readLine()) != null) {
            String[] split = line.split(" ");
            for (String word : split) {
                if (result.containsKey(word)) {
                    result.put(word, result.get(word) + 1);
                } else {
                    result.put(word, 1);
                }
            }
        }
        results.add(result);
    }

    private static void mergeResulttoFinal(Map<String, Integer> waitforMerge, Map<String, Integer> countResult) {
        for (Map.Entry<String, Integer> entry : waitforMerge.entrySet()) {
            String word = entry.getKey();
            int i = countResult.getOrDefault(word, 0) + entry.getValue();
            countResult.put(word, i);
        }
    }
}

package com.github.hcsp.multithread;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class MultiThreadWordCount6 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws ExecutionException, InterruptedException {
        List<Map<String, Integer>> fileCounts = new ArrayList<>();
        files.parallelStream().forEach(file -> {
            Map<String, Integer> result = new HashMap<>();
            List<String> allLines = null;
            try {
                allLines = Files.readAllLines(file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            for (String line : allLines) {
                String[] words = line.split(" ");
                Arrays.asList(words).forEach(word -> {
                    int count = result.getOrDefault(word, 0);
                    result.put(word, ++count);
                });
            }
            fileCounts.add(result);
        });

        // 汇总结果
        Map<String, Integer> result = new HashMap<>();
        for (Map<String, Integer> item : fileCounts) {
            merge(item, result);
        }
        return result;
    }


    private static Map<String, Integer> merge(Map<String, Integer> map1, Map<String, Integer> map2) {
        map1.keySet().forEach(key -> {
            if (map2.containsKey(key)) {
                map2.put(key, map2.get(key) + map1.get(key));
            } else {
                map2.put(key, map1.get(key));
            }
        });
        return map2;
    }


}

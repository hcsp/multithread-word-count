package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class MultiThreadWordCount1 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    private static ExecutorService threadPool = null;
    private static List<Future<Map<String, Integer>>> futures = null;

    public static Map<String, Integer> count(int threadNum, List<File> files) throws FileNotFoundException, ExecutionException, InterruptedException {
        threadPool = Executors.newCachedThreadPool();
        futures = new ArrayList<>();
        for (int j = 0; j < files.size(); j++) {
            BufferedReader reader = new BufferedReader(new FileReader(files.get(j)));

            for (int i = 0; i < threadNum; i++) {
                Map<String, Integer> result = new HashMap<>();
                futures.add(threadPool.submit(() -> {
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        String[] words = line.split(" ");
                        for (String word : words) {
                            result.put(word, result.getOrDefault(word, 0) + 1);
                        }
                    }
                    return result;
                }));
            }
        }
        Map<String, Integer> result = new HashMap<>();
        for (Future<Map<String, Integer>> future : futures) {
            for (Map.Entry<String, Integer> entry : future.get().entrySet()) {
                int mergeResult = result.getOrDefault(entry.getKey(), 0) + entry.getValue();
                result.put(entry.getKey(), mergeResult);
            }
        }
        return result;
    }

}

package com.github.hcsp.multithread;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class MultiThreadWordCount1 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws InterruptedException, ExecutionException, IOException {
        Map<String, Integer> result = new HashMap<>();
        for (File file: files) {
            Map<String, Integer> res = count(threadNum, file);
            for (Map.Entry<String, Integer> entry: res.entrySet()) {
                String word = entry.getKey();
                result.put(word, result.getOrDefault(word, 0) + entry.getValue());
            }
        }
        return result;
    }

    public static Map<String, Integer> count(int threadNum, File file) throws IOException, ExecutionException, InterruptedException {
        Map<String, Integer> result = new HashMap<>();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();
        ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);

        for (int i = 0; i < threadNum; i++) {
            futures.add(threadPool.submit(() -> {
                Map<String, Integer> res = new HashMap<>();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    String[] words = line.split(" ");
                    for (String word: words) {
                        res.put(word, res.getOrDefault(word, 0) + 1);
                    }
                }
                return res;
            }));
        }

        for (Future<Map<String, Integer>> future: futures) {
            for (Map.Entry<String, Integer> entry: future.get().entrySet()) {
                String word = entry.getKey();
                result.put(word, result.getOrDefault(word, 0) + entry.getValue());
            }
        }

        return result;
    }
}

package com.github.hcsp.multithread;

import java.io.File;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.*;

public class MultiThreadWordCount4 {

    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws ExecutionException, InterruptedException {
        Map<String, Integer> results = new HashMap<>();
        ExecutorService threadPool = Executors.newFixedThreadPool(files.size());
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();
        for (File file : files) {
            Future<Map<String, Integer>> future = threadPool.submit(new FileFuture(file));
            futures.add(future);
        }
        // 接收结果
        for (Future<Map<String, Integer>> item : futures) {
            Map<String, Integer> result = item.get();
            result.keySet().forEach(key -> {
                if (results.containsKey(key)) {
                    results.put(key, results.get(key) + result.get(key));
                } else {
                    results.put(key, result.get(key));
                }
            });
        }
        return results;
    }

    private static class FileFuture implements Callable<Map<String, Integer>> {
        private File file;

        FileFuture(File file) {
            this.file = file;
        }

        @Override
        public Map<String, Integer> call() throws Exception {
            Map<String, Integer> wordCount = new HashMap<>();
            List<String> allLines = Files.readAllLines(file.toPath());
            for (String line : allLines) {
                String[] words = line.split(" ");
                Arrays.asList(words).forEach(word -> {
                    int count = wordCount.getOrDefault(word, 0);
                    wordCount.put(word, ++count);
                });
            }
            return wordCount;
        }
    }
}

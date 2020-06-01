package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
    Callable/Future，每个文件一个线程，互相不影响。
 */
public class MultiThreadWordCount1 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        long t0 = System.currentTimeMillis();
        ExecutorService pool = Executors.newFixedThreadPool(threadNum);

        List<Future<Map<String, Integer>>> futureList = new ArrayList<>();

        for (File file : files) {
            Future<Map<String, Integer>> mapFuture = pool.submit(new FileWordCount(file));
            futureList.add(mapFuture);
        }

        Map<String, Integer> finallyResult = new HashMap<>();

        for (Future<Map<String, Integer>> future : futureList) {
            try {
                Map<String, Integer> wordCountMap = future.get();
                eachMapToFinallyResult(wordCountMap, finallyResult);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        long t1 = System.currentTimeMillis();
        System.out.println("MultiThreadWordCount1 cost " + (t1 - t0) + " ms");
        return finallyResult;
    }

    static void eachMapToFinallyResult(Map<String, Integer> map, Map<String, Integer> finallyResult) {
        for (Map.Entry<String, Integer> wordEntry : map.entrySet()) {
            finallyResult.put(wordEntry.getKey(), finallyResult.getOrDefault(wordEntry.getKey(), 0)
                    +
                    wordEntry.getValue());
        }
    }

    // 线程执行体，计算单个文件的wordCount
    static class FileWordCount implements Callable<Map<String, Integer>> {
        private final File file;
        private final Map<String, Integer> map = new HashMap<>();

        FileWordCount(File file) {
            this.file = file;
        }

        @Override
        public Map<String, Integer> call() throws Exception {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line = null;
                while ((line = reader.readLine()) != null) {
                    lineWordCount(line, map);
                }
            }
            return map;
        }

        private void lineWordCount(String line, Map<String, Integer> map) {
            String[] words = line.split(" ");
            for (String word : words) {
                map.put(word, map.getOrDefault(word, 0) + 1);
            }
        }
    }
}

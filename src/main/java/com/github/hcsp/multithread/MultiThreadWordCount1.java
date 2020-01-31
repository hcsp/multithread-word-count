package com.github.hcsp.multithread;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class MultiThreadWordCount1 {
    // 使用threadNum个线程，并发统计文件中各单词的数量

    public static Map<String, Integer> count(int threadNum, List<File> files) throws IOException, ExecutionException, InterruptedException {
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();
        ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);

        for (File f : files) {
            BufferedReader reader = new BufferedReader(new FileReader(f));
            futures.add(threadPool.submit(new WorkJob(reader)));
        }

        Map<String, Integer> finalResult = new HashMap<>();
        for (Future<Map<String, Integer>> future : futures) {
            Set<Map.Entry<String, Integer>> entrySet = future.get().entrySet();
            mergedResult(finalResult, entrySet);
        }
        return finalResult;
    }

    private static void mergedResult(Map<String, Integer> finalResult, Set<Map.Entry<String, Integer>> entrySet) {
        for (Map.Entry<String, Integer> entry : entrySet) {
            int mergedResult = finalResult.getOrDefault(entry.getKey(), 0) + entry.getValue();
            finalResult.put(entry.getKey(), mergedResult);
        }
    }

    static class WorkJob implements Callable<Map<String, Integer>> {
        private BufferedReader reader;

        WorkJob(BufferedReader reader) {
            this.reader = reader;
        }

        @Override
        public Map<String, Integer> call() throws Exception {
            Map<String, Integer> wordMap = new HashMap<>();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] words = line.split(" ");
                for (String word : words) {
                    wordMap.put(word, wordMap.getOrDefault(word, 0) + 1);
                }
            }
            return wordMap;
        }
    }
}

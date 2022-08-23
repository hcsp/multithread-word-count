package com.github.hcsp.multithread;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class MultiThreadWordCount2 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws FileNotFoundException, ExecutionException, InterruptedException {
        ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();
        Map<String, Integer> finalResult = new HashMap<>();
        for (File file : files) {
            futures.add(threadPool.submit(new CountTask(new BufferedReader(new FileReader(file)))));
        }
        for (Future<Map<String, Integer>> future : futures) {
            Map<String, Integer> result = future.get();
            mergeResult(finalResult, result);
        }
        return finalResult;
    }

    private static void mergeResult(Map<String, Integer> finalResult, Map<String, Integer> result) {
        for (Map.Entry<String, Integer> entry : result.entrySet()) {
            String word = entry.getKey();
            finalResult.put(word, finalResult.getOrDefault(word, 0) + entry.getValue());
        }
    }

    protected static class CountTask implements Callable {
        private final BufferedReader reader;

        CountTask(BufferedReader reader) {
            this.reader = reader;
        }

        @Override
        public Map<String, Integer> call() throws Exception {
            String line;
            return geWordCountMap(reader);
        }

        static Map<String, Integer> geWordCountMap(BufferedReader reader) throws IOException {
            String line;
            Map<String, Integer> result = new HashMap<>();
            while ((line = reader.readLine()) != null) {
                String[] words = line.split(" ");
                for (String word : words) {
                    result.put(word, result.getOrDefault(word, 0) + 1);
                }
            }
            return result;
        }
    }
}

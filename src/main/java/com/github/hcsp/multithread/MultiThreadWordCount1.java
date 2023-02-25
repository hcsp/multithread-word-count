package com.github.hcsp.multithread;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class MultiThreadWordCount1 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    private static ExecutorService threadPool = null;
    private static List<Future<Map<String, Integer>>> futures = null;
    private static Map<String, Integer> finalResult = null;

    public static Map<String, Integer> count(int threadNum, List<File> files) throws FileNotFoundException {
        threadPool = Executors.newFixedThreadPool(threadNum);
        futures = new ArrayList<>();
        finalResult = new HashMap<>();
        for (File file : files) {
            countSingleFile(file, threadNum);
        }
        for (Future<Map<String, Integer>> future : futures) {
            try {
                Map<String, Integer> singleResult = future.get();
                mergeResult(singleResult);
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        return finalResult;
    }

    public static void countSingleFile(File file, Integer threadNum) throws FileNotFoundException {
        BufferedReader fileReader = new BufferedReader(new FileReader(file));
        for (int i = 0; i < threadNum; i++) {
            Future<Map<String, Integer>> submit = threadPool.submit(new WorkerShop(fileReader));
            futures.add(submit);
        }
    }

    static private void mergeResult(Map<String, Integer> singleResult) {
        for (Map.Entry<String, Integer> entry : singleResult.entrySet()) {
            finalResult.put(entry.getKey(), finalResult.getOrDefault(entry.getKey(), 0) + entry.getValue());
        }
    }

    static class WorkerShop implements Callable<Map<String, Integer>> {
        private final BufferedReader reader;

        WorkerShop(BufferedReader reader) {
            this.reader = reader;
        }

        @Override
        public Map<String, Integer> call() throws Exception {
            String line;
            Map<String, Integer> result = new HashMap<>();
            while ((line = reader.readLine()) != null) {
                countSingleLine(result, line);
            }
            return result;
        }

        public static void countSingleLine(Map<String, Integer> result, String line) {
            String[] words = line.split(" ");
            for (String word : words) {
                result.put(word, result.getOrDefault(word, 0) + 1);
            }
        }
    }


}

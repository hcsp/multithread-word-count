package com.github.hcsp.multithread;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class MultiThreadWordCount1 {
    private static List<Future<HashMap<String, Integer>>> futures = new ArrayList<>();
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws FileNotFoundException, ExecutionException, InterruptedException {
        ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);
        for (File file : files) {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            for (int i = 0; i < threadNum; i++) {
                Future<HashMap<String, Integer>> future = threadPool.submit(() -> getTheCountResult(file, reader));
                futures.add(future);
            }
        }

        return mergeTheFutureAndGetTheResult(futures);
    }

    private static HashMap<String, Integer> mergeTheFutureAndGetTheResult(List<Future<HashMap<String, Integer>>> futures) throws ExecutionException, InterruptedException {
        HashMap<String, Integer> finalResult = new HashMap<>();
        for (Future<HashMap<String, Integer>> future:futures) {
            for (Map.Entry<String, Integer> entry: future.get().entrySet()) {
                finalResult.put(entry.getKey(), finalResult.getOrDefault(entry.getKey(), 0) + entry.getValue());
            }
        }
        return finalResult;
    }

    private static HashMap<String, Integer> getTheCountResult(File file, BufferedReader reader) throws IOException {
        HashMap<String, Integer> result = new HashMap<>();
        String line;
        while ((line = reader.readLine()) != null) {
            String[] words = line.split(" ");
            for (String word : words) {
                result.put(word, result.getOrDefault(word, 0) + 1);
            }
        }
        return result;
    }
}

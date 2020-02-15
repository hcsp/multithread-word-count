package com.github.hcsp.multithread;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class MultiThreadWordCount1 {
    private static AtomicInteger fileIndex = new AtomicInteger(0);
    private static List<Future<HashMap<String, Integer>>> futures = new ArrayList<>();
    private static ConcurrentHashMap<Integer, BufferedReader> commonReader = new ConcurrentHashMap<>();
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws ExecutionException, InterruptedException {
        ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);
        HashMap<String, Integer> finalResult = new HashMap<>();
        for (int i = 0; i < threadNum; i++) {
            Future<HashMap<String, Integer>> future = threadPool.submit(() -> getTheCountResult(files));
            futures.add(future);
        }
        for (Future<HashMap<String, Integer>> future:futures) {
            for (Map.Entry<String, Integer> entry: future.get().entrySet()) {
                finalResult.put(entry.getKey(), finalResult.getOrDefault(entry.getKey(), 0) + entry.getValue());
            }
        }
        return finalResult;
    }

    private static HashMap<String, Integer> getTheCountResult(List<File> files) throws IOException {
        HashMap<String, Integer> result = new HashMap<>();
        while (files.size() > fileIndex.get()) {
            File file = files.get(fileIndex.get());
            BufferedReader reader = commonReader.getOrDefault(fileIndex.get(), new BufferedReader(new FileReader(file)));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] words = line.split(" ");
                for (String word : words) {
                    result.put(word, result.getOrDefault(word, 0) + 1);
                }
            }
            fileIndex.addAndGet(1);
        }
        return result;
    }
}

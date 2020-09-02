package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MultiThreadWordCount1 {
    private final ExecutorService threadPool;

    public MultiThreadWordCount1(int threadNum) {
        threadPool = Executors.newFixedThreadPool(threadNum);
    }

    public ExecutorService getThreadPool() {
        return threadPool;
    }

    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws FileNotFoundException, ExecutionException, InterruptedException {
        MultiThreadWordCount1 multiThreadWordCount1 = new MultiThreadWordCount1(threadNum);
        List<Future<Map<String, Integer>>> allFileWorkResult = new ArrayList<>();
        Map<String, Integer> finalResult = new HashMap<>();
        for (File file : files) {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            allFileWorkResult.addAll(readOneFileAndCountWord(reader, multiThreadWordCount1, threadNum));
        }
        for (Future<Map<String, Integer>> future : allFileWorkResult) {
            Map<String, Integer> resultFromWorker = future.get();
            mergeWorkResultIntoFinalResult(resultFromWorker, finalResult);
        }
        return finalResult;
    }

    private static void mergeWorkResultIntoFinalResult(Map<String, Integer> resultFromWorker, Map<String, Integer> finalResult) {
        for (Map.Entry<String, Integer> entry : resultFromWorker.entrySet()) {
            finalResult.put(entry.getKey(), finalResult.getOrDefault(entry.getKey(), 0) + entry.getValue());
        }
    }

    private static List<Future<Map<String, Integer>>> readOneFileAndCountWord(BufferedReader reader, MultiThreadWordCount1 multiThreadWordCount1, int threadNum) {
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();
        for (int i = 0; i < threadNum; i++) {
            futures.add(multiThreadWordCount1.getThreadPool().submit(() -> {
                String line = null;
                Map<String, Integer> result = new HashMap<>();
                while ((line = reader.readLine()) != null) {
                    String[] words = line.split(" ");
                    for (String word : words) {
                        result.put(word, result.getOrDefault(word, 0) + 1);
                    }
                }
                return result;
            }));
        }
        return futures;
    }
}

package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MultiThreadWordCount1 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws ExecutionException, InterruptedException {

        ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);

        List<Future<Map<String, Integer>>> futures = new ArrayList<>();

        for (File file : files) {
            futures.add(threadPool.submit(() -> workJob(file)));
        }

        Map<String, Integer> finalResult = new HashMap<>();
        for (Future<Map<String, Integer>> workResult : futures) {
            mergeWorkResultIntoFileResult(workResult.get(), finalResult);
        }

        return finalResult;
    }


    private static Map<String, Integer> workJob(File file) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

        Map<String, Integer> result = new HashMap<>();
        String line;
        while ((line = bufferedReader.readLine()) != null) {

            String[] words = line.split(" ");
            for (String word : words) {
                result.put(word, result.getOrDefault(word, 0) + 1);
            }
        }
        return result;
    }


    private static Map<String, Integer> mergeWorkResultIntoFileResult(Map<String, Integer> workResult, Map<String, Integer> fileResult) {


        for (Map.Entry<String, Integer> entrySet : workResult.entrySet()) {
            String word = entrySet.getKey();
            int mergerResult = fileResult.getOrDefault(word, 0) + entrySet.getValue();
            fileResult.put(word, mergerResult);
        }

        return fileResult;
    }
}

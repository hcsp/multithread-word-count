package com.github.hcsp.multithread;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
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

    public static Map<String, Integer> count(int threadNum, List<File> files) throws ExecutionException, InterruptedException, FileNotFoundException {
        ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();
        Map<String, Integer> finalResult = new HashMap<>();
        for (File file : files) {
            BufferedReader br = new BufferedReader(new FileReader(file));
            for (int j = 0; j < threadNum; j++) {
                futures.add(threadPool.submit(() -> {
                            String line;
                            Map<String, Integer> wordCount = new HashMap<>();
                            while ((line = br.readLine()) != null) {
                                String[] words = line.split(" ");
                                for (String word : words) {
                                    wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
                                }
                            }
                            return wordCount;
                        }
                ));
            }
        }
        for (Future<Map<String, Integer>> future : futures) {
            Map<String, Integer> resultFromWorker = future.get();
            new MergeWorker().mergeWorkerResultIntoFinalResult(resultFromWorker, finalResult);
        }
        return finalResult;
    }
}

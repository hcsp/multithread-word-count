package com.github.hcsp.multithread;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;


public class MultiThreadWordCount2 {
    //     使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        Map<String, Integer> wordCounts = new HashMap<>();
        ExecutorService executorService = Executors.newFixedThreadPool(threadNum);
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();
        for (File file : files) {
            Future<Map<String, Integer>> future = executorService.submit(new Callable<Map<String, Integer>>() {
                @Override
                public Map<String, Integer> call() throws Exception {
                    HashMap<String, Integer> wordCounts = new HashMap<>();
                    List<String> allLines = Files.readAllLines(Paths.get(file.getPath()));
                    for (String words : allLines) {
                        String[] wordspaces = words.split("\\s");
                        for (String word : wordspaces) {
                            Integer orDefault = wordCounts.getOrDefault(word, 0) + 1;
                            wordCounts.put(word, orDefault);
                        }
                    }
                    return wordCounts;
                }
            });
            futures.add(future);
        }
        for (Future<Map<String, Integer>> future : futures) {
            try {
                Map<String, Integer> futurewordCounts = future.get();
                futurewordCounts.forEach((key, value) -> wordCounts.put(key, wordCounts.getOrDefault(key, 0) + value));
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

        }
        return wordCounts;
    }
}

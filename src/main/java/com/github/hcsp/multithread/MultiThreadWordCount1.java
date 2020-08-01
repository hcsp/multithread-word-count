package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
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
    private final int threadNum;
    private ExecutorService executorService;

    public MultiThreadWordCount1(int threadNum) {
        this.threadNum = threadNum;
        executorService = Executors.newFixedThreadPool(threadNum);
    }

    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws IOException {
        MultiThreadWordCount1 multiThreadWordCount1 = new MultiThreadWordCount1(threadNum);
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();
        Map<String, Integer> finalResult = new HashMap<>();
        for (File file : files) {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            for (int i = 0; i < threadNum; i++) {
                futures.add(multiThreadWordCount1.executorService.submit(() -> {
                    String line;
                    Map<String, Integer> resMap = new HashMap<>();
                    while ((line = bufferedReader.readLine()) != null) {
                        String[] words = line.split(" ");
                        for (String word : words) {
                            if (resMap.containsKey(word)) {
                                resMap.put(word, resMap.getOrDefault(word, 0) + 1);
                            }
                        }
                    }
                    return resMap;
                }));
            }
            bufferedReader.close();
            for (Future<Map<String, Integer>> future : futures) {
                Map<String, Integer> resultFromWorker = new HashMap<>();
                mergeResult(resultFromWorker, finalResult);
            }
        }
        return finalResult;
    }

    private static void mergeResult(Map<String, Integer> resultFromWorker, Map<String, Integer> finalResult) {
        for (Map.Entry<String, Integer> entry : resultFromWorker.entrySet()) {
            int result = finalResult.getOrDefault(entry.getKey(), 0) + entry.getValue();
            finalResult.put(entry.getKey(), result);
        }
    }


}

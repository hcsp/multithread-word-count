package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class MultiThreadWordCount1 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws FileNotFoundException, ExecutionException, InterruptedException {
        ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);
        List<Future<Map<String, Integer>>> futureList = new ArrayList<>();
        for (File file : files) {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            for (int i = 0; i < threadNum; i++) {
                futureList.add(threadPool.submit(new WorkerJob(bufferedReader) {
//                    @Override
//                    public Map<String, Integer> call() throws Exception {
//                        String line;
//                        Map<String, Integer> wordAndCount = new HashMap<>();
//                        while ((line = bufferedReader.readLine()) != null) {
//                            String[] words = line.split(" ");
//                            for (String word : words) {
//                                wordAndCount.put(word, wordAndCount.getOrDefault(word, 0) + 1);
//                            }
//                        }
//                        return wordAndCount;
//                    }
                }));
            }
        }
        Map<String, Integer> finalResultMap = new HashMap<>();
        for (Future<Map<String, Integer>> future : futureList) {
            Map<String, Integer> resultFromFutureList = future.get();
            mergeResultFromFutureListIntoFinalResultMap(resultFromFutureList, finalResultMap);
        }
        return finalResultMap;
    }

    static class WorkerJob implements Callable<Map<String, Integer>> {
        private BufferedReader bufferedReader;

        WorkerJob(BufferedReader bufferedReader) {
            this.bufferedReader = bufferedReader;
        }

        @Override
        public Map<String, Integer> call() throws Exception {
            String line;
            Map<String, Integer> wordAndCount = new HashMap<>();
            while ((line = bufferedReader.readLine()) != null) {
                String[] words = line.split(" ");
                for (String word : words) {
                    wordAndCount.put(word, wordAndCount.getOrDefault(word, 0) + 1);
                }
            }
            return wordAndCount;
        }
    }

    private static void mergeResultFromFutureListIntoFinalResultMap(Map<String, Integer> resultFromFutureList,
                                                                    Map<String, Integer> finalResultMap) {
        for (Map.Entry<String, Integer> wordEntry : resultFromFutureList.entrySet()) {
            int mergeResult = finalResultMap.getOrDefault(wordEntry.getKey(), 0) + wordEntry.getValue();
            finalResultMap.put(wordEntry.getKey(), mergeResult);
        }
    }
}

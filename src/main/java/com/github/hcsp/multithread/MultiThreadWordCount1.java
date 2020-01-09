package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class MultiThreadWordCount1 {

    private static final int NUM_OF_THREADS = 10;
    private static ExecutorService threadPool = Executors.newFixedThreadPool(NUM_OF_THREADS);

    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws ExecutionException, InterruptedException {
        List<Future<Map<String, Integer>>> futureList = new ArrayList<>();

        for (File file : files) {
            Future<Map<String, Integer>> future = getWorkerResult(file);
            futureList.add(future);
        }

        Map<String, Integer> result = new HashMap<>();

        for (Future<Map<String, Integer>> future : futureList) {
            Map<String, Integer> workerResult = future.get();
            mergeWorkerResult(result, workerResult);
        }

        return result;
    }

    private static Future<Map<String, Integer>> getWorkerResult(File file) {
        return threadPool.submit(new Callable<Map<String, Integer>>() {
            @Override
            public Map<String, Integer> call() throws Exception {
                Map<String, Integer> result = new HashMap<>();
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] words = line.split(" ");
                    for (String word : words) {
                        result.put(word, result.getOrDefault(word, 0) + 1);
                    }
                }

                return result;
            }
        });
    }

    public static void mergeWorkerResult(Map<String, Integer> result, Map<String, Integer> workerResult) {
        for (Map.Entry<String, Integer> entry : workerResult.entrySet()) {
            String word = entry.getKey();
            int mergedCount = result.getOrDefault(word, 0) + entry.getValue();
            result.put(word, mergedCount);
        }
    }

    public static void main(String[] args) throws InterruptedException {

        final int[] i = {0};
//        final int j = 0;

        Thread t = new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                i[0]++;
//                j++;
            }
        };

        t.start();
//        t.join();

        System.out.println(i[0]);


    }

}

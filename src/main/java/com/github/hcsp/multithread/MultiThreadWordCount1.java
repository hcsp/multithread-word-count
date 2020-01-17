package com.github.hcsp.multithread;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class MultiThreadWordCount1 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    private final int threadNum;
    private ExecutorService threadPool;

    public MultiThreadWordCount1(int threadNum) {
        this.threadNum = threadNum;
        this.threadPool = Executors.newFixedThreadPool(threadNum);
    }

    public Map<String, Integer> count(List<File> files) throws FileNotFoundException, ExecutionException, InterruptedException {
        // 开启若干个线程，每个线程去读取文件的一行内容，并将其中的单词统计结果返回
        // 最后，主线程将工作线程返回的结果汇总在一起
        Map<String, Integer> finalResult = new HashMap<>();
        for (File file : files) {
            countFromEachFile(file, finalResult);
        }
        return finalResult;
    }

    private void countFromEachFile(File file, Map<String, Integer> finalResult) throws FileNotFoundException, ExecutionException, InterruptedException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();
        for (int i = 0; i < threadNum; i++) {
            futures.add(threadPool.submit(new workerJob(reader)));
        }
        for (Future<Map<String, Integer>> future : futures) {
            Map<String, Integer> resultFromWorker = future.get();
            mergeWorkerResultIntoFinalResult(resultFromWorker, finalResult);
        }
    }

    private void mergeWorkerResultIntoFinalResult(Map<String, Integer> resultFromWorker, Map<String, Integer> finalResult) {
        for (Map.Entry<String, Integer> entry : resultFromWorker.entrySet()) {
            String word = entry.getKey();
            int mergedResult = finalResult.getOrDefault(word, 0) + entry.getValue();
            finalResult.put(word, mergedResult);    //Map中重复存入时value将被覆盖
        }
    }

    static class workerJob implements Callable<Map<String, Integer>> {
        private BufferedReader reader;

        workerJob(BufferedReader reader) {
            this.reader = reader;
        }

        @Override
        public Map<String, Integer> call() throws Exception {
            String line;
            Map<String, Integer> result = new HashMap<>();
            while ((line = reader.readLine()) != null) {
                String[] words = line.split(" ");
                for (String word : words) {
                    result.put(word, result.getOrDefault(word, 0) + 1);
                }
            }
            return result;
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService threadPool = Executors.newFixedThreadPool(10);

        Future<Integer> future1 = threadPool.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                Thread.sleep(1000);
                return 0;
            }
        });

        Future<String> future2 = threadPool.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                Thread.sleep(1000);
                return "abc";
            }
        });

        Future<Object> future3 = threadPool.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                throw new RuntimeException();
            }
        });

        System.out.println(future1.get());
        System.out.println(future2.get());
        System.out.println(future3.get());
    }
}

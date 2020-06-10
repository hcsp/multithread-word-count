package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class MultiThreadWordCount1 {

    private final int threadNum;
    private ExecutorService threadPool;

    public MultiThreadWordCount1(int threadNum, int threadNum1) {
        this.threadNum = threadNum1;
        threadPool = Executors.newFixedThreadPool(threadNum);
    }

    // 使用threadNum个线程，并发统计文件中各单词的数量
    public Map<String, Integer> count(File file)
            throws FileNotFoundException, ExecutionException, InterruptedException {

        BufferedReader reader = new BufferedReader(new FileReader(file));

        //存储得到的数据
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();
        // 开一个线程池
        for (int i = 0; i < threadNum; ++i) {
            //让其中一个小弟提交一个任务,这里多用多态变成接口了
            futures.add(threadPool.submit(new WorkerJob(reader)));
        }

        Map<String, Integer> finalResult = new HashMap<>();

        for (Future<Map<String, Integer>> future : futures) {
            Map<String, Integer> resultFromWorker = future.get();
            mergeWorkerResultIntoFinalResult(resultFromWorker, finalResult);
        }
        // 合并的代码
        return finalResult;
    }

    private void mergeWorkerResultIntoFinalResult(Map<String, Integer> resultFromWorker,
                                                  Map<String, Integer> finalResult) {
        for (Map.Entry<String, Integer> entry : resultFromWorker.entrySet()) {
            int mergedResult = finalResult.getOrDefault(entry.getKey(), 0) + entry.getValue();
            finalResult.put(entry.getKey(), mergedResult);
        }
    }

    static class WorkerJob implements Callable<Map<String, Integer>> {
        private BufferedReader reader;

        WorkerJob(BufferedReader reader) {
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

}






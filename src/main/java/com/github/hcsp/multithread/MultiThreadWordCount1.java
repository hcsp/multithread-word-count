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
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MultiThreadWordCount1 {
    /**
     * 使用threadNum个线程，并发统计文件中各单词的数量
     *
     * @param threadNum 线程个数
     * @param files     文件列表
     * @return Map(单词计数增加)
     * key 单词
     * value 单词计数
     */
    public static Map<String, Integer> count(int threadNum, List<File> files) throws FileNotFoundException,
            ExecutionException, InterruptedException {
        Map<String, Integer> map_aggregate = new HashMap<>();
        // 新建ThreadPool
        ThreadPoolExecutor pool = new ThreadPoolExecutor(threadNum, threadNum,
                                                         0L, TimeUnit.MILLISECONDS,
                                                         new LinkedBlockingQueue<Runnable>());
        for (File file : files) {
            // 遍历每个文件
            Map<String, Integer> map_a_file = countFile(pool, file);
            // map_a_file 汇总至 map_aggregate
            mergeWorkerResultIntoFinalResult(map_a_file, map_aggregate);
        }
        return map_aggregate;
    }

    /**
     * 读取单个文件的单词，存入Map
     *
     * @param pool 线程池
     * @param file File文件
     * @return 目标 Map
     */
    public static Map<String, Integer> countFile(ThreadPoolExecutor pool, File file) throws FileNotFoundException,
            ExecutionException, InterruptedException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        // 得到最大线程数
        final int maximumPoolSize = pool.getMaximumPoolSize();
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();
        for (int i = 0; i < maximumPoolSize; i++) {
            // 使用多线程建立Map
            futures.add(pool.submit(new WorkerJob(br)));
        }
        // Future<Map<String, Integer>> 转 Map<String, Integer>
        Map<String, Integer> finalResult = new HashMap<>();
        for (Future<Map<String, Integer>> future : futures) {
            Map<String, Integer> resultsFromWorker = future.get();  // .get得到返回值
            // resultsFromWorker 汇总至 finalResult
            mergeWorkerResultIntoFinalResult(resultsFromWorker, finalResult);
        }
        return finalResult;
    }

    /**
     * 将源Map加入目标Map( 单词计数增加 )
     * key 单词
     * value 单词计数
     *
     * @param resultsFromWorker 源Map
     * @param finalResult       目标Map
     */
    private static void mergeWorkerResultIntoFinalResult(Map<String, Integer> resultsFromWorker,
                                                         Map<String, Integer> finalResult) {
        for (Map.Entry<String, Integer> entry : resultsFromWorker.entrySet()) {
            int mergedResult = finalResult.getOrDefault(entry.getKey(), 0) + entry.getValue();
            finalResult.put(entry.getKey(), mergedResult);
        }
    }

    /**
     * 实现Callable接口的类
     */
    static class WorkerJob implements Callable<Map<String, Integer>> {
        private BufferedReader br;

        WorkerJob(BufferedReader br) {
            this.br = br;
        }

        /**
         * 将一行文字提取word的 Map
         *
         * @return 一行文字的word的 Map  key: word  value:计数
         */
        @Override
        public Map<String, Integer> call() throws Exception {
            String line;
            Map<String, Integer> result = new HashMap<>();
            while ((line = br.readLine()) != null) {
                String[] words = line.split(" ");
                for (String word : words) {
                    result.put(word, result.getOrDefault(word, 0) + 1);
                }
            }
            return result;
        }
    }

}

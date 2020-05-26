package com.github.hcsp.multithread;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Future与线程池
 */
public class MultiThreadWordCount2 {
    //使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();
        for (File file : files) {
            Future<Map<String, Integer>> future = threadPool.submit(new WordCount(file));
            futures.add(future);
        }

        Map<String, Integer> finalResult = new HashMap<>();
        for (Future<Map<String, Integer>> future : futures) {
            try {
                Map<String, Integer> result = future.get();
                Utils.mergeSourceMapToDest(finalResult, result);
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        threadPool.shutdown();
        return finalResult;
    }

    static class WordCount implements Callable<Map<String, Integer>> {
        File file;

        WordCount(File file) {
            this.file = file;
        }

        @Override
        public Map<String, Integer> call() throws Exception {
            return Utils.countOneFile(file);
        }
    }
}

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

public class MultiThreadWordCount4 {
    // 使用 threadNum 个线程，并发统计文件中各单词的数量
    // 使用 Future 与线程池实现
    public static Map<String, Integer> count(int threadNum, List<File> files) throws ExecutionException, InterruptedException {
        ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();
        Map<String, Integer> finalCountResult = new HashMap<>();

        for (File file : files) {
            futures.add(threadPool.submit(new WorkJob(file)));
        }
        for (Future<Map<String, Integer>> future : futures) {
            CountUtil.mergeSingleCountResultToFinalCountResult(future.get(), finalCountResult);
        }

        return finalCountResult;
    }

    private static class WorkJob implements Callable<Map<String, Integer>> {
        File file;

        private WorkJob(File file) {
            this.file = file;
        }

        @Override
        public Map<String, Integer> call() {
            return CountUtil.getCountResultFromSingleFile(file);
        }
    }
}
